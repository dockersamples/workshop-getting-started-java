# Hands-on Three: Building container images

## Learning objectives

In this hands-on, you will complete the following objectives:

- Learn how to build a simple container image for the Spring-boot application
- Learn how to use Docker Compose to test the containerized app

Let's get started!

> [!IMPORTANT]
> This workshop is designed to use the Docker Official Images found on Docker Hub. If using this working in a corporate
> environment, these images may not be available.
> Look for additional instructions on where equivalent images may be found within your organization.


## Segment One: Containerizing the Java app

There are several ways to package and deploy Java-based applications into container images. In this segment, we'll start with the simplest: build using native tooling and simply packaging the JAR file into a container image.

To build container images, we're going to use a `Dockerfile`, which is basically the instruction set on how to build an image.

From there, we will then build the image and then run it!

1. At the root of this project, create a file named `Dockerfile` (no file extension) with the following contents:

    ```dockerfile
    # Swap the image after the FROM to use your org's image
    FROM azul/zulu-openjdk:21.0.6-21.40-jre
    COPY target/*.jar ./app.jar
    CMD ["java", "-jar", "app.jar"]
    ```
   
    - `FROM azul/zulu-openjdk:21.0.6-21.40-jre` - the image to start from and extend
    - `COPY target/*.jar ./app.jar` - copy the JAR files from the host's `target` directory and put them into the container at `./app.jar`
    - `CMD ["java", "-jar", "app.jar"]` - the default command that should be executed when starting a container from this image

2. Before we can build this image, we need to build our JAR file. Do so by running the following command:

    ```console
    ./mvnw package -DskipTests
    ```

   We're skipping tests here simply to run the build faster. Feel free to remove them to run the full integration test suite.

3. Once the JAR file is built, it's time to package it into a container image. Use the following `docker build` command to do so:

    ```console
    docker build -t java-app .
    ```

   - `-t java-app` - tag (or name) the newly built image as `java-app`
   - `.` - the location of the Dockerfile and other referenced files (a `.` means the current directory)


> [!TIP]
> This build is keeping things _very_ simple. Check out [Spring's Docker Guide](https://spring.io/guides/gs/spring-boot-docker) for advanced capabilities, including splitting dependencies into their own layers, multi-stage builds, and more.


## Segment Two: Testing the containerized app

Now that we have a Dockerfile and we're able to build it, let's make another Compose file that will allow us to test our fully containerized app.

1. At the root of the project, create a new file called `compose-prod.yaml` with the following contents:

      ```yaml
      services:
        server:
          build: ./
          ports:
             - 8080:8080
          depends_on:
             - postgres  # Ensure the database starts before the app
          environment:
             POSTGRES_HOST: postgres # Uses the service name of the database as the host
             POSTGRES_PORT: 5432
             POSTGRES_USER: postgres
             POSTGRES_PASSWORD: postgres 
             POSTGRES_DB: postgres
      
        postgres:
          image: postgres:16-alpine # Swap this with the image for your org
          environment:
             POSTGRES_USER: postgres
             POSTGRES_PASSWORD: postgres 
             POSTGRES_DB: postgres
    ```

    You'll notice there are now _two_ containers defined here. The first one (named `server`) also indicates the image will come from the output of a build.

2. Start the stack by using the following Compose file:

    ```console
    docker compose -f compose-prod.yaml up
    ```
   
    You'll see an image build and then start the stack!

> [!IMPORTANT]
> When using `build` in a Compose file, the image will be built only on the first launch. Add the `--build` flag to rebuild the image at launch.


## Recap

In this hands-on, you accomplished the following:

- Created a basic Dockerfile to package the Java app into a container image
- Created a Compose file that can be used to test the containerized application
