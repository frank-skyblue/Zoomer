package ca.utoronto.utm.mcs;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/*
Please write your tests for the User Microservice in this class. 
*/

public class UserTests {

    @Test
    public void userLoginPass() throws URISyntaxException, IOException, InterruptedException, JSONException {
        // Register User if doesn't exist
        String testUri = "http://localhost:8004/user/register";
        String testBody = "{\"password\":\"123\",\"name\":\"testname\",\"email\":\"testname@email.com\"}";

        HttpRequest supportRequest = HttpRequest.newBuilder()
                .uri(new URI(testUri))
                .POST(HttpRequest.BodyPublishers.ofString(testBody))
                .build();

        HttpClient.newBuilder()
                .build()
                .send(supportRequest, HttpResponse.BodyHandlers.ofString());

        // Login User
        testUri = "http://localhost:8004/user/login";
        testBody = "{\"password\":\"123\",\"email\":\"testname@email.com\"}";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(testUri))
                .POST(HttpRequest.BodyPublishers.ofString(testBody))
                .build();

        HttpResponse response = HttpClient.newBuilder()
                .build()
                .send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals( "{\"status\":\"OK\"}", new JSONObject(response.body().toString()).toString());
    }

    @Test
    public void userLoginFail() throws URISyntaxException, IOException, InterruptedException, JSONException {
        // Register User if doesn't exist
        String testUri = "http://localhost:8004/user/register";
        String testBody = "{\"password\":\"123\",\"name\":\"testname2\",\"email\":\"testname2@email.com\"}";

        HttpRequest supportRequest = HttpRequest.newBuilder()
                .uri(new URI(testUri))
                .POST(HttpRequest.BodyPublishers.ofString(testBody))
                .build();

        HttpClient.newBuilder()
                .build()
                .send(supportRequest, HttpResponse.BodyHandlers.ofString());

        // Login User with the wrong password
        testUri = "http://localhost:8004/user/login";
        testBody = "{\"password\":\"321\",\"email\":\"testname2@email.com\"}";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(testUri))
                .POST(HttpRequest.BodyPublishers.ofString(testBody))
                .build();

        HttpResponse response = HttpClient.newBuilder()
                .build()
                .send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals( "{\"status\":\"FORBIDDEN\"}", new JSONObject(response.body().toString()).toString());
    }

    @Test
    public void userRegisterPass() throws JSONException, IOException, InterruptedException, URISyntaxException {
        // Register User
        String testUri = "http://localhost:8004/user/register";
        String testBody = "{\"password\":\"123\",\"name\":\"testname3\",\"email\":\"testname3@email.com\"}";

        HttpRequest supportRequest = HttpRequest.newBuilder()
                .uri(new URI(testUri))
                .POST(HttpRequest.BodyPublishers.ofString(testBody))
                .build();

        HttpResponse response = HttpClient.newBuilder()
                .build()
                .send(supportRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals( "{\"status\":\"OK\"}", new JSONObject(response.body().toString()).toString());
    }

    @Test
    public void userRegisterFail() throws URISyntaxException, IOException, InterruptedException, JSONException {
        // Register User without an email
        String testUri = "http://localhost:8004/user/register";
        String testBody = "{\"password\":\"123\",\"name\":\"testname4\"}";

        HttpRequest supportRequest = HttpRequest.newBuilder()
                .uri(new URI(testUri))
                .POST(HttpRequest.BodyPublishers.ofString(testBody))
                .build();

        HttpResponse response = HttpClient.newBuilder()
                .build()
                .send(supportRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals( "{\"status\":\"BAD REQUEST\"}", new JSONObject(response.body().toString()).toString());
    }
}
