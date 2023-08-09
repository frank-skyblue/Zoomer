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
Please write your tests for the Location Microservice in this class. 
*/

public class LocationTests {

    @Test
    public void getNearbyDriverPass() throws URISyntaxException, IOException, InterruptedException, JSONException {
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

        // Find nearby driver (itself)
        testUri = "http://localhost:8004/location/nearbyDriver/1?radius=100000";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(testUri))
                .GET()
                .build();

        HttpResponse response = HttpClient.newBuilder()
                .build()
                .send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals( "{\"data\":{\"1\":{\"street\":\"road1\",\"latitude\":70.368,\"longitude\":56.235},\"3\":{\"street\":\"road3\",\"latitude\":70.167,\"longitude\":56.255},\"5\":{\"street\":\"road3\",\"latitude\":70.168,\"longitude\":56.255}},\"status\":\"OK\"}", new JSONObject(response.body().toString()).toString());
    }

    @Test
    public void getNearbyDriverFail() throws URISyntaxException, IOException, InterruptedException, JSONException {

        // Find nearby driver from a non-existing user
        String testUri = "http://localhost:8004/location/nearbyDriver/99?radius=100000";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(testUri))
                .GET()
                .build();

        HttpResponse response = HttpClient.newBuilder()
                .build()
                .send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals( "{\"status\":\"NOT FOUND\"}", new JSONObject(response.body().toString()).toString());
    }

    @Test
    public void getNavigationPass() throws URISyntaxException, IOException, InterruptedException, JSONException {
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

        // Add user to database if it doesn't exist
        testUri = "http://localhost:8004/location/user";
        testBody = "{\"uid\":2,\"is_driver\":false}";

        HttpRequest supportRequest2 = HttpRequest.newBuilder()
                .uri(new URI(testUri))
                .PUT(HttpRequest.BodyPublishers.ofString(testBody))
                .build();

        HttpClient.newBuilder()
                .build()
                .send(supportRequest2, HttpResponse.BodyHandlers.ofString());

        // Add road 1 if it doesn't exist
        testUri = "http://localhost:8004/location/road";
        testBody = "{\"roadName\":\"road1\",\"hasTraffic\":false}";

        HttpRequest supportRequest3 = HttpRequest.newBuilder()
                .uri(new URI(testUri))
                .PUT(HttpRequest.BodyPublishers.ofString(testBody))
                .build();

        HttpClient.newBuilder()
                .build()
                .send(supportRequest3, HttpResponse.BodyHandlers.ofString());

        // Add road 2 if it doesn't exist
        testUri = "http://localhost:8004/location/road";
        testBody = "{\"roadName\":\"road2\",\"hasTraffic\":false}";

        HttpRequest supportRequest4 = HttpRequest.newBuilder()
                .uri(new URI(testUri))
                .PUT(HttpRequest.BodyPublishers.ofString(testBody))
                .build();

        HttpClient.newBuilder()
                .build()
                .send(supportRequest4, HttpResponse.BodyHandlers.ofString());

        // Add driver location
        testUri = "http://localhost:8004/location/1";
        testBody = "{\"longitude\":56.235,\"latitude\":70.368,\"street\":\"road1\"}";

        HttpRequest supportRequest5 = HttpRequest.newBuilder()
                .uri(new URI(testUri))
                .method("PATCH", HttpRequest.BodyPublishers.ofString(testBody))
                .build();

        HttpClient.newBuilder()
                .build()
                .send(supportRequest5, HttpResponse.BodyHandlers.ofString());

        // Add user location
        testUri = "http://localhost:8004/location/2";
        testBody = "{\"longitude\":56.235,\"latitude\":70.369,\"street\":\"road2\"}";

        HttpRequest supportRequest6 = HttpRequest.newBuilder()
                .uri(new URI(testUri))
                .method("PATCH", HttpRequest.BodyPublishers.ofString(testBody))
                .build();

        HttpClient.newBuilder()
                .build()
                .send(supportRequest6, HttpResponse.BodyHandlers.ofString());

        // Add route
        testUri = "http://localhost:8004/location/hasRoute";
        testBody = "{\"roadName1\":\"road1\",\"roadName2\":\"road2\",\"hasTraffic\":false,\"time\":2}";

        HttpRequest supportRequest7 = HttpRequest.newBuilder()
                .uri(new URI(testUri))
                .method("POST", HttpRequest.BodyPublishers.ofString(testBody))
                .build();

        HttpClient.newBuilder()
                .build()
                .send(supportRequest7, HttpResponse.BodyHandlers.ofString());

        // Find nearby driver from a non-existing user
        testUri = "http://localhost:8004/location/navigation/1?passengerUid=2";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(testUri))
                .GET()
                .build();

        HttpResponse response = HttpClient.newBuilder()
                .build()
                .send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals( "{\"data\":{\"route\":[{\"street\":\"road1\",\"is_traffic\":false,\"time\":0},{\"street\":\"road2\",\"is_traffic\":false,\"time\":2}],\"total_time\":2},\"status\":\"OK\"}", new JSONObject(response.body().toString()).toString());
    }

    @Test
    public void getNavigationFail() throws IOException, InterruptedException, JSONException, URISyntaxException {
        // Find nearby driver from a non-existing user
        String testUri = "http://localhost:8004/location/navigation/99?passengerUid=98";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(testUri))
                .GET()
                .build();

        HttpResponse response = HttpClient.newBuilder()
                .build()
                .send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals( "{\"status\":\"NOT FOUND\"}", new JSONObject(response.body().toString()).toString());
    }
}
