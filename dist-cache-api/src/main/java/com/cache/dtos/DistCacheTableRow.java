package com.cache.dtos;

import java.util.Map;

public class DistCacheTableRow {

    public String table_schema;
    public String table_name;

    public DistCacheTableRow(String table_schema, String table_name) {
        this.table_schema = table_schema;
        this.table_name = table_name;
    }

    public static DistCacheTableRow fromMap(Map<String, Object> map) {
        return new DistCacheTableRow(map.getOrDefault("table_schema", "").toString(),
                map.getOrDefault("table_name", "").toString());
    }
}
