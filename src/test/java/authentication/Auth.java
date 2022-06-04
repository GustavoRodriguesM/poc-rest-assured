package authentication;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class Auth {
    private static final String URL_LOGIN = "https://api-nodejs-todolist.herokuapp.com/user/login";

    public String authenticate() throws URISyntaxException, IOException, InterruptedException {
        var client = HttpClient.newHttpClient();
        var body = this.createBody();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(URL_LOGIN))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        return this.extractToken(response);
    }

    private String extractToken(HttpResponse<String> response) throws JsonProcessingException {
        var mapper = new ObjectMapper();
        var obj = mapper.readTree(response.body());
        return obj.get("token").asText();
    }

    private String createBody() throws JsonProcessingException {
        var mapper = new ObjectMapper();
        var body = mapper.createObjectNode();

        body.put("email", "muh.nurali43@gmail.com");
        body.put("password", "12345678");

        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(body);
    }
}
