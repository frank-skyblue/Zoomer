package ca.utoronto.utm.mcs;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;

public class Gateway implements HttpHandler {

    public HashMap<Integer, String> errorMap;

    public Gateway() {
        errorMap = new HashMap<>();
        errorMap.put(200, "OK");
        errorMap.put(400, "BAD REQUEST");
        errorMap.put(403, "FORBIDDEN");
        errorMap.put(404, "NOT FOUND");
        errorMap.put(405, "METHOD NOT ALLOWED");
        errorMap.put(500, "INTERNAL SERVER ERROR");
    }

    public void handle(HttpExchange r) {
        try {
            String[] splitUrl = r.getRequestURI().getPath().split("/");
            String serviceRoute = splitUrl[1];

            String req_uri = r.getRequestURI().getPath();
            String req_params = (r.getRequestURI().getQuery() == null) ? "" : "?" + r.getRequestURI().getRawQuery();

            if (serviceRoute.equals("location")) {
                req_uri = "http://locationmicroservice:8000" + req_uri + req_params;
            }
            else if (serviceRoute.equals("trip")) {
                req_uri = "http://tripinfomicroservice:8000" + req_uri + req_params;
            }
            else if (serviceRoute.equals("user")) {
                req_uri = "http://usermicroservice:8000" + req_uri + req_params;
            }
            else {
                this.sendStatus(r, 400);
            }

            switch (r.getRequestMethod()) {
                case "GET" -> {
                    // making the get request
                    try {
                        HttpRequest request = HttpRequest.newBuilder()
                                .uri(new URI(req_uri))
                                .GET()
                                .build();

                        HttpResponse response = HttpClient.newBuilder()
                                .build()
                                .send(request, HttpResponse.BodyHandlers.ofString());

                        if (String.valueOf(response.statusCode()).equals("200")) {
                            this.sendResponse(r, new JSONObject(response.body().toString()), 200);
                        } else {
                            this.sendStatus(r, response.statusCode());
                        }
                    }
                    catch (Exception e){
                        this.sendStatus(r, 500);
                    }
                }
                case "PATCH" -> {
                    // making the patch request
                    try {

                        // Get request body
                        String body = Utils.convert(r.getRequestBody());
                        JSONObject deserialized = new JSONObject(body);
                        String requestBody = deserialized.toString();

                        HttpRequest request = HttpRequest.newBuilder()
                                .uri(new URI(req_uri))
                                .method("PATCH", HttpRequest.BodyPublishers.ofString(requestBody))
                                .build();

                        HttpResponse response = HttpClient.newBuilder()
                                .build()
                                .send(request, HttpResponse.BodyHandlers.ofString());

                        if (String.valueOf(response.statusCode()).equals("200")) {
                            this.sendResponse(r, new JSONObject(response.body().toString()), 200);
                        } else {
                            this.sendStatus(r, response.statusCode());
                        }
                    }
                    catch (Exception e){
                        this.sendStatus(r, 500);
                    }
                }
                case "POST" -> {
                    // making the post request
                    try {

                        // Get request body
                        String body = Utils.convert(r.getRequestBody());
                        JSONObject deserialized = new JSONObject(body);
                        String requestBody = deserialized.toString();

                        HttpRequest request = HttpRequest.newBuilder()
                                .uri(new URI(req_uri))
                                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                                .build();

                        HttpResponse response = HttpClient.newBuilder()
                                .build()
                                .send(request, HttpResponse.BodyHandlers.ofString());

                        if (String.valueOf(response.statusCode()).equals("200")) {
                            this.sendResponse(r, new JSONObject(response.body().toString()), 200);
                        } else {
                            this.sendStatus(r, response.statusCode());
                        }
                    }
                    catch (Exception e){
                        this.sendStatus(r, 500);
                    }
                }
                case "PUT" -> {
                    // making the patch request
                    try {

                        // Get request body
                        String body = Utils.convert(r.getRequestBody());
                        JSONObject deserialized = new JSONObject(body);
                        String requestBody = deserialized.toString();

                        HttpRequest request = HttpRequest.newBuilder()
                                .uri(new URI(req_uri))
                                .PUT(HttpRequest.BodyPublishers.ofString(requestBody))
                                .build();

                        HttpResponse response = HttpClient.newBuilder()
                                .build()
                                .send(request, HttpResponse.BodyHandlers.ofString());

                        if (String.valueOf(response.statusCode()).equals("200")) {
                            this.sendResponse(r, new JSONObject(response.body().toString()), 200);
                        } else {
                            this.sendStatus(r, response.statusCode());
                        }
                    }
                    catch (Exception e){
                        this.sendStatus(r, 500);
                    }
                }
                case "DELETE" -> {
                    // making the patch request
                    try {

                        // Get request body
                        String body = Utils.convert(r.getRequestBody());
                        JSONObject deserialized = new JSONObject(body);
                        String requestBody = deserialized.toString();

                        HttpRequest request = HttpRequest.newBuilder()
                                .uri(new URI(req_uri))
                                .method("DELETE", HttpRequest.BodyPublishers.ofString(requestBody))
                                .build();

                        HttpResponse response = HttpClient.newBuilder()
                                .build()
                                .send(request, HttpResponse.BodyHandlers.ofString());

                        if (String.valueOf(response.statusCode()).equals("200")) {
                            this.sendResponse(r, new JSONObject(response.body().toString()), 200);
                        } else {
                            this.sendStatus(r, response.statusCode());
                        }
                    }
                    catch (Exception e){
                        this.sendStatus(r, 500);
                    }
                }
                default -> this.sendStatus(r, 405);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void writeOutputStream(HttpExchange r, String response) throws IOException {
        OutputStream os = r.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }

    public void sendResponse(HttpExchange r, JSONObject obj, int statusCode) throws JSONException, IOException {
        obj.put("status", errorMap.get(statusCode));
        String response = obj.toString();
        r.sendResponseHeaders(200, response.length());
        this.writeOutputStream(r, response);
    }

    public void sendStatus(HttpExchange r, int statusCode) throws JSONException, IOException {
        JSONObject res = new JSONObject();
        res.put("status", errorMap.get(statusCode));
        String response = res.toString();
        r.sendResponseHeaders(statusCode, response.length());
        this.writeOutputStream(r, response);
    }

    public void sendStatus(HttpExchange r, int statusCode, boolean hasEmptyData) throws JSONException, IOException {
        JSONObject res = new JSONObject();
        res.put("status", errorMap.get(statusCode));
        res.put("data", new JSONObject());
        String response = res.toString();
        r.sendResponseHeaders(statusCode, response.length());
        this.writeOutputStream(r, response);
    }

    public boolean validateFields(JSONObject JSONRequest, ArrayList<String> stringFields, ArrayList<String> integerFields) {
        try {
            return validateFieldsHelper(JSONRequest,stringFields, String.class) && validateFieldsHelper(JSONRequest, integerFields, Integer.class);
        } catch (JSONException e) {
            System.err.println("Caught Exception: " + e.getMessage());
            return false;
        }
    }

    // check if fields are in JSONRequest and make sure they're of type classOfFields
    private boolean validateFieldsHelper(JSONObject JSONRequest, ArrayList<String> fields, Class<?> classOfFields) throws JSONException {
        for (String field : fields) {
            if (!(JSONRequest.has(field) && JSONRequest.get(field).getClass().equals(classOfFields))) {
                return false;
            }
        }
        return true;
    }
}
