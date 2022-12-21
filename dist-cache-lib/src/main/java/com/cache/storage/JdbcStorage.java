package com.cache.storage;

import com.cache.api.*;
import com.cache.base.dtos.DistCacheItemRow;
import com.cache.base.CacheStorageBase;
import com.cache.base.DaoBase;
import com.cache.jdbc.JdbcDialect;

import java.util.*;
import java.util.stream.Collectors;

/** cache with JDBC connection to any compliant database
 * it would create special table with cache items and index to fast access
 * */
public class JdbcStorage extends CacheStorageBase {

    /** DAO with DBCP to database */
    private final DaoBase dao;
    /** JDBC dialect with SQL queries for different database management systems */
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
        var tables = dao.executeSelectQuery(dialect.selectCacheTables());
        if (tables.size() == 0) {
            log.info("Creating distcacheitem TABLE and INDEX");
            int ret = dao.executeAnyQuery(dialect.createDistCacheItemTable());
            dao.executeAnyQuery(dialect.createCacheItemIndex());
            log.info("Created distcacheitem TABLE and INDEX: " + ret);
        }
    }

    /** JDBC is external storage */
    public  boolean isInternal() { return false; }

    /** check if object has given key, optional with specific type */
    public boolean contains(String key) {
        var items = dao.executeSelectQuery(dialect.selectFindCacheItems(),
                new Object[] { "%" + key + "%" }, x -> DistCacheItemRow.fromMap(x));
        return !items.isEmpty();
    }

    /** get CacheObject item from JDBC */
    public Optional<CacheObject> getObject(String key) {
        //CacheObject.fromSerialized();
        var items = dao.executeSelectQuery(dialect.selectCacheItemsByKey(),
                new Object[] { key }, x -> CacheObjectSerialized.fromMap(x).toCacheObject(distSerializer));
        if (items.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(items.get(0));
        }
    }

    /** set object */
    public  Optional<CacheObject> setObject(CacheObject o) {
        log.debug(" CACHE JDBC SET OBJECT");
        CacheObjectSerialized cos = o.serializedFullCacheObject(distSerializer);
        var createDate = new java.util.Date();
        var endDate = new java.util.Date(createDate.getTime()+cos.getTimeToLiveMs());
        dao.executeUpdateQuery(dialect.insertUpdateCacheItem(), new Object[] {
                cos.getKey(), cos.getObjectInCache(), cos.getObjectClassName(), // cachekey, cachevalue, objectclassname,
                createDate, getCacheUid(), createDate, //  inserteddate, cacheguid, lastusedate,
                endDate, cos.getCreatedTimeMs(), cos.getObjectSeq(), cos.getObjSize(), cos.getAcquireTimeMs(), //  enddate, createdtimems, objectseq, objsize, acquiretimems,
                cos.getMode().ordinal(), cos.getPriority(), cos.getGroupsList()}); // cachemode, cachepriority, groupslist
        return Optional.empty();
    }

    /** remove objects in cache storage by keys */
    public void removeObjectsByKeys(List<String> keys) {
        var sqlParams = String.join(",", keys.stream().map(x -> "?").collect(Collectors.toList()));
        dao.executeUpdateQuery("delete from distcacheitem where cachekey in (" + sqlParams + ")", keys.toArray());
    }
    /** remove object in cache storage by key */
    public void removeObjectByKey(String key) {
        dao.executeUpdateQuery("delete from distcacheitem where cachekey=?", new Object[] { key });
    }
    /** get number of items in cache */
    public int getItemsCount() {
        return dao.executeSelectQuerySingle("select sum(objsize) as cnt from distcacheitem").getIntOrZero("cnt");
    }
    /** get number of objects in this cache */
    public int getObjectsCount() {
        return dao.executeSelectQuerySingle("select count(*) as cnt from distcacheitem").getIntOrZero("cnt");
    }
    /** get keys for all cache items */
    public Set<String> getKeys(String containsStr) {
        log.info("Get cache keys ");
        var rows = dao.executeSelectQuery("select cacheKey as key from distcacheitem where cachekey like ?", new Object[] { "%" + containsStr + "%" });
        log.info("Got cache keys: " + rows.size());
        return rows.stream().map(x -> ""+x.get("key")).collect(Collectors.toSet());
    }
    /** get info values */
    public List<CacheObjectInfo> getInfos(String containsStr) {
        return getValues(containsStr).stream().map(c -> c.getInfo()).collect(Collectors.toList());
    }
    /** get values of cache objects that contains given String in key */
    public List<CacheObject> getValues(String containsStr) {
        log.info("GET VALUES -=======> " + dialect.selectFindCacheItems());
        var items = dao.executeSelectQuery(dialect.selectFindCacheItems(),
                new Object[] { "%" + containsStr + "%" }, x -> CacheObjectSerialized.fromMap(x).toCacheObject(distSerializer));
        return items.stream().collect(Collectors.toList());
    }
    /** clear caches for given group */
    public int clearCacheForGroup(String groupName) {
        return dao.executeUpdateQuery("delete from distcacheitem where groupslist like ?", new Object[] { "%," + groupName + ",%" });
    }
    /** clear cache contains given partial key */
    public int clearCacheContains(String str) {
        return dao.executeUpdateQuery("delete from distcacheitem where cachekey like ?", new Object[] { "%" + str + "%" });
    }

    /** clear cache by given mode
     * returns estimated of elements cleared */
    public int clearCache(CacheClearMode clearMode) {

        return -1;
    }
    /** check cache every X seconds to clear TTL caches */
    public void onTimeClean(long checkSeq) {
        // dao.executeAnyQuery("delete from distcacheitem where enddate < now()", new Object[0]);
    }
    public void disposeStorage() {
        if (dao != null) {
            dao.close();
        }
    }
}
