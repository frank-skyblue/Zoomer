package ca.utoronto.utm.mcs;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.sun.net.httpserver.HttpExchange;

import org.json.JSONObject;
import org.neo4j.driver.*;
import org.neo4j.driver.Record;

public class Navigation extends Endpoint{
    /**
     * GET /location/navigation/:driver?passengerUid=
     * @param "driverUid, passengerUid
     * @return shortest path of navigation
     */
    @Override
    public void handleGet(HttpExchange r) throws IOException, JSONException {
        // get parameters
        String[] params = r.getRequestURI().toString().split("/");
        if(params.length != 4 || params[2].isEmpty()) {
            this.sendStatus(r, 400, true);
            return;
        }
        try {
            String[] content = params[3].split("\\?");

            if(content.length != 2 || content[0].isEmpty() || content[1].isEmpty()) {
                this.sendStatus(r, 400, true);
                return;
            }
            String did = content[0];
            String pid = content[1].split("=")[1];
            Result dRes = this.dao.getUserLocationByUid(did);
            Result pRes = this.dao.getUserLocationByUid(pid);

            if (dRes.hasNext() && pRes.hasNext() ) {
                JSONObject res = new JSONObject();
                JSONObject res2 = new JSONObject();

                // get driver and passenger streets
                String dLoc, pLoc;
                dLoc = dRes.next().get("n.street").asString();
                pLoc = pRes.next().get("n.street").asString();

                // Find the shortest paths
                Result shortestPath = this.dao.shortestPath(dLoc, pLoc);
                Record path = shortestPath.next();

                // Extract information
                int total_time = path.get("total_time").asInt();
                List costs = path.get("costs").asList();
                List paths = path.get("paths").asList();

                // Extract id of the nodes on the path
                ArrayList<Integer> path_id = new ArrayList<>();
                for(int i=0; i<paths.size(); i++) {
                    String node = paths.get(i).toString();
                    int node_id = Integer.parseInt(node.substring(node.indexOf("<") + 1, node.indexOf(">")));
                    path_id.add(node_id);
                }

                String street;
                Boolean is_traffic;
                // Construct "route" array
                ArrayList<Object> route = new ArrayList<>();
                for(int i=0; i<path_id.size(); i++) {
                    Result node = this.dao.getRoadById(path_id.get(i));
                    Record rec = node.next();
                    street = rec.get("n.name").asString();
                    is_traffic = rec.get("n.is_traffic").asBoolean();

                    JSONObject entry = new JSONObject();
                    entry.put("street", street);
                    if(i == 0) {
                        entry.put("time", Math.round(Float.parseFloat(costs.get(i).toString())));
                    } else {
                        entry.put("time", Math.round(Float.parseFloat(costs.get(i).toString()))-Math.round(Float.parseFloat(costs.get(i-1).toString())));
                    }
                    entry.put("is_traffic", is_traffic);
                    route.add(entry);
                }
                res.put("route", route);
                res.put("total_time", total_time);
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
