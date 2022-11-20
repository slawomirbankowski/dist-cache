package com.cache.api;

import java.time.LocalDateTime;

/** information about storage */
public class StorageInfo {
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

    public LocalDateTime getStorageCreatedDate() {
        return storageCreatedDate;
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
