package ca.utoronto.utm.mcs;

import com.mongodb.BasicDBObject;
import com.mongodb.client.*;
import io.github.cdimascio.dotenv.Dotenv;
import org.bson.Document;
import org.bson.types.ObjectId;



public class MongoDAO {
    private MongoCollection<Document> collection;

    private final String username = "root";
    private final String password = "123456";
    private final String dbName = "trip";
    private final String collectionName = "trips";

    public MongoDAO() {
        Dotenv dotenv = Dotenv.load();
        String addr = dotenv.get("MONGODB_ADDR");
        String uriDb = String.format("mongodb://%s:%s@" + addr + ":27017", username, password);

        try {
            MongoClient mongoClient = MongoClients.create(uriDb);
            MongoDatabase database = mongoClient.getDatabase(this.dbName);
            this.collection = database.getCollection(this.collectionName);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ObjectId insertTripInfo(String driver, String passenger, int startTime) {
        Document doc = new Document();
        doc.put("driver", driver);
        doc.put("passenger", passenger);
        doc.put("startTime", startTime);

        this.collection.insertOne(doc);
        return doc.getObjectId("_id");
    }

    public void updateTripFromUid(String id, int distance, int endTime, String timeElapsed, Double discount, Double totalCost, Double driverPayout) {
        BasicDBObject query = new BasicDBObject();
        query.put("_id", new ObjectId(id));

        // Create documents
        BasicDBObject distance_doc = new BasicDBObject();
        distance_doc.put("distance", distance);

        BasicDBObject endTime_doc = new BasicDBObject();
        endTime_doc.put("endTime", endTime);

        BasicDBObject timeElapsed_doc = new BasicDBObject();
        timeElapsed_doc.put("timeElapsed", timeElapsed);

        BasicDBObject discount_doc = new BasicDBObject();
        discount_doc.put("discount", discount);

        BasicDBObject totalCost_doc = new BasicDBObject();
        totalCost_doc.put("totalCost", totalCost);

        BasicDBObject driverPayout_doc = new BasicDBObject();
        driverPayout_doc.put("driverPayout", driverPayout);

        // Update documents
        BasicDBObject updateDistance = new BasicDBObject();
        updateDistance.put("$set", distance_doc);
        this.collection.updateOne(query, updateDistance);

        BasicDBObject updateEndTime = new BasicDBObject();
        updateEndTime.put("$set", endTime_doc);
        this.collection.updateOne(query, updateEndTime);

        BasicDBObject updateTimeElapsed = new BasicDBObject();
        updateTimeElapsed.put("$set", timeElapsed_doc);
        this.collection.updateOne(query, updateTimeElapsed);

        BasicDBObject updateDiscount = new BasicDBObject();
        updateDiscount.put("$set", discount_doc);
        this.collection.updateOne(query, updateDiscount);

        BasicDBObject updateTotalCost = new BasicDBObject();
        updateTotalCost.put("$set", totalCost_doc);
        this.collection.updateOne(query, updateTotalCost);

        BasicDBObject updateDriverPayout = new BasicDBObject();
        updateDriverPayout.put("$set", driverPayout_doc);
        this.collection.updateOne(query, updateDriverPayout);
    }

    public FindIterable<Document> getTrip(String uid) {
        BasicDBObject query = new BasicDBObject();
        query.put("passenger", uid);
        return this.collection.find(query);
    }
}
