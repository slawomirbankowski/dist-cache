package com.cache.storage;

import com.cache.api.*;
import com.cache.base.CacheStorageBase;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.io.File;
import java.util.*;

/** cache with MongoDB as storage for objects
 *
 * TODO: Implement storage saving cache objects in MongoDB
 * 
 * */
public class MongodbStorage extends CacheStorageBase {

    private String mongoHost;
    private int mongoPort;
    private String mongoDbName;
    private String mongoCollection;
    private MongoClient mongoClient;
    private MongoDatabase mongoDb;
    private MongoCollection<Document> mongoCacheItems;

    /** initialize Redis storage */
    public MongodbStorage(StorageInitializeParameter p) {
        super(p);
        mongoHost = p.cache.getConfig().getProperty(DistConfig.CACHE_STORAGE_MONGODB_HOST);
        mongoPort = p.cache.getConfig().getPropertyAsInt(DistConfig.CACHE_STORAGE_MONGODB_PORT, 8081);
        mongoDbName = p.cache.getConfig().getProperty(DistConfig.CACHE_STORAGE_MONGODB_DATABASE, "distcache");
        mongoCollection = p.cache.getConfig().getProperty(DistConfig.CACHE_STORAGE_MONGODB_COLLECTION, "distcacheitems");
        log.info("Try to start Cache Storage of MongoDB for agent:" + p.cache.getAgent().getAgentGuid() + ", host: " + mongoHost + ", port: " + mongoPort + ", DB: " + mongoDbName + ", collection" + mongoCollection);
        mongoClient = new MongoClient( mongoHost , mongoPort );
        mongoDb = mongoClient.getDatabase(mongoDbName);
        mongoCacheItems = mongoDb.getCollection(mongoCollection);
    }

    /** MongoDB is external storage */
    public  boolean isInternal() { return false; }
    /** returns true if MongoDB is available  */
    public boolean isOperable() {
        try {
            mongoClient.listDatabaseNames();
            return true;
        } catch (Exception ex) {
            return false;
        }
    }
    /** get type of this storage */
    public CacheStorageType getStorageType() {
        return CacheStorageType.mongodb;
    }
    /** check if object has given key, optional with specific type */
    public boolean contains(String key) {
        mongoCacheItems.find(com.mongodb.client.model.Filters.eq("key", key));

        // TODO: check document by key
        return false;
    }
    /** TODO: implement getting document from MongoDB */
    public Optional<CacheObject> getObject(String key) {
        //
        return Optional.empty();
    }
    public  Optional<CacheObject> setObject(CacheObject o) {
        CacheObjectSerialized cos = o.serializedFullCacheObject(distSerializer);
        org.bson.Document cacheItem = new Document()
                .append("key", cos.getKey())
                .append("size", cos.getObjSize())
                .append("mode", cos.getMode().name())
                .append("groups", cos.getGroups())
                .append("value", cos.getObjectInCache())
                .append("priority", cos.getPriority())
                .append("class", cos.getObjectClassName());
        mongoCacheItems.insertOne(cacheItem);
        return Optional.empty();
    }
    /** remove objects in cache storage by keys */
    public void removeObjectsByKeys(Collection<String> keys) {
        // remove objects from MongoDB

    }
    /** remove object in cache storage by key */
    public void removeObjectByKey(String key) {
    }
    /** get number of items in cache */
    public  int getItemsCount() {
        return (int)mongoCacheItems.estimatedDocumentCount();
    }
    /** get number of objects in this cache */
    public int getObjectsCount() {
        return (int)mongoCacheItems.estimatedDocumentCount();
    }
    /** get keys for all cache items */
    public Set<String> getKeys(String containsStr) {
        return new HashSet<String>();
    }
    /** get info values */
    public List<CacheObjectInfo> getInfos(String containsStr) {
        return new LinkedList<CacheObjectInfo>();
    }
    /** get values of cache objects that contains given String in key */
    public List<CacheObject> getValues(String containsStr) {
        return new LinkedList<CacheObject>();
    }
    /** clear caches with given clear cache */
    public int clearCacheForGroup(String groupName) {
        return 1;
    }
    /** clear cache contains given partial key */
    public int clearCacheContains(String str) {
        return 1;
    }
    /** clear cache by given mode
     * returns estimated of elements cleared */
    public int clearCache(CacheClearMode clearMode) {
        return -1;
    }
    /** check cache every X seconds to clear TTL caches */
    public void onTimeClean(long checkSeq) {

    }
}
