# Workshop: Getting Started - Java

This workshop is intended to help introduce several concepts of Docker, focused around a Java application.


## Getting started

To get started, clone this repo to your local machine and open the project in your preferred IDE. IntelliJ is used in the writeup, but the principles are easily swapped to other IDEs.

The workshop leverages Maven and all resources are available from Maven Central. If your org doesn't allow direct access to Maven Central, adjustments may be needed in.

## Workshop contents

1. [Running, troubleshooting, and connecting to a containerized database](./docs/1-containers.md)
2. [Sharing container configurations in your projects](./docs/2-sharing-container-config.md)
3. [Building a container image](./docs/3-building-images.md)
4. [Securing your container images](./docs/4-securing-images.md)


## The sample application

The application used with this workshop is a fairly simple Spring Boot-based todo application in which todo items are stored in a PostgreSQL database.

There are also a few [Testcontainer](https://testcontainers.com/) integration tests bundled in the project, although they are not the focus of the workshop. 

## Acknowledgements

The sample app is a modified version of the [S123 app from AtomicJar](https://github.com/AtomicJar/S123). 