package com.yy.order.utils;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ConstantPropertiesUtils implements InitializingBean {


    private String appid = "wx74862e0dfcf69954";

    // 商户号
    private String partner ="1558950191";

    private String partnerkey = "T6m9iK73b0kn9g5v426MKfHQH7X8rKwb";

    private String cert =  "D:\\\\JavaUser\\\\yy_parent\\\\service\\\\service_orders\\\\src\\\\main\\\\resources\\\\apiclient_cert.p12";

    public static String APPID;
    public static String PARTNER;
    public static String PARTNERKEY;
    public static String CERT;

    @Override
    public void afterPropertiesSet() throws Exception {
        System.out.println(cert+"===========================================================");
        APPID = appid;
        PARTNER = partner;
        PARTNERKEY = partnerkey;
        CERT = cert;
    }
}

