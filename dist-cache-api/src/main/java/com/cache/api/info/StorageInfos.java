package com.cache.api.info;

import com.cache.api.info.StorageInfo;

import java.util.List;

/** wrapper for storage information list of objects */
public class StorageInfos {
    private List<StorageInfo> storages;
    public StorageInfos(List<StorageInfo> storages) {
        this.storages = storages;
    }
    public List<StorageInfo> getStorages() {
        return storages;
    }
}
