package com.yy.oss;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author ice
 * @date 2022/8/11 11:56
 */
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
@ComponentScan("com.yy")
public class ServiceOssMain8205 {
    public static void main(String[] args) {
        SpringApplication.run(ServiceOssMain8205.class, args);
    }
}
