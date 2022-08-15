package com.yy.hosp;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author ice
 * @date 2022/7/29 10:30
 *
 * 医院设置后台管理api接口
 */
@SpringBootApplication
@ComponentScan("com.yy")
@MapperScan(basePackages = "com.yy.hosp.mapper")
@EnableDiscoveryClient
@EnableFeignClients("com.yy")
public class ServiceHospMain8201 {
    public static void main(String[] args) {
        SpringApplication.run(ServiceHospMain8201.class, args);
    }
}
