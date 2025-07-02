package com.dahuaboke.grlc.core.config;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import java.util.ArrayList;
import java.util.List;

public class DynamicDataSource extends AbstractRoutingDataSource {

    private ThreadLocal<Key> cache = new ThreadLocal<>();

    private List<Key> Keys = new ArrayList<>();

    @Override
    protected Object determineCurrentLookupKey() {
        try {
            return cache.get();
        } finally {
            cache.remove();
        }
    }

    Key createKey(String group, String name) {
        Key key = new Key(group, name);
        Keys.add(key);
        return key;
    }

    public Key getKey() {
        try {
            return cache.get();
        } finally {
            cache.remove();
        }
    }

    public void setKey(Key key) {
        cache.set(key);
    }

    public void setKey(String group) {
        setKey(group, null);
    }

    public void setKey(String group, String name) {
        Key Key = new Key(group, name);
        cache.set(Key);
    }

    public List<Key> getKeys(String group) {
        return getKeys(group, null);
    }

    public List<Key> getKeys(Key key) {
        assert key != null;
        return getKeys(key.getGroup(), key.getName());
    }

    public List<Key> getKeys(String group, String name) {
        assert group != null;
        List<Key> result = new ArrayList<>();
        for (Key Key : Keys) {
            if (Key.getGroup().equals(group)) {
                if (name != null) {
                    if (name.equals(Key.getName())) {
                        result.add(Key);
                        break;
                    }
                } else {
                    result.add(Key);
                }
            }
        }
        return result;
    }

    public class Key {
        private String group;
        private String name;

        public Key(String group, String name) {
            this.group = group;
            this.name = name;
        }

        public String getGroup() {
            return group;
        }

        public String getName() {
            return name;
        }
    }
}
