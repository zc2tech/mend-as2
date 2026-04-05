package de.mendelson.util.preferences;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */
/**
 * Cache for the server preferences to prevent database access
 *
 * @author S.Heller
 * @version $Revision: 7 $
 */
public class PreferencesCache {

    private long expireTime = TimeUnit.SECONDS.toMillis(5);

    private final Map<String, String> preferencesMap = Collections.synchronizedMap(new HashMap<String, String>());
    private final Map<String, Long> expireMap = new ConcurrentHashMap<String, Long>();

    public PreferencesCache(long expireTime) {
        this.expireTime = expireTime;
    }

    /**
     * Returns the cached value or null if it has been either expired or the
     * value is not cached
     *
     * @param key Preferences key to get the server preferences value for
     * @return
     */
    public String get(String key) {
        synchronized (this.preferencesMap) {
            String foundValue = this.preferencesMap.get(key);
            if (foundValue == null) {
                return (null);
            } else {
                Long initTime = this.expireMap.get(key);
                if (initTime == null) {
                    //should not happen but this is for data consistency
                    this.preferencesMap.remove(key);
                    return (null);
                } else {
                    //check if found value is still valid
                    long ageInMS = System.currentTimeMillis() - initTime;
                    if (ageInMS < this.expireTime) {
                        //cache hit
                        return (foundValue);
                    } else {
                        //value expired
                        this.preferencesMap.remove(key);
                        this.expireMap.remove(key);
                        return (null);
                    }
                }
            }
        }
    }

    /**
     * Adds a new key value pair to the cache
     */
    public void put(String key, String value) {
        synchronized (this.preferencesMap) {
            this.preferencesMap.put(key, value);
            this.expireMap.put(key, Long.valueOf(System.currentTimeMillis()));
        }
    }

    /**
     * Removes a key from the cache
     */
    public void remove(String key) {
        synchronized (this.preferencesMap) {
            this.preferencesMap.remove(key);
            this.expireMap.remove(key);
        }
    }
    
    /**
     * Clear the cache. This might be required in HA mode if there are multiple
     * nodes working on the same preferences and a request needs to get
     * the current stored value in the database
     */
    public void clear(){
        synchronized (this.preferencesMap) {
            this.preferencesMap.clear();
            this.expireMap.clear();
        }
    }
  
}
