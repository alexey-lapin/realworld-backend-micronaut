# AGENTS

Guidance for coding agents working in this repository: verified build and test commands, and the conventions the
code follows.

## Project overview

A Micronaut implementation of the RealWorld API spec:
https://github.com/realworld-apps/realworld/blob/main/specs/api/openapi.yml

## Modules

- `service-bus`: the command and query bus (`Bus`, `CommandHandler`, `QueryHandler`, `Registry`).
- `service-api`: request and response records, `JsonNullable`, and the HTTP operation interfaces.
- `service`: the Micronaut application.
- Gradle conventions live in `gradle/plugins/common`. Dependency versions come from the Micronaut platform catalog
  (`mn.*`) and `gradle/libs.versions.toml` (`libs.*`).

Java 25 throughout, through a Gradle toolchain.

## Build, run, test

Use the wrapper from the repository root.

```bash
./gradlew build             # compile, unit and integration tests, Spotless, all modules
./gradlew run               # run on the JVM
./gradlew nativeRun         # run as a native image; needs GraalVM for JDK 25 as JAVA_HOME
./gradlew test              # unit tests only
./gradlew integrationTest   # integration tests in service/src/integrationTest
./gradlew check             # unit, integration, Spotless
./gradlew spotlessApply     # add license headers, drop unused imports
./gradlew jacocoReport      # coverage across modules and suites
```

A single test:

```bash
./gradlew :service:test --tests "com.github.al.realworld.application.service.SlugServiceTest"
./gradlew :service:integrationTest --tests "com.github.al.realworld.rest.ArticleApiTest"
```

### RealWorld spec

CI gates on the upstream hurl suite. `./gradlew check` does not cover it, so run it after any change to controllers,
DTOs, validation, error handling, status codes or persistence:

```bash
SPEC_REF=$(grep -E '^  SPEC_REF:' .github/workflows/main.yml | awk '{print $2}')
git clone --filter=blob:none --sparse https://github.com/realworld-apps/realworld .realworld-spec   # first time
git -C .realworld-spec sparse-checkout set specs/api                                                  # first time
git -C .realworld-spec fetch origin && git -C .realworld-spec checkout --detach "$SPEC_REF"

# clean first: service/build/libs may hold jars from other branches that the wildcard would match
./gradlew clean :service:shadowJar
java -jar service/build/libs/realworld-backend-micronaut-*.jar &
APP_PID=$!
until curl --fail --silent --output /dev/null http://localhost:8080/health; do sleep 1; done
.github/scripts/hurl-report.sh http://localhost:8080 "$(date +%s)"
kill "$APP_PID"
```

Pass the host without `/api`. Always run the pinned `SPEC_REF`, since an older checkout enforces rules upstream has
dropped.

### Native image

A change can pass every JVM test and still fail in the native image, which forbids unregistered reflection and dynamic
proxies. After changes to serialization, validation, security, Liquibase changelogs or anything reflective, build and
run the binary, then run the spec suite against it:

```bash
JAVA_HOME=/path/to/graalvm-25 ./gradlew :service:nativeCompile -x check
./service/build/native/nativeCompile/realworld-backend-micronaut &
APP_PID=$!
until curl --fail --silent --output /dev/null http://localhost:8080/health; do sleep 1; done
.github/scripts/hurl-report.sh http://localhost:8080 "$(date +%s)"
kill "$APP_PID"
```

Stop any JVM instance first, or the check runs against it instead of the binary.

Reflection hints the libraries do not ship go in `infrastructure/config/GraalConfig.java`. The Liquibase entries there
cover the change types the changelog uses, so a new change type needs a new entry.

## Conventions

### Formatting

- Every Java file carries the MIT header from `src/spotless/mit-license.java`. Spotless adds it and removes unused
  imports; it does not reformat layout.
- Four-space indentation, braces on the same line, no wildcard imports.
- Records, pattern matching, sealed types and `var` where they make the code clearer.
- JSpecify: the API, domain, exception and JDBC packages are `@NullMarked`, and anything that can be null there is
  `@Nullable`. Mark new packages the same way.

### Layering

- `application`: `command` handlers change state, `query` handlers read it. Handlers get the current user from
  `AuthenticationService`, never from the request.
- `domain`: records and repository interfaces, free of framework types.
- `infrastructure`: web controllers and error handling, JDBC entities, repositories and adapters, JSON, security and
  configuration.
- Dependencies point inward: infrastructure, then application, then domain.

### Naming

- Commands and queries are imperative (`CreateArticle`, `GetFeed`), handlers end in `Handler`, results in `Result`.
- Controllers implement the `*Operations` interfaces from `service-api`.

### Micronaut specifics

- Constructor injection with `@RequiredArgsConstructor` and `@Singleton`.
- Transactions use `io.micronaut.transaction.annotation.Transactional`; query handlers are `readOnly = true`.
- API records are `@Serdeable`. Request `Data` components that validation checks are `@Nullable` together with
  `@NotBlank` or `@NotNull`, so a missing field reaches validation and gets a field error instead of failing in
  deserialization.
- Validation messages that contain an apostrophe double it (`can''t`), because the interpolator treats `'` as a quote.
- Persistence is Micronaut Data JDBC with `@MappedEntity` records. Keep SQL identifiers unquoted: Micronaut's H2 dialect
  quotes with backticks, which H2 folds to upper case like unquoted names.
- The compiler checks derived queries and `@Query` parameter bindings, but not table or column names inside native
  SQL. Only executing a query proves it, so cover new SQL with an integration test.
- Mapping between entities, domain records and DTOs is explicit code in the adapters and in the `*Assembler` classes.

### Errors

- Throw the application exceptions from `application/exception` with the response field and message, for example
  `notFound("article", "article [slug=%s] does not exist", slug)`. The handlers in `infrastructure/web` render them.
- The hurl suite pins the error envelope, status codes and field keys. Changing any of them means running the suite.

## Testing

- JUnit with AssertJ. Unit tests in `src/test`, integration tests in `service/src/integrationTest`.
- Integration tests call the API through declarative clients built from the `*Operations` interfaces. A declarative
  client returns `null` for a 404 rather than throwing, so assert the effect, or use the raw `HttpClient` as
  `ErrorResponseApiTest` does.
- Integration tests share one database. Use random names, as `AuthSupport.register()` does, rather than clearing tables.

## When editing

- Add or update tests for behaviour changes.
- Run `./gradlew spotlessApply` and the narrowest relevant tests before finishing, plus the spec suite and the native
  binary when the change calls for them (see above).
- Keep changes small and consistent with the code around them.
