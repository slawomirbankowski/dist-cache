package com.cache.test.model;

import com.cache.api.CacheUtils;

import java.time.LocalDateTime;

public class WarehouseStock extends BaseTable {

    public String warehouseCode;
    public String productName;
    public long itemsCount;
    public String getKey() { return warehouseCode+"-"+productName; }
    public String getParentKey() { return warehouseCode; }

    public static WarehouseStock createResource(String resourceName, double resourcePrice) {
        WarehouseStock u = new WarehouseStock();
        return u;
    }
    /** create many standard users */
    public static WarehouseStock[] createStocks(Warehouse[] warehouses, Product[] products, int totalItems, int maxItemsPerProduct) {
        WarehouseStock[] objs = new WarehouseStock[totalItems];
        for (int i=0; i<totalItems; i++) {
            Product product = products[CacheUtils.randomInt(products.length)];
            Warehouse warehouse = warehouses[CacheUtils.randomInt(warehouses.length)];
            WarehouseStock obj = new WarehouseStock();
            obj.id = i;
            obj.warehouseCode = warehouse.warehouseCode;
            obj.productName = product.productName;
            obj.itemsCount = CacheUtils.randomInt(maxItemsPerProduct);
            warehouse.totalItemsCount += obj.itemsCount;
            objs[i] = obj;
        }
        return objs;
    }

}
