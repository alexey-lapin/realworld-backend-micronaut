[![CI](https://github.com/alexey-lapin/realworld-backend-micronaut/workflows/CI/badge.svg)](https://github.com/alexey-lapin/realworld-backend-micronaut/actions)
[![Codecov](https://img.shields.io/codecov/c/gh/alexey-lapin/realworld-backend-micronaut?logo=codecov)](https://codecov.io/gh/alexey-lapin/realworld-backend-micronaut)

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
