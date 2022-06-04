package apitest;

import authentication.Auth;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static io.restassured.RestAssured.*;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class TestTodoAPI {

    /***
     *
     * Testing Postman TODO API
     * Get all Task
     *
     * Expecting HTTP STATUS 200
     *
     */
    @Test
    public void getAllTaskAndExpect200() throws URISyntaxException, IOException, InterruptedException {
        var token = new Auth().authenticate();
        given()
                .header("Content-Type", ContentType.JSON.toString())
                .header("Authorization", "Bearer ".concat(token))
        .when()
                .get("https://api-nodejs-todolist.herokuapp.com/task")
        .then()
                .statusCode(200);
    }

    /***
     *
     * Testing Postman TODO API
     * Add Task
     *
     * Expecting HTTP STATUS 201
     *
     */
    @Test
    public void addTaskAndExpect201() throws URISyntaxException, IOException, InterruptedException {
        var token = new Auth().authenticate();
        var postBody = this.createPostBody();

        given()
                .header("Content-Type", ContentType.JSON.toString())
                .header("Authorization", "Bearer ".concat(token))
        .with()
                .body(postBody)
        .when()
                .post("https://api-nodejs-todolist.herokuapp.com/task")
        .then().log().all()
                .statusCode(201);
    }

    /***
     *
     * Testing Postman TODO API
     * Add Task and verify if really exists
     *
     * Expecting HTTP STATUS 201
     *
     */
    @Test
    public void addTaskAndVerifyByGet() throws URISyntaxException, IOException, InterruptedException {
        var token = new Auth().authenticate();
        var mapper = new ObjectMapper();
        var postBody = this.createPostBody();

        // Insere tarefa
         var response = given()
                .header("Content-Type", ContentType.JSON.toString())
                .header("Authorization", "Bearer ".concat(token))
        .with()
                .body(postBody)
        .when()
                .post("https://api-nodejs-todolist.herokuapp.com/task");

        response.then()
                    .statusCode(201);

        // Trata Json
        var json  = mapper.readTree(response.body().asString());

        var jsonData = json.get("data");
        var createdId = jsonData.get("_id");

        // Verifica se existe a tarefa
        given()
                .header("Content-Type", ContentType.JSON.toString())
                .header("Authorization", "Bearer ".concat(token))
        .when()
                .get("https://api-nodejs-todolist.herokuapp.com/task/".concat(createdId.textValue()))
        .then()
                .statusCode(200);

    }


    /***
     *
     * Testing Postman TODO API
     * Get all Task
     *
     * Expecting HTTP STATUS 200 with list body not equals 10
     *
     */
    @Test
    public void getAllTaskAndExpect200WithListBodyNotEquals10() throws URISyntaxException, IOException, InterruptedException {
        var mapper = new ObjectMapper();
        var token = new Auth().authenticate();
        Integer expectedCount = 20;

        var response = given().log().all().
                                        header("Content-Type", ContentType.JSON.toString()).
                                        header("Authorization", "Bearer ".concat(token)).
                                when().
                                        get("https://api-nodejs-todolist.herokuapp.com/task");

        var json  = mapper.readTree(response.body().asString());

        var jsonData = json.get("data");

        for (var i : this.toList(jsonData.elements())) {
            System.out.println(i.get("description"));
        }

        var jsonCount = json.get("count");
        Integer count = jsonCount.asInt();

        assertNotEquals(expectedCount, count);
    }

    private String createPostBody() throws JsonProcessingException {
        var mapper = new ObjectMapper();
        var body = mapper.createObjectNode();

        body.put("description", "Rest assured");

        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(body);
    }

    private List<JsonNode> toList(Iterator<JsonNode> elements) {
        Iterable<JsonNode> iterable = () -> elements;
        return StreamSupport
                .stream(iterable.spliterator(), false)
                .collect(Collectors.toList());
    }
}
