package com.cache;

/**
 * local cache object contains storages that keeps object for fast read
 * and connects to other distibuted cache through agent system
 *
 * */
public class DistCacheFactory {

    // TODO: change getting instance to define full configuration for cache
    public static DistCacheFactory getInstance() {
        return new DistCacheFactory();
    }

    public static void main(String[] args) {
        System.out.println("START");
    }

}
