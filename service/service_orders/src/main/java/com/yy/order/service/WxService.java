package com.yy.order.service;

import java.util.Map;

/**
 * 微信支付
 * @author ice
 * @date 2022/8/16 11:39
 */

public interface WxService {
    Map<String, Object> createNative(String orderId);

    Map<String, String> queryPayStatus(String orderId, Integer status);

    boolean refund(String orderId);
}
