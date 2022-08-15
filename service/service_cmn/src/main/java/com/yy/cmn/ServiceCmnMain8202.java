package com.yy.cmn;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author ice
 * @date 2022/8/2 9:35
 *
 * 数据字典模块
 */
@ComponentScan("com.yy")
@MapperScan(basePackages = "com.yy.cmn.mapper")
@SpringBootApplication
@EnableDiscoveryClient
public class ServiceCmnMain8202 {
    public static void main(String[] args) {
        SpringApplication.run(ServiceCmnMain8202.class, args);
    }
}
