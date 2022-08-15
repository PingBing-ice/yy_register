package com.yy.user.util;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

/**
 * @author ice
 * @date 2022/8/10 16:04
 */
@Component
public class ConstantPropertiesUtil implements InitializingBean {

    private String appId ="wxed9954c01bb89b47";

    private String appSecret = "a7482517235173ddb4083788de60b90e";

    private String redirectUrl = "http://localhost:8160/user/weixilogin/callback";

    public static String WX_OPEN_APP_ID;
    public static String WX_OPEN_APP_SECRET;
    public static String WX_OPEN_REDIRECT_URL;
    @Override
    public void afterPropertiesSet() throws Exception {
        WX_OPEN_APP_ID = appId;
        WX_OPEN_APP_SECRET = appSecret;
        WX_OPEN_REDIRECT_URL = redirectUrl;
    }
}
