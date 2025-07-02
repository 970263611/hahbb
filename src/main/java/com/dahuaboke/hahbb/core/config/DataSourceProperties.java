package com.dahuaboke.hahbb.core.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties(prefix = "spring")
public class DataSourceProperties {

    private List<Group> datasource;

    public List<Group> getDatasource() {
        return datasource;
    }

    public void setDatasource(List<Group> datasource) {
        this.datasource = datasource;
    }

    public static class Group {
        private String group;
        private List<Config> datasource;

        public Group() {
        }

        public String getGroup() {
            return group;
        }

        public void setGroup(String group) {
            this.group = group;
        }

        public List<Config> getDatasource() {
            return datasource;
        }

        public void setDatasource(List<Config> datasource) {
            this.datasource = datasource;
        }
    }

    public static class Config {
        private String name;
        private String driverClassName;
        private String url;
        private String username;
        private String password;
        private long maxActive;
        private long initialSize;
        private long maxWait;
        private long minIdle;

        public Config() {
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDriverClassName() {
            return driverClassName;
        }

        public void setDriverClassName(String driverClassName) {
            this.driverClassName = driverClassName;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public long getMaxActive() {
            return maxActive;
        }

        public void setMaxActive(long maxActive) {
            this.maxActive = maxActive;
        }

        public long getInitialSize() {
            return initialSize;
        }

        public void setInitialSize(long initialSize) {
            this.initialSize = initialSize;
        }

        public long getMaxWait() {
            return maxWait;
        }

        public void setMaxWait(long maxWait) {
            this.maxWait = maxWait;
        }

        public long getMinIdle() {
            return minIdle;
        }

        public void setMinIdle(long minIdle) {
            this.minIdle = minIdle;
        }
    }
}
