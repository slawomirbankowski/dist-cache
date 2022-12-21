package com.cache.test;

import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.client.MongoDatabase;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StorageMongoSimpleTest {
    private static final Logger log = LoggerFactory.getLogger(StorageMongoSimpleTest.class);

    @Test
    public void mongoSimpleTest() {
        log.info("START ------ ");
        String host = "localhost";
        int port = 8081;
        MongoClient mongo = new MongoClient( "localhost" , 8081 );
        MongoCredential credential;
        credential = MongoCredential.createCredential("sampleUser", "myDb",
                "password".toCharArray());
        log.info("Mongo databases: " + mongo.listDatabaseNames());
        MongoDatabase database = mongo.getDatabase("myDb");
        log.info("Mongo db: " + database.getName());
        mongo.close();
        log.info("END-----");
    }

}
