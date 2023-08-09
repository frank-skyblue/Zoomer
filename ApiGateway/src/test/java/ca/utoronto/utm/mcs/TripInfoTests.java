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
Please write your tests for the TripInfo Microservice in this class. 
*/

public class TripInfoTests {
    @Test
    public void tripRequestPass() throws URISyntaxException, IOException, InterruptedException, JSONException {
        // Add a driver to database if it doesn't exist
        String testUri = "http://localhost:8004/location/user";
        String testBody = "{\"uid\":1,\"is_driver\":true}";

        HttpRequest supportRequest = HttpRequest.newBuilder()
                .uri(new URI(testUri))
                .PUT(HttpRequest.BodyPublishers.ofString(testBody))
                .build();

        HttpClient.newBuilder()
                .build()
                .send(supportRequest, HttpResponse.BodyHandlers.ofString());

        // Add road 1 if it doesn't exist
        testUri = "http://localhost:8004/location/road";
        testBody = "{\"roadName\":\"road1\",\"hasTraffic\":false}";

        HttpRequest supportRequest2 = HttpRequest.newBuilder()
                .uri(new URI(testUri))
                .PUT(HttpRequest.BodyPublishers.ofString(testBody))
                .build();

        HttpClient.newBuilder()
                .build()
                .send(supportRequest2, HttpResponse.BodyHandlers.ofString());

        // Add driver location
        testUri = "http://localhost:8004/location/1";
        testBody = "{\"longitude\":56.235,\"latitude\":70.368,\"street\":\"road1\"}";

        HttpRequest supportRequest3 = HttpRequest.newBuilder()
                .uri(new URI(testUri))
                .method("PATCH", HttpRequest.BodyPublishers.ofString(testBody))
                .build();

        HttpClient.newBuilder()
                .build()
                .send(supportRequest3, HttpResponse.BodyHandlers.ofString());

        // Get nearby driver
        testUri = "http://localhost:8004/trip/request";
        testBody = "{\"uid\":\"1\",\"radius\":100000}";

        HttpRequest supportRequest4 = HttpRequest.newBuilder()
                .uri(new URI(testUri))
                .method("POST", HttpRequest.BodyPublishers.ofString(testBody))
                .build();

        HttpResponse response = HttpClient.newBuilder()
                .build()
                .send(supportRequest4, HttpResponse.BodyHandlers.ofString());

        assertEquals( "{\"data\":[\"1\"],\"status\":\"OK\"}", new JSONObject(response.body().toString()).toString());
    }

    @Test
    public void tripRequestFail() throws JSONException, URISyntaxException, IOException, InterruptedException {
        // Get nearby driver that doesn't exist
        String testUri = "http://localhost:8004/trip/request";
        String testBody = "{\"uid\":\"99\",\"radius\":100000}";

        HttpRequest Request = HttpRequest.newBuilder()
                .uri(new URI(testUri))
                .method("POST", HttpRequest.BodyPublishers.ofString(testBody))
                .build();

        HttpResponse response = HttpClient.newBuilder()
                .build()
                .send(Request, HttpResponse.BodyHandlers.ofString());

        assertEquals( "{\"status\":\"NOT FOUND\"}", new JSONObject(response.body().toString()).toString());
    }

    @Test
    public void tripConfirmPass() throws URISyntaxException, IOException, InterruptedException, JSONException {

        // Insert a request into database
        String testUri = "http://localhost:8004/trip/confirm";
        String testBody = "{\"driver\":\"2\",\"passenger\":\"3\",\"startTime\": 12345667}";

        HttpRequest Request = HttpRequest.newBuilder()
                .uri(new URI(testUri))
                .method("POST", HttpRequest.BodyPublishers.ofString(testBody))
                .build();

        HttpResponse response = HttpClient.newBuilder()
                .build()
                .send(Request, HttpResponse.BodyHandlers.ofString());

        assertEquals( 200, response.statusCode());
    }

    @Test
    public void tripConfirmFail() throws IOException, InterruptedException, URISyntaxException {
        // Insert a request into database with the wrong format
        String testUri = "http://localhost:8004/trip/confirm";
        String testBody = "{\"driver\":\"2\",\"passenger\":3,\"startTime\": 12345667}";

        HttpRequest Request = HttpRequest.newBuilder()
                .uri(new URI(testUri))
                .method("POST", HttpRequest.BodyPublishers.ofString(testBody))
                .build();

        HttpResponse response = HttpClient.newBuilder()
                .build()
                .send(Request, HttpResponse.BodyHandlers.ofString());

        assertEquals( 400, response.statusCode());
    }

}
