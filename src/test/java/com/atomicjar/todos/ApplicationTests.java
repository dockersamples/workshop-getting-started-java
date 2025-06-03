package com.atomicjar.todos;

import com.atomicjar.todos.entity.Todo;
import com.atomicjar.todos.repository.TodoRepository;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ApplicationTests {

    @LocalServerPort
    protected Integer localServerPort;
    protected RequestSpecification requestSpecification;

    @Autowired
    private TodoRepository todoRepository;

    @BeforeEach
    void setUp() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        requestSpecification = new RequestSpecBuilder()
                .setPort(localServerPort)
                .addHeader(
                        HttpHeaders.CONTENT_TYPE,
                        MediaType.APPLICATION_JSON_VALUE
                )
                .build();
    }

    @Test
    void listAllTodos() {
        List<Todo> customers = List.of(
                new Todo(null, "Feed all horses", false, 1),
                new Todo(null, "Free all horses", false, 2),
                new Todo(null, "Feel all horses", false, 3)
        );
        todoRepository.saveAll(customers);

        given(requestSpecification)
                .when()
                .get("/todos")
                .then()
                .statusCode(200)
                .body(".", hasSize(3));

        todoRepository.deleteAll();
    }


    @Test
    void getByIDFindsId() {
        Todo feedAllHorses = todoRepository.save(new Todo(null, "Feed all horses", false, 1));

        given(requestSpecification)
                .when()
                .get("/todos/" + feedAllHorses.getId())
                .then()
                .statusCode(200)
                .body("title", equalTo("Feed all horses"));


        todoRepository.deleteAll();
    }

    @Test
    void deleteWorks() {
        Todo feedAllHorses = todoRepository.save(new Todo(null, "Feed all horses", false, 1));

        String id = feedAllHorses.getId();

        given(requestSpecification)
                .when()
                .delete("/todos/" + id)
                .then()
                .statusCode(200);

        assertFalse(todoRepository.existsById(id));
        todoRepository.deleteAll();
    }



}
