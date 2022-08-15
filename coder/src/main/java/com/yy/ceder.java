package com.yy;

import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;

import java.util.Collections;

/**
 * @author ice
 * @date 2022/7/29 11:12
 */

public class ceder {
    public static void main(String[] args) {
        FastAutoGenerator.create("jdbc:mysql://localhost:3306/yygh_order?serverTimezone=GMT%2B8&characterEncoding=utf-8", "root", "pwb2001")
                .globalConfig(builder -> {
                    builder.author("ice") // 设置作者
                            .enableSwagger() // 开启 swagger 模式
                            .fileOverride() // 覆盖已生成文件
                            .outputDir("D:\\JavaUser\\yy_parent\\service\\service_orders\\src\\main\\java"); // 指定输出目录


                })
                .packageConfig(builder -> {
                    builder.parent("com.yy") // 设置父包名
                            .moduleName("user") // 设置父包模块名
                            .pathInfo(Collections.singletonMap(OutputFile.xml,
                                    "D:\\JavaUser\\yy_parent\\service\\service_orders\\src\\main\\java\\com\\yy\\order\\mapper\\xml")); // 设置mapperXml生成路径
                })
                .strategyConfig(builder -> {
                    builder.addInclude("order_info,payment_info,refund_info")// 设置需要生成的表名
                            .addTablePrefix(); // 设置过滤表前缀

                })
                .templateEngine(new FreemarkerTemplateEngine()) // 使用Freemarker引擎模板，默认的是Velocity引擎模板
                .execute();

    }
}
