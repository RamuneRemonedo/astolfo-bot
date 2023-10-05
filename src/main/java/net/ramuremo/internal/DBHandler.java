package net.ramuremo.internal;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

public class DBHandler {
    private static MongoClient client;

    public static void connect(String uri) {
        client = MongoClients.create(uri);
    }

    public static MongoDatabase getDatabase() {
        return client.getDatabase("astolfo-bot");
    }
}
