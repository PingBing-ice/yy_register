package com.yy.task;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 定时任务
 * @author ice
 * @date 2022/8/17 9:25
 */
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
@ComponentScan("com.yy")
@EnableScheduling
@EnableDiscoveryClient
public class ServiceTaskMain8208 {
    public static void main(String[] args) {
        SpringApplication.run(ServiceTaskMain8208.class, args);
    }
}
