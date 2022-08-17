package com.yy.statistics;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

/**
 * 预约统计模块
 *
 * @author ice
 * @date 2022/8/17 11:30
 */
@ComponentScan("com.yy")
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
@EnableDiscoveryClient
@EnableFeignClients("com.yy")
public class ServiceStatisticsMain8209 {
    public static void main(String[] args) {
        SpringApplication.run(ServiceStatisticsMain8209.class, args);
    }
}
