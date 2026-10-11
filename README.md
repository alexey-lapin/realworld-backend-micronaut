# ![RealWorld Example App using Micronaut](example-logo.png)

> **A Micronaut implementation of the [RealWorld](https://github.com/realworld-apps/realworld) API specification.**
>
> Java 25 and Micronaut 5, shipped as a JVM jar and as GraalVM native binaries.

[![CI](https://github.com/alexey-lapin/realworld-backend-micronaut/actions/workflows/main.yml/badge.svg?branch=master&event=push)](https://github.com/alexey-lapin/realworld-backend-micronaut/actions/workflows/main.yml?query=branch%3Amaster+event%3Apush)
[![Codecov](https://codecov.io/gh/alexey-lapin/realworld-backend-micronaut/branch/master/graph/badge.svg)](https://codecov.io/gh/alexey-lapin/realworld-backend-micronaut)

This is the complete RealWorld backend on [Micronaut](https://micronaut.io/) 5 and Java 25. CI runs the upstream spec
suite against the JVM jar and against the native binary for every platform on every build, and a failing spec file
fails the build. Any RealWorld frontend can use it.

It shows JWT authentication, a command and query bus, Micronaut Data JDBC over read views, Liquibase migrations,
compile-time serialization, and native images built for four platforms.

The demo runs H2 in memory on a free instance. Treat it as a worked reference, not a production deployment.

## Live demo

The application runs on [Render](https://render.com/):

| Resource   | URL                                                         |
|------------|-------------------------------------------------------------|
| api        | https://realworld-backend-micronaut.onrender.com/api        |
| swagger-ui | https://realworld-backend-micronaut.onrender.com/swagger-ui |

It is on a free tier, so the first request after a quiet period takes a few seconds while the instance wakes up.

## How it works

### Stack

- [Micronaut 5](https://docs.micronaut.io/latest/guide/) with the Netty HTTP server
- [Micronaut Data JDBC](https://micronaut-projects.github.io/micronaut-data/latest/guide/#jdbc), which generates
  repository queries at compile time
- [Micronaut Serialization](https://micronaut-projects.github.io/micronaut-serialization/latest/guide/), which
  generates JSON code at compile time, so native images need no reflection metadata for API types
- [Micronaut Security JWT](https://micronaut-projects.github.io/micronaut-security/latest/guide/#jwt), RS256 tokens
  signed with a key generated at startup, BCrypt password hashes
- [Micronaut Liquibase](https://micronaut-projects.github.io/micronaut-liquibase/latest/guide/) for the schema
- [Micronaut Management](https://docs.micronaut.io/latest/guide/#management) for health, info and Prometheus metrics
- [Micronaut OpenAPI](https://micronaut-projects.github.io/micronaut-openapi/latest/guide/) for the Swagger UI
- H2 in memory

### Design

- Three Gradle modules: `service-bus` holds the command and query bus, `service-api` the request and response records
  and the HTTP operation interfaces, and `service` the application.
- Controllers implement the operation interfaces and hand each request record to the bus. The integration tests use
  the same interfaces as declarative HTTP clients.
- Command handlers write through domain repositories. Article and comment reads go through database views that join
  the author, and the repository queries compute `favorited` and `following` for the current user on each request.
- Domain types are Java records. Partial updates use a small `JsonNullable` type to tell an absent field from an
  explicit `null`.
- Every error renders in the spec's `{"errors": {"field": ["message"]}}` envelope with the spec's status codes.

### CI/CD

- Builds the JVM jar and native executables for Linux (amd64, arm64), macOS (arm64) and Windows (amd64)
- Runs unit and integration tests, and uploads coverage to
  [Codecov](https://codecov.io/gh/alexey-lapin/realworld-backend-micronaut)
- Runs the upstream [hurl spec suite](https://github.com/realworld-apps/realworld/tree/main/specs/api) against the jar
  and every native binary, pinned to a commit that Renovate keeps current
- Builds a multi-arch Docker image from the Linux binaries and starts both architectures before publishing to the
  [GitHub Container Registry](https://github.com/alexey-lapin/realworld-backend-micronaut/pkgs/container/realworld-backend-micronaut)
- For every `vX.Y.Z` tag, publishes a
  [GitHub release](https://github.com/alexey-lapin/realworld-backend-micronaut/releases) with the jar and native
  archives, and redeploys the Render demo in the same run

## Getting started

You need Java 25 to build the project. Native builds need GraalVM for JDK 25.

### JVM

```bash
./gradlew run
```

Or download the jar from the [latest release](https://github.com/alexey-lapin/realworld-backend-micronaut/releases/latest):

```bash
java -jar realworld-backend-micronaut-*.jar
```

### Native image

```bash
./gradlew nativeRun
```

Or download the archive for your platform from the
[latest release](https://github.com/alexey-lapin/realworld-backend-micronaut/releases/latest), unpack it and run it.
Linux arm64 archives start with the first release built by this pipeline; v0.2.5 and earlier do not have them.

```bash
./realworld-backend-micronaut
```

### Docker

The image holds the native executable and is published for linux/amd64 and linux/arm64:

```bash
docker run -p 8080:8080 ghcr.io/alexey-lapin/realworld-backend-micronaut:latest
```

Once it is running:

- API: http://localhost:8080/api
- Swagger UI: http://localhost:8080/swagger-ui
- Health: http://localhost:8080/health

The database lives in memory and starts empty on every launch. The schema changed completely in this rewrite, and the
changelog does not migrate data from older versions, so a persistent database from an older release needs a fresh
start.

## Frontend integration

Point any [RealWorld frontend](https://github.com/realworld-apps/realworld) at `http://localhost:8080/api`.
Authentication accepts both the spec's `Token` scheme and `Bearer`.

## Testing

There are three layers: unit tests, integration tests against a running application, and the RealWorld spec itself.

```bash
# Unit tests
./gradlew test

# Integration tests, a separate suite that `test` does not run
./gradlew integrationTest

# Both suites plus Spotless, as CI runs them
./gradlew check

# Coverage across modules and suites
./gradlew jacocoReport
```

To run the spec suite you need [hurl](https://hurl.dev). The suite lives upstream, so check it out into
`.realworld-spec`, the path CI uses, at the revision CI pins:

```bash
SPEC_REF=$(grep -E '^  SPEC_REF:' .github/workflows/main.yml | awk '{print $2}')

# First time only:
git clone --filter=blob:none --sparse https://github.com/realworld-apps/realworld .realworld-spec
git -C .realworld-spec sparse-checkout set specs/api

# Before each run:
git -C .realworld-spec fetch origin && git -C .realworld-spec checkout --detach "$SPEC_REF"

# clean first, so the wildcard below matches only the jar just built
./gradlew clean build
java -jar service/build/libs/realworld-backend-micronaut-*.jar &
APP_PID=$!
until curl --fail --silent --output /dev/null http://localhost:8080/health; do sleep 1; done
.github/scripts/hurl-report.sh http://localhost:8080 "$(date +%s)"
kill "$APP_PID"
```

The script runs each spec file and prints a failure's details. `.realworld-spec` is a separate clone with its own
`.git`, so it is gitignored.

Pass the host without `/api`, because the `.hurl` files add the prefix themselves. And run the pinned `SPEC_REF`
rather than whatever the checkout happens to be on. The spec changes over time: duplicate article titles once had to
return `409` and now must succeed with distinct slugs, so an old checkout fails a correct service. To move to a newer
spec, bump `SPEC_REF` in the workflow and fix whatever turns red.

### Manual requests

`api.http` holds a request for every endpoint, for the IntelliJ IDEA HTTP client. Requests capture the token and the
article slug as they go, so you can run the file top to bottom.

### Load test

`k6-create-articles.js` registers one user, then has 5 virtual users create articles with random tags for 30 seconds.
The run fails unless fewer than 1% of requests fail, more than 99% of status checks pass, and p95 latency stays under
750 ms.

```bash
k6 run k6-create-articles.js
k6 run -e BASE_URL=https://your-api.example.com/api k6-create-articles.js
```
