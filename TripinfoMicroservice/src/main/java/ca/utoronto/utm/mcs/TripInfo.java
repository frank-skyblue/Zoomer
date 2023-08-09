package ca.utoronto.utm.mcs;

import com.mongodb.BasicDBObject;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.time.Instant;

import org.bson.types.ObjectId;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.mongodb.client.*;
import org.bson.Document;

public class TripInfo extends Endpoint {

    @Override
    public void handleGet(HttpExchange r) throws IOException, JSONException {
        String[] splitUrl = r.getRequestURI().getPath().split("/");
        if (splitUrl.length != 4) {
            this.sendStatus(r, 400);
            return;
        }

        //GET /trip/passenger/:uid
        if(splitUrl[2].equals("passenger")) {
            JSONObject res1 = new JSONObject();
            String pid = splitUrl[3];
            String resString = "";
            FindIterable<Document> trip = this.dao.getTrip(pid);
//            JSONArray arr = new JSONArray();
            try {
                if (trip != null) {
                    resString = Utils.findIterableToJSONArray(trip).toString();
                    resString = resString.replace("\\","");
                    resString = resString.replace("[\"{", "{");
                    resString = resString.replace("}\"]", "}");
                    resString = resString.replace("{\"$oid\": \"", "");
                    resString = resString.replace("\"},", ",");
                }
            } catch (Exception e) {
                this.sendStatus(r, 500);
                return;
            }
//            arr.put(resString);
//            res1.put("trips", arr);
            res1.put("trips", resString);
            res1.put("status", "OK");
            this.sendResponse(r, res1, 200);
            return;
        }

        // GET /trip/driver/:uid
        else if(splitUrl[2].equals("driver")) {
            JSONObject res2 = new JSONObject();
            String did = splitUrl[3];

            this.sendStatus(r, 400);
            return;
        }

        // GET /trip/driverTime/:_id
        else if(splitUrl[2].equals("driverTime")) {
            JSONObject res3 = new JSONObject();
            String id = splitUrl[3];

            this.sendStatus(r, 400);
            return;
        }

        else {
            this.sendStatus(r, 400);
            return;
        }

    }

    @Override
    public void handlePatch(HttpExchange r) throws IOException, JSONException {
        // check if request url isn't malformed
        String[] splitUrl = r.getRequestURI().getPath().split("/");
        if (splitUrl.length != 3) {
            this.sendStatus(r, 400);
            return;
        }

        String uidString = splitUrl[2];

        String body = Utils.convert(r.getRequestBody());
        JSONObject deserialized = new JSONObject(body);

        int distance = -1;
        int endTime = -1;
        String timeElapsed = "";
        Double discount = -1D;
        Double totalCost = -1D;
        Double driverPayout = -1D;

        // check what values are present
        if (deserialized.has("distance")) {
            if (deserialized.get("distance").getClass() != Integer.class) {
                this.sendStatus(r, 400);
                return;
            }
            distance = deserialized.getInt("distance");
        }

        if (deserialized.has("endTime")) {
            if (deserialized.get("endTime").getClass() != Integer.class) {
                this.sendStatus(r, 400);
                return;
            }
            endTime = deserialized.getInt("endTime");
        }

        if (deserialized.has("timeElapsed")) {
            if (deserialized.get("timeElapsed").getClass() != String.class) {
                this.sendStatus(r, 400);
                return;
            }
            timeElapsed = deserialized.getString("timeElapsed");
        }

        if (deserialized.has("discount")) {
            if (deserialized.get("discount").getClass() != Double.class) {
                this.sendStatus(r, 400);
                return;
            }
            discount = deserialized.getDouble("discount");
        }

        if (deserialized.has("totalCost")) {
            if (deserialized.get("totalCost").getClass() != Double.class) {
                this.sendStatus(r, 400);
                return;
            }
            totalCost = deserialized.getDouble("totalCost");
        }

        if (deserialized.has("driverPayout")) {
            if (deserialized.get("driverPayout").getClass() != Double.class) {
                this.sendStatus(r, 400);
                return;
            }
            driverPayout = deserialized.getDouble("driverPayout");
        }

        // if all the variables are still null then there's no variables in request so return 400
        if (distance == -1 || endTime == -1 || timeElapsed == "" || discount == -1D || totalCost == -1D || driverPayout == -1D) {
            this.sendStatus(r, 400);
            return;
        }

        // check driver payout
        if(driverPayout != totalCost*0.65) {
            this.sendStatus(r, 400);
            return;
        }

        // update db, return 500 if error
        try {
            this.dao.updateTripFromUid(uidString, distance, endTime, timeElapsed, discount, totalCost, driverPayout);
        }
        catch (Exception e) {
            this.sendStatus(r, 500);
            return;
        }

        // return 200 if everything is updated without error
        this.sendStatus(r, 200);
        return;
    }

