package com.yy.msm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

/**
 * 短信模块
 * @author ice
 * @date 2022/8/10 11:23
 */
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
@ComponentScan("com.yy")
public class ServiceMsmApplication8206 {
    public static void main(String[] args) {
        SpringApplication.run(ServiceMsmApplication8206.class, args);
    }
}
