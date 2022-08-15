package com.yy.order;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

/**
 * 用户订单模块
 *
 * @author ice
 * @date 2022/8/13 18:55
 */
@SpringBootApplication
@EnableFeignClients("com.yy")
@ComponentScan("com.yy")
@EnableDiscoveryClient
@MapperScan("com.yy.order.mapper")
public class ServiceOrderMain8207 {
    public static void main(String[] args) {
        SpringApplication.run(ServiceOrderMain8207.class, args);
    }
}