    @Override
    public void handlePost(HttpExchange r) throws IOException, JSONException {

        // check if request url isn't malformed
        String[] splitUrl = r.getRequestURI().getPath().split("/");
        if (splitUrl.length != 3) {
            this.sendStatus(r, 400);
            return;
        }

        // Handle Request
        if (splitUrl[2].equals("request")) {
            // Get request body
            String body = Utils.convert(r.getRequestBody());
            JSONObject deserialized = new JSONObject(body);

            String uid = null;
            int radius = -1;

            if (deserialized.has("uid")) {
                if (deserialized.get("uid").getClass() != String.class) {
                    this.sendStatus(r, 400);
                    return;
                }
                uid = deserialized.getString("uid");
            }

            if (deserialized.has("radius")) {
                if (deserialized.get("radius").getClass() != Integer.class) {
                    this.sendStatus(r, 400);
                    return;
                }
                radius = deserialized.getInt("radius");
            }

            // One of the fields not provided, bad request
            if (uid == null || radius == -1 ) {
                this.sendStatus(r, 400);
                return;
            }

            // Send request to Location Microservice

            // making the response
            JSONObject resp = new JSONObject();
            try {
                String req_uri = "http://locationmicroservice:8000/location/nearbyDriver/%d?radius=%d";
                req_uri = String.format(req_uri, Integer.parseInt(uid), radius);

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(new URI(req_uri))
                        .GET()
                        .build();

                HttpResponse response = HttpClient.newBuilder()
                        .build()
                        .send(request, HttpResponse.BodyHandlers.ofString());

                if (String.valueOf(response.statusCode()).equals("200")) {
                    JSONObject responseBody = new JSONObject(response.body().toString());
                    JSONArray keys = responseBody.getJSONObject("data").names();
                    resp.put("data", keys);
                }
                else {
                    this.sendStatus(r, 404);
                }
            }
            catch (Exception e){
                this.sendStatus(r, 500);
                return;
            }

            if (!resp.has("data")){
                this.sendStatus(r, 404);
            }

            this.sendResponse(r, resp, 200);
            return;
        }

        // Handle confirm
        else if (splitUrl[2].equals("confirm")) {
            // Get request body
            String body = Utils.convert(r.getRequestBody());
            JSONObject deserialized = new JSONObject(body);

            String driver = null;
            String passenger = null;
            int startTime = -1;

            if (deserialized.has("driver")) {
                if (deserialized.get("driver").getClass() != String.class) {
                    this.sendStatus(r, 400);
                    return;
                }
                driver = deserialized.getString("driver");
            }

            if (deserialized.has("passenger")) {
                if (deserialized.get("passenger").getClass() != String.class) {
                    this.sendStatus(r, 400);
                    return;
                }
                passenger = deserialized.getString("passenger");
            }

            if (deserialized.has("startTime")) {
                if (deserialized.get("startTime").getClass() != Integer.class) {
                    this.sendStatus(r, 400);
                    return;
                }
                startTime = deserialized.getInt("startTime");
            }

            // One of the fields not provided, bad request
            if (driver == null || passenger == null || startTime == -1) {
                this.sendStatus(r, 400);
                return;
            }

            // making the response
            JSONObject resp = new JSONObject();
            JSONObject data = new JSONObject();

            try {
                ObjectId id = this.dao.insertTripInfo(driver, passenger, startTime);
                data.put("_id", id);
            } catch (Exception e) {
                this.sendStatus(r, 400, true);
                return;
            }

            resp.put("data", data);

            this.sendResponse(r, resp, 200);
            return;
        }
        else {
            this.sendStatus(r, 400);
            return;
        }
    }

    @Override
    public void handleDelete(HttpExchange r) throws IOException, JSONException {
        this.sendStatus(r, 200);
        return;
    }
}
