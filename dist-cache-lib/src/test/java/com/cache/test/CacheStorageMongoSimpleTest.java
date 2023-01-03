package com.cache.test;

import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CacheStorageMongoSimpleTest {
    private static final Logger log = LoggerFactory.getLogger(CacheStorageMongoSimpleTest.class);

    @Test
    public void mongoSimpleTest() {
        log.info("START ------ ");
        String host = "localhost";
        int port = 8081;
        MongoClient mongo = new MongoClient( "localhost" , 8081 );
        //MongoCredential credential;
        //credential = MongoCredential.createCredential("sampleUser", "myDb", "password".toCharArray());
        //mongo.listDatabaseNames();


        log.info("Mongo databases: " + mongo.listDatabaseNames());
        MongoDatabase database = mongo.getDatabase("myDb");

        database.createCollection("dist_items");

        org.bson.Document cacheItem = new Document().append("", "");

        var mongoCacheItems = database.getCollection("dist_items");
        mongoCacheItems.insertOne(cacheItem);

        log.info("Mongo db: " + database.getName());
        mongo.close();
        log.info("END-----");
    }

}
