package com.yy.user;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

/**
 * 用户登陆注册模块
 * @author ice
 * @date 2022/8/9 19:54
 */
@SpringBootApplication
@ComponentScan("com.yy")
@MapperScan("com.yy.user.mapper")
@EnableFeignClients(basePackages = "com.yy")
public class ServiceUserApplication8160 {
    public static void main(String[] args) {
        SpringApplication.run(ServiceUserApplication8160.class, args);
    }
}
