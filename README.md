# ![RealWorld Example App using Spring](example-logo.png)

> ### Micronaut Framework codebase containing real world examples (CRUD, auth, advanced patterns, etc) that adheres to the [RealWorld](https://github.com/gothinkster/realworld) spec and API

[![CI](https://github.com/alexey-lapin/realworld-backend-micronaut/workflows/CI/badge.svg)](https://github.com/alexey-lapin/realworld-backend-micronaut/actions)
[![Codecov](https://img.shields.io/codecov/c/gh/alexey-lapin/realworld-backend-micronaut?logo=codecov)](https://codecov.io/gh/alexey-lapin/realworld-backend-micronaut)

This codebase was created to demonstrate a fully fledged fullstack application built with **[Micronaut](https://micronaut.io/)** including CRUD operations, authentication, routing, pagination, and more.

We've gone to great lengths to adhere to the Micronaut community styleguides & best practices.

For more information on how to this works with other frontends/backends, head over to the [RealWorld](https://github.com/gothinkster/realworld) repo.

**Heroku**: https://realworld-backend-micronaut.herokuapp.com/api

# How it works
This application basically uses Micronaut Framework with Java 8 with some other modules known to development community:
- Hibernate 5
- Jackson for JSON
- H2 in memory database
- Jsonwebtoken jjwt

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
