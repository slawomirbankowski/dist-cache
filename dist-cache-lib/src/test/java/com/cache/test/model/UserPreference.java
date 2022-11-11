package com.cache.test.model;

import com.cache.api.CacheUtils;

import java.time.LocalDateTime;

// user selection of favourite products
public class UserPreference extends BaseTable {

    public String userLogin;
    public String productName;
    public long totalOrdersCount;
    public String getKey() { return userLogin+"-"+productName; }
    public String getParentKey() { return userLogin; }

    public static UserPreference createPreference(int id, String userLogin, String productName) {
        UserPreference u = new UserPreference();
        u.id = id;
        u.userLogin = userLogin;
        u.productName = productName;
        u.totalOrdersCount = 0;
        return u;
    }
    /** create many standard users */
    public static UserPreference[] createPreferences(User[] users, Product[] products, int objectsCount) {
        UserPreference[] prefs = new UserPreference[objectsCount];
        for (int i=0; i<objectsCount; i++) {
            Product product = products[CacheUtils.randomInt(products.length)];
            User user = users[CacheUtils.randomInt(users.length)];
            prefs[i] = createPreference(i, user.userLogin, product.productName);
        }
        return prefs;
    }

}
