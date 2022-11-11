package com.cache.test.dao;

import com.cache.api.CacheUtils;
import com.cache.test.model.BaseTable;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class DatabaseTable<T extends BaseTable> {

    public T[] initialTable;
    public LinkedList<T> obs = new LinkedList<>();
    public HashMap<Integer, T> objsById = new HashMap<>();
    public HashMap<String, T> objsByName = new HashMap<>();

    public DatabaseTable() {
    }
    public T[] getInitialTable() {return initialTable; }
    void setObjects(T[] table) {
        this.initialTable = table;
        for (int i=0; i<table.length; i++) {
            obs.add(table[i]);
            objsById.put(table[i].id, table[i]);
            objsByName.put(table[i].getKey(), table[i]);
        }
    }
    public void insert(T newObj) {
        obs.add(newObj);
        objsById.put(newObj.id, newObj);
        objsByName.put(newObj.getKey(), newObj);
    }
    /** */
    public void insertMany(T[] newObjs) {
        for (T obj: newObjs) {
            insert(obj);
        }
    }
    public T getById(int id) {
        CacheUtils.sleep(20);
        return objsById.get(id);
    }
    public T getByKey(String key) {
        CacheUtils.sleep(40);
        return objsByName.get(key);
    }
    public List<T> search(String str) {
        CacheUtils.sleep(200);
        return objsByName.entrySet()
                .stream()
                .filter(x -> x.getValue().rowSearch(str))
                .map(x -> x.getValue())
                .collect(Collectors.toList());
    }
    public List<T> getByParentKey(String parentKey) {
        CacheUtils.sleep(200);
        return objsByName.entrySet()
                .stream()
                .filter(x -> x.getValue().getParentKey().equals(parentKey))
                .map(x -> x.getValue())
                .collect(Collectors.toList());
    }
    public T getLast() {
        CacheUtils.sleep(40);
        return obs.getLast();
    }

}
