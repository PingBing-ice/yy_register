package com.yy.oss.util;



/**
 * 常量类，读取配置文件application.properties中的配置
 */
public class ConstantPropertiesUtil {


    private static final String endpoint = "oss-cn-hangzhou.aliyuncs.com";

    private static final String keyId = "LTAI5tNB3itGeywJ6BtVn3nb";

    private static final String keySecret = "kjiNPToIUiAODzMqcdP08KBYUazK9J";

    private static final String bucketName = "bing-edu";

    public static String getEndpoint() {
        return endpoint;
    }

    public static String getKeyId() {
        return keyId;
    }

    public static String getKeySecret() {
        return keySecret;
    }

    public static String getBucketName() {
        return bucketName;
    }
}