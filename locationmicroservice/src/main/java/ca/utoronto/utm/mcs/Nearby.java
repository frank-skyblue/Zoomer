package ca.utoronto.utm.mcs;

import org.json.*;
import org.neo4j.driver.*;

import java.io.IOException;
import com.sun.net.httpserver.HttpExchange;

import org.neo4j.driver.Record;

public class Nearby extends Endpoint{

    /**
     * GET /location/nearbyDriver/:uid?radius=
     * @param "uid, radius
     * @return all available nearby drivers
     */

    @Override
    public void handleGet(HttpExchange r) throws IOException, JSONException {
        // get parameters
        String[] params = r.getRequestURI().toString().split("/");
        if(params.length != 4 || params[2].isEmpty()) {
            this.sendStatus(r, 400, true);
            return;
        }
        try{
            String[] content = params[3].split("\\?");

            if(content.length != 2 || content[0].isEmpty() || content[1].isEmpty()) {
                this.sendStatus(r, 400, true);
                return;
            }
            String uid = content[0];
            int radius = Integer.parseInt(content[1].split("=")[1]);
            // get user location
            Result result = this.dao.getUserLocationByUid(uid);
            if(result.hasNext()) {
                JSONObject res = new JSONObject();
                JSONObject res2 = new JSONObject();

                // get all drivers
                Result drivers = this.dao.getDrivers();
                Double d_longitude, d_latitude;
                String d_street, did;
                Result inRadius;

                // for each driver, check if they are
                // in radius, if so, add them to the list
                while(drivers.hasNext()) {
                    Record driver = drivers.next();
                    did = driver.get("n.uid").asString();
                    inRadius = this.dao.inRadius(uid, did);
                    int rad = inRadius.next().get("dist").asInt();
                    if(rad <= radius) {
                        JSONObject data = new JSONObject();
                        d_longitude = driver.get("n.longitude").asDouble();
                        d_latitude = driver.get("n.latitude").asDouble();
                        d_street = driver.get("n.street").asString();
                        data.put("longitude", d_longitude);
                        data.put("latitude", d_latitude);
                        data.put("street", d_street);
                        res.put(did, data);
                    }
                }

                res2.put("status", "OK");
                res2.put("data", res);
                this.sendResponse(r, res2, 200);
                return;
            } else {
                this.sendStatus(r, 404);
                return;
            }

        } catch (Exception e) {
            e.printStackTrace();
            this.sendStatus(r, 500, true);
            return;
        }
    }

}
