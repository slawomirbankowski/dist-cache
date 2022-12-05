package com.cache.storage;

import com.cache.api.*;
import com.cache.dtos.CacheRowJdbc;
import com.cache.base.CacheStorageBase;
import com.cache.base.DaoBase;

import java.util.*;

/** cache with JDBC connection to any compliant database
 * it would create special table with cache items and index to fast access
 * */
public class JdbcStorage extends CacheStorageBase {

    /** DAO with DBCP to database */
    private final DaoBase dao;
    private final JdbcDialect dialect;

    /** initialize JDBC storage */
    public JdbcStorage(StorageInitializeParameter p) {
        super(p);
        var jdbcUrl = p.cache.getConfig().getProperty(DistConfig.CACHE_STORAGE_JDBC_URL);
        var jdbcDriver = p.cache.getConfig().getProperty(DistConfig.CACHE_STORAGE_JDBC_DRIVER);
        var jdbcUser = p.cache.getConfig().getProperty(DistConfig.CACHE_STORAGE_JDBC_USER, "");
        var jdbcPass = p.cache.getConfig().getProperty(DistConfig.CACHE_STORAGE_JDBC_PASS, "");
        var jdbcDialect = p.cache.getConfig().getProperty(DistConfig.CACHE_STORAGE_JDBC_DIALECT, "");
        // get dialect by driver class and dialect name
        dialect = JdbcDialect.getDialect(jdbcDriver, jdbcDialect);
        log.info(" ========================= Initializing JdbcStorage with URL: " + jdbcUrl + ", dialect: " + dialect);
        var initConnections = p.cache.getConfig().getPropertyAsInt(DistConfig.JDBC_INIT_CONNECTIONS, 2);
        var maxActiveConnections = p.cache.getConfig().getPropertyAsInt(DistConfig.JDBC_MAX_ACTIVE_CONNECTIONS, 10);
        dao = new DaoBase(jdbcUrl, jdbcDriver, jdbcUser, jdbcPass, initConnections, maxActiveConnections);
        initializeConnectionAndCreateTables();
    }
    /** */
    private void initializeConnectionAndCreateTables() {
        var tables = dao.executeSelectQuery(dialect.selectCacheTables);
        if (tables.size() == 0) {
            log.info("Creating distcacheitem TABLE and INDEX");
            int ret = dao.executeAnyQuery(dialect.createDistCacheItemTable);
            dao.executeAnyQuery(dialect.ddlCreateCacheItemIndex);
            log.info("Created distcacheitem TABLE and INDEX: " + ret);
        }
    }

    /** JDBC is external storage */
    public  boolean isInternal() { return false; }

    /** check if object has given key, optional with specific type */
    public boolean contains(String key) {
        var items = dao.executeSelectQuery(dialect.selectFindCacheItems, new Object[] { "%" + key + "%" }, x -> CacheRowJdbc.fromMap(x));
        return !items.isEmpty();
    }

    /** get CacheObject item from JDBC */
    public Optional<CacheObject> getObject(String key) {
        //CacheObject.fromSerialized();
        var items = dao.executeSelectQuery(dialect.selectCacheItemsByKey,
                new Object[] { key }, x -> CacheObjectSerialized.fromMap(x).toCacheObject(cacheSerializer));
        if (items.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(items.get(0));
        }
    }

    /** set object */
    public  Optional<CacheObject> setObject(CacheObject o) {
        log.debug(" CACHE JDBC SET OBJECT !!!!!!!! ");
        CacheObjectSerialized cos = o.serializedFullCacheObject(cacheSerializer);
        // cachekey, cachevalue, objectclassname,
        // inserteddate, cacheguid, lastusedate
        // enddate,createdtimems,objectseq,objsize,acquiretimems,cachemode,cachepriority,groupslist
        var createDate = new java.util.Date();
        var endDate = new java.util.Date(createDate.getTime()+cos.getTimeToLiveMs());
        dao.executeUpdateQuery(dialect.insertUpdateCacheItem, new Object[] {
                cos.getKey(), cos.getObjectInCacheAsString(), cos.getObjectClassName(),
                createDate, getCacheUid(), createDate,
                endDate, cos.getCreatedTimeMs(), cos.getObjectSeq(), cos.getObjSize(), cos.getAcquireTimeMs(),
                cos.getMode().ordinal(), cos.getPriority(), String.join(",", cos.getGroups())
        });
        return Optional.empty();
    }

    /** remove objects in cache storage by keys */
    public void removeObjectsByKeys(List<String> keys) {
        dao.executeUpdateQuery("delete from distcacheitem where cachekey in (?)", new Object[] { "" });
    }
    /** remove object in cache storage by key */
    public void removeObjectByKey(String key) {
        dao.executeUpdateQuery("delete from distcacheitem where cachekey=?", new Object[] { key });
    }
    /** get number of items in cache */
    public int getItemsCount() {
        dao.executeSelectQuerySingle("select sum(objsize) as cnt from distcacheitem").getIntOrZero("cnt");

        return 0;
    }
    /** get number of objects in this cache */
    public int getObjectsCount() {
        return dao.executeSelectQuerySingle("select count(*) as cnt from distcacheitem").getIntOrZero("cnt");
    }
    /** get keys for all cache items */
    public Set<String> getKeys(String containsStr) {
        dao.executeSelectQuery("select cacheKey from distcacheitem", new Object[] { "%" + containsStr + "%" });
        return new HashSet<String>();
    }
    /** get info values */
    public List<CacheObjectInfo> getValues(String containsStr) {

        return new LinkedList<CacheObjectInfo>();
    }
    /** clear caches with given clear cache */
    public int clearCache(int clearMode) {
        //
        return 1;
    }
    /** clear cache contains given partial key */
    public int clearCacheContains(String str) {
        return dao.executeUpdateQuery("delete from distcacheitem where cachekey like ?", new Object[] { "%" + str + "%" });
    }
    /** check cache every X seconds to clear TTL caches */
    public void onTimeClean(long checkSeq) {
        dao.executeSelectQuery("delete from distcacheitem where enddate < now()", new Object[0]);
    }
    public void disposeStorage() {
        if (dao != null) {
            dao.close();
        }
    }
}
