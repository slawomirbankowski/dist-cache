package com.cache.storage;

import com.cache.api.*;
import com.cache.base.CacheStorageBase;
import com.cache.utils.CacheUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.*;

/** cache with local disk - this could be ephemeral
 * this kind of cache should be for larger object without need of often use
 * */
public class LocalDiskStorage extends CacheStorageBase {

    protected static final Logger log = LoggerFactory.getLogger(LocalDiskStorage.class);
    private String filePrefixName;
    /** TODO: init local disk storage */
    public LocalDiskStorage(StorageInitializeParameter p) {
        super(p);
        filePrefixName = initParams.cacheCfg.getProperty(CacheConfig.LOCAL_DISK_PREFIX_PATH, "/tmp/");
    }
    /** Local Disk is external storage */
    public  boolean isInternal() { return false; }
    /** check if object has given key, optional with specific type */
    public boolean contains(String key) {
        return false;
    }
    /** TODO: get item from local disk */
    public Optional<CacheObject> getObject(String key) {
        // try to read object from disk
        try {

            String cacheObjectFileName = filePrefixName + CacheUtils.stringToHex(key) + ".cache";
            java.io.File f = new File(cacheObjectFileName);
            java.io.ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f));
            CacheObject co = (CacheObject)ois.readObject();
            ois.close();

            return Optional.of(co);
        } catch (Exception ex) {
            initParams.cache.addIssue("LocalDiskStorage.getObject", ex);
            return Optional.empty();
        }
    }
    /** write object to local disk to be read later */
    public Optional<CacheObject> setObject(CacheObject o) {
        try {
            o.getKey();
            String expireDateString = CacheUtils.formatDateAsYYYYMMDDHHmmss(new java.util.Date(System.currentTimeMillis() + o.timeToLive()));
            String cacheObjectFileName = filePrefixName + ".EXPDATE" + expireDateString + "." + CacheUtils.stringToHex(o.getKey()) + ".cache";
            // create temporary file with content - object
            java.io.File f = new File(cacheObjectFileName);
            java.io.ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(f));
            // TODO: serialize somehow this object to be written to Disk

            oos.writeObject(o);
            oos.close();
        } catch (Exception ex) {
            initParams.cache.addIssue("LocalDiskStorage.setObject", ex);
        }
        return Optional.empty();
    }
    /** remove objects in cache storage by keys */
    public void removeObjectsByKeys(List<String> keys) {
        try {


        } catch (Exception ex) {
            initParams.cache.addIssue("LocalDiskStorage.removeObjectsByKeys", ex);
        }
    }
    /** remove object in cache storage by key */
    public void removeObjectByKey(String key) {

    }
    /** get number of items in cache */
    public  int getItemsCount() {
        try {
            java.io.File f = new File(filePrefixName);
            File[] files =  f.listFiles();
            return getObjectsCount() + (int)Arrays.stream(files).mapToLong(x -> x.length()).sum() / 1024;
        } catch (Exception ex) {
            return 0;
        }
    }
    /** get number of objects in this cache */
    public int getObjectsCount() {
        try {
            java.io.File f = new File(filePrefixName);
            return f.list().length;
        } catch (Exception ex) {
            return 0;
        }
    }
    /** get keys for all cache items */
    public Set<String> getKeys(String containsStr) {
        // save keys in one text file
        return new HashSet<String>();
    }
    /** get info values */
    public List<CacheObjectInfo> getValues(String containsStr) {
        return new LinkedList<CacheObjectInfo>();
    }
    /** clear caches with given clear cache */
    public int clearCache(int clearMode) {
        try {
            java.io.File f = new File(filePrefixName);
            File[] files =  f.listFiles();


            return 3;
        } catch (Exception ex) {
            return 0;
        }
    }
    /** clear cache contains given partial key */
    public int clearCacheContains(String str) {
        try {
            java.io.File f = new File(filePrefixName);
            File[] files =  f.listFiles();
            String keyHex = CacheUtils.stringToHex(str);
            Arrays.stream(files).filter(x -> x.getName().contains(keyHex)).forEach(fileToDelete -> {
                fileToDelete.delete();
            });
            return 3;
        } catch (Exception ex) {
            return 0;
        }
    }
    /** check cache every X seconds to clear TTL caches */
    public void onTimeClean(long checkSeq) {

    }
}
