# ![RealWorld Example App using Spring](example-logo.png)

> ### Micronaut Framework codebase containing real world examples (CRUD, auth, advanced patterns, etc) that adheres to the [RealWorld](https://github.com/gothinkster/realworld) spec and API

[![CI](https://github.com/alexey-lapin/realworld-backend-micronaut/workflows/CI/badge.svg)](https://github.com/alexey-lapin/realworld-backend-micronaut/actions)
[![Codecov](https://img.shields.io/codecov/c/gh/alexey-lapin/realworld-backend-micronaut?logo=codecov)](https://codecov.io/gh/alexey-lapin/realworld-backend-micronaut)

This codebase was created to demonstrate a fully fledged fullstack application built with **[Micronaut](https://micronaut.io/)** including CRUD operations, authentication, routing, pagination, and more.

We've gone to great lengths to adhere to the Micronaut community styleguides & best practices.

For more information on how to this works with other frontends/backends, head over to the [RealWorld](https://github.com/gothinkster/realworld) repo.

Actual application is accessible on **Heroku**: https://realworld-backend-micronaut.herokuapp.com/api

# How it works
This application basically uses Micronaut Framework with Java 8 with some other modules:
- [Micronaut Data](https://micronaut-projects.github.io/micronaut-data/latest/guide/) with Hibernate
- Json Web Token [jjwt](https://github.com/jwtk/jjwt)
- H2 in memory database

Some other highlights:
- [Liquibase](https://www.liquibase.org/) for the database changes management
- [Micronaut declarative http clients](https://docs.micronaut.io/snapshot/guide/index.html#clientAnnotation) for integration tests
- [Github Actions](https://github.com/alexey-lapin/realworld-backend-micronaut/actions) as CI
- Execution of [Realworld Postman collection](https://github.com/gothinkster/realworld/blob/master/api/Conduit.postman_collection.json) is part of CI
- Deployment on [Heroku](https://realworld-backend-micronaut.herokuapp.com/api) is part of CI
- OpenAPI support with [swagger-ui](https://realworld-backend-micronaut.herokuapp.com/swagger-ui)
- Micronaut built-in [health](https://realworld-backend-micronaut.herokuapp.com/health) and [info](https://realworld-backend-micronaut.herokuapp.com/info) endpoints
- GraalVM support

# Getting started
Java 8 or above is required

    ./gradlew run

To test that it works, open a browser tab at http://localhost:8080/api/tags .  
Alternatively, you can run

    curl http://localhost:8080/api/tags

# Try it out with a RealWorld frontend

The entry point address of the backend API is at http://localhost:8080/api

# Run test

The repository contains a lot of test cases to cover both api test and repository test.

    ./gradlew test
