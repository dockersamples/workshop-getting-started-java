# Hands-on Two: Sharing container configurations in your projects

## Learning objectives

In this hands-on, you will complete the following objectives:

- Learn about Docker Compose to share your container configuration
- Explore methods to use the Testcontainers framework to start containers programatically

Let's get started!

> [!IMPORTANT]
> This workshop is designed to use the Docker Official Images found on Docker Hub. If using this working in a corporate
> environment, these images may not be available.
> Look for additional instructions on where equivalent images may be found within your organization.


## Segment One: Writing a Compose file

In the previous hands-on, you launched a container using the CLI (or GUI). While this works, sending teammates a collection of CLI commands can be quite cumbersome.

That's where Docker Compose comes in!

With Docker Compose, you can define all the containers, volumes, and networks required for your application to run using a YAML-based configuration. 

1. At the root of this project, create a file named `compose.yaml` with the following contents:

    ```yaml
    services:
      postgres:
        image: postgres:16-alpine # Swap this with your org's specific image
        ports:
          - 5432:5432
        environment:
          POSTGRES_PASSWORD: postgres 
    ```

2. If you still have the postgres container running from the previous hands-on, go ahead and remove it now:

    ```console
    docker rm -f postgres
    ```

   The `-f` flag will stop the container first and then remove it (doing both a `docker stop` and a `docker rm`).

3. Launch the stack by using the `docker compose up` command:

    ```console
    docker compose up
    ```

    You'll see the containers start and logs start to stream to the console.

    When you're done, press Ctrl+C to tear it all down!

4. You can also launch the stack and run it in the background, similar to what we did with containers by using the `-d` flag:

    ```console
    docker compose up -d
    ```
   
5. To access the log stream, use the `docker compose logs` command (the `-f` flag will "follow" the log output):

    ```console
    docker compose logs -f
    ```
   
6. When you're done, tear everything down with the `docker compose down` command:

    ```console
    docker compose down
    ```

And... that's it! With this, your team can place a `compose.yaml` at the root of the project and everyone can easily spin up their app's dependencies. And you'll know everyone is on the same version.

> [!TIP]
> When you need to update your database version, simply update the Compose file and everyone on the team will get the update on their next pull and `docker compose up`! No more having to uninstall and reconfigure your machine for a different database.



## Segment Two: Programmatically starting containers

The [Testcontainers SDK](https://testcontainers.com) provides the ability to manage the lifecycle of containers in code. While it's typically used for integration testing, the deep integration into Spring Boot makes it really appealing to launch and easily configure your app.

1. Open the `src/main/resources/application.properties` file and add the following property:

    ```properties
    docker.image.postgres = docker.io/library/postgres:16-alpine
    ```
   
    **Reminder:** change the value to use the PostgreSQL image your organization is using.

2. In the `src/test/java/com/atomicjar/todos` directory, create a `ContainersConfig` class with the following configuration:

    ```java
    package com.atomicjar.todos;
    
    import org.springframework.beans.factory.annotation.Value;
    import org.springframework.boot.devtools.restart.RestartScope;
    import org.springframework.boot.test.context.TestConfiguration;
    import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
    import org.springframework.context.annotation.Bean;
    import org.testcontainers.containers.PostgreSQLContainer;
    import org.testcontainers.utility.DockerImageName;
    
    @TestConfiguration(proxyBeanMethods = false)
    public class ContainersConfig {
    
        @Bean
        @ServiceConnection
        @RestartScope
        PostgreSQLContainer<?> postgreSQLContainer(@Value("${docker.image.postgres}") String postgresImage) {
            return new PostgreSQLContainer<>(
                    DockerImageName.parse(postgresImage).asCompatibleSubstituteFor("postgres")
            );
        }
    
    }
    ```

    This class is defining a Spring Bean, which is going to manage the lifecycle of an actual container. A few explanations:

    - `@Value("${docker.image.postgres}") String postgresImage` - this will inject the value from the `application.properties` file
    - `new PostgreSQLContainer<>(...)` - create a PostgreSQL container, using the specified image
    - `@ServiceConnection` - will use the configuration of the container image to automatically create the JDBC URL or other connection details used by Spring. [More docs here...](https://docs.spring.io/spring-boot/reference/testing/testcontainers.html#testing.testcontainers.service-connections)

3. Now, in the same test package, create a `TestApplication` with the following contents:

    ```java
    package com.atomicjar.todos;

    import org.springframework.boot.SpringApplication;
    
    public class TestApplication {
        public static void main(String[] args) {
            SpringApplication
                    .from(Application::main)
                    .with(ContainersConfig.class)
                    .run(args);
        }
    }
    ```

4. Now, start the app using the following command:

    ```console
    ./mvnw spring-boot:test-run
    ```
   
    One of the things you may see is that the database will bind to unique ports, ensuring you never have a conflict. And the app is automatically configured!

> [!TIP]
> While this example is only using PostgreSQL, there is a large library of [Testcontainer modules](https://testcontainers.com/modules/) to easily plug into your codebase. If you need an image that isn't there, it's also easy to make your own modules! 


## Recap

In this hands-on, you accomplished the following:

- Created a Compose file to easily define and share your container config with your team
- Learned how to use Testcontainer's Spring Boot integration to programmatically define the application's containers

