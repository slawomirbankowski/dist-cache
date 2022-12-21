package com.cache.api;

import java.time.LocalDateTime;

/** information about storage - object to be serialized to JSON and returned to user via dist-cache application */
public class StorageInfo {
    /** UID for storage */
    private String storageUid;
    private LocalDateTime storageCreatedDate;
    private String storageClassName;
    private int itemsCount;
    private int objectsCount;
    private boolean internal;

    public StorageInfo() {
        this.storageUid = null;
        this.storageCreatedDate = null;
        this.storageClassName = null;
        this.itemsCount = -1;
        this.objectsCount = -1;
        this.internal = true;
    }
    public StorageInfo(String storageUid, LocalDateTime storageCreatedDate, String storageClassName, int itemsCount, int objectsCount, boolean internal) {
        this.storageUid = storageUid;
        this.storageCreatedDate = storageCreatedDate;
        this.storageClassName = storageClassName;
        this.itemsCount = itemsCount;
        this.objectsCount = objectsCount;
        this.internal = internal;
    }

    public String getStorageUid() {
        return storageUid;
    }

    public String getStorageCreatedDate() {
        return storageCreatedDate.toString();
    }

    public String getStorageClassName() {
        return storageClassName;
    }

    public int getItemsCount() {
        return itemsCount;
    }

    public int getObjectsCount() {
        return objectsCount;
    }

    public boolean isInternal() {
        return internal;
    }
}
