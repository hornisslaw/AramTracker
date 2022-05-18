package com.example.aramtracker;

import org.junit.Test;

import static org.junit.Assert.*;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;

import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.List;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void test_insertToMongoDB() {
        try {
            MongoClient mongoClient = new MongoClient(new MongoClientURI("mongodb://localhost:27017"));
            DB database = mongoClient.getDB("TestDatabase");
            DBCollection collectionTest = database.getCollection("test");
            List<Integer> books = Arrays.asList(27464, 747854);
            DBObject person = new BasicDBObject("_id", "oy")
                    .append("name", "Dildo Swaggins")
                    .append("address", new BasicDBObject("street", "123 Fake St")
                            .append("city", "Faketon")
                            .append("state", "MA")
                            .append("zip", 12345))
                    .append("books", books);
            collectionTest.insert(person);

        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void test_selectCollectionFromMongoDB() throws UnknownHostException {
        MongoClient mongoClient = new MongoClient(new MongoClientURI("mongodb://localhost:27017"));
        DB database = mongoClient.getDB("TestDatabase");
        DBCollection collection = database.getCollection("test");
        DBObject query = new BasicDBObject("_id", "jo");
        DBCursor cursor = collection.find(query);
        cursor.one().get("name");
        String name = cursor.one().get("name").toString();

        assertEquals("Jo Bloggs", name);

    }
}