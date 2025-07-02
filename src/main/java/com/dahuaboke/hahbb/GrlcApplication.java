package com.dahuaboke.hahbb;

import com.dahuaboke.hahbb.core.config.DataSourceProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
@EnableConfigurationProperties(DataSourceProperties.class)
@EnableAspectJAutoProxy
public class GrlcApplication {

    public static void main(String[] args) {
        SpringApplication.run(GrlcApplication.class, args);
    }

}
