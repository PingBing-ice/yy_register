package com.yy.order.service.impl;
import java.math.BigDecimal;
import com.google.common.collect.Maps;

import cn.hutool.core.date.DateTime;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.wxpay.sdk.WXPayUtil;
import com.yy.order.service.IOrderInfoService;
import com.yy.order.service.IPaymentInfoService;
import com.yy.order.service.IRefundInfoService;
import com.yy.order.service.WxService;
import com.yy.order.utils.ConstantPropertiesUtils;
import com.yy.order.utils.HttpClient;
import com.yy.util.exception.RException;
import com.yy.yygh.enums.PaymentStatusEnum;
import com.yy.yygh.enums.PaymentTypeEnum;
import com.yy.yygh.enums.RefundStatusEnum;
import com.yy.yygh.model.order.OrderInfo;
import com.yy.yygh.model.order.PaymentInfo;
import com.yy.yygh.model.order.RefundInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author ice
 * @date 2022/8/16 11:40
 */
@Service
public class WxServiceImpl implements WxService {

    @Autowired
    private IOrderInfoService orderInfoService;

    @Autowired
    private IPaymentInfoService paymentInfoService;

    @Autowired
    private IRefundInfoService refundInfoService;
    // 微信退款
    @Override
    public boolean refund(String orderId) {
        try {
            QueryWrapper<PaymentInfo> wrapper = new QueryWrapper<>();
            wrapper.eq("order_id", orderId);
            PaymentInfo paymentInfo = paymentInfoService.getOne(wrapper);
            if (paymentInfo == null) {
                throw new RException("无法取消");
            }
            RefundInfo refundInfo = refundInfoService.saveRefundInfo(paymentInfo);
            if (refundInfo == null) {
                return false;
            }
            if(refundInfo.getRefundStatus() == RefundStatusEnum.REFUND.getStatus().intValue()) {
                return true;
            }
            String outTradeNo = paymentInfo.getOutTradeNo();
            Map<String, String> paramMap = new HashMap<>();
            paramMap.put("appid", ConstantPropertiesUtils.APPID);       //公众账号ID
            paramMap.put("mch_id", ConstantPropertiesUtils.PARTNER);   //商户编号
            paramMap.put("nonce_str", WXPayUtil.generateNonceStr());
            paramMap.put("out_trade_no", outTradeNo); //商户订单编号
            paramMap.put("out_refund_no", "tk" + paymentInfo.getOutTradeNo()); //商户退款单号
            //       paramMap.put("total_fee",paymentInfoQuery.getTotalAmount().multiply(new BigDecimal("100")).longValue()+"");
            //       paramMap.put("refund_fee",paymentInfoQuery.getTotalAmount().multiply(new BigDecimal("100")).longValue()+"");
            paramMap.put("total_fee", "1");
            paramMap.put("refund_fee", "1");
            HttpClient httpClient = new HttpClient("https://api.mch.weixin.qq.com/secapi/pay/refund");
            httpClient.setCert(true);
            httpClient.setHttps(true);
            httpClient.setXmlParam(WXPayUtil.generateSignedXml(paramMap, ConstantPropertiesUtils.PARTNERKEY));
            httpClient.setCertPassword(ConstantPropertiesUtils.PARTNER);
            httpClient.post();

            //3、返回第三方的数据
            String xml = httpClient.getContent();
            Map<String, String> resultMap = WXPayUtil.xmlToMap(xml);
            if (resultMap.get("result_code").equals("SUCCESS")) {
                refundInfo.setCallbackTime(new Date());
                refundInfo.setTradeNo(resultMap.get("refund_id"));
                refundInfo.setRefundStatus(RefundStatusEnum.REFUND.getStatus());
                refundInfo.setCallbackContent(JSONObject.toJSONString(resultMap));
                refundInfoService.updateById(refundInfo);
                return true;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 查看支付状态
     *
     * @param orderId 订单Id
     * @param status 微信支付
     * @return 微信返回的结构
     */
    @Override
    public Map<String, String> queryPayStatus(String orderId, Integer status) {
        try {
            OrderInfo orderInfo = orderInfoService.getById(orderId);
            String outTradeNo = orderInfo.getOutTradeNo();
            Map<String, String> map = new HashMap<>();
            map.put("appid", ConstantPropertiesUtils.APPID);
            map.put("mch_id", ConstantPropertiesUtils.PARTNER);
            map.put("out_trade_no", outTradeNo);
            map.put("nonce_str", WXPayUtil.generateNonceStr());
            HttpClient httpClient = new HttpClient("https://api.mch.weixin.qq.com/pay/orderquery");
            httpClient.setXmlParam(WXPayUtil.generateSignedXml(map, ConstantPropertiesUtils.PARTNERKEY));
            httpClient.setHttps(true);
            httpClient.post();
            String xml = httpClient.getContent();
            return WXPayUtil.xmlToMap(xml);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // 创建订单
    @Override
    public Map<String, Object> createNative(String orderId) {
        try {
            OrderInfo order = orderInfoService.getById(orderId);
            paymentInfoService.savePaymentInfo(order, PaymentTypeEnum.WEIXIN.getStatus());
            // 生成二维码
            //1、设置参数
            Map<String, String> paramMap = new HashMap<>();
            paramMap.put("appid", ConstantPropertiesUtils.APPID);
            paramMap.put("mch_id", ConstantPropertiesUtils.PARTNER);
            paramMap.put("nonce_str", WXPayUtil.generateNonceStr());
            Date reserveDate = order.getReserveDate();
            String reserveDateString = new DateTime(reserveDate).toString("yyyy/MM/dd");
            String body = reserveDateString + "就诊" + order.getDepname();
            paramMap.put("body", body);
            paramMap.put("out_trade_no", order.getOutTradeNo());
            //paramMap.put("total_fee", order.getAmount().multiply(new BigDecimal("100")).longValue()+"");
            paramMap.put("total_fee", "1");//为了测试
            paramMap.put("spbill_create_ip", "127.0.0.1");
            paramMap.put("notify_url", "http://guli.shop/api/order/weixinPay/weixinNotify");
            paramMap.put("trade_type", "NATIVE");
            //2、HTTPClient来根据URL访问第三方接口并且传递参数
            HttpClient client = new HttpClient("https://api.mch.weixin.qq.com/pay/unifiedorder");
            //client设置参数
            client.setXmlParam(WXPayUtil.generateSignedXml(paramMap, ConstantPropertiesUtils.PARTNERKEY));
            client.setHttps(true);
            client.post();
            //3、返回第三方的数据
            String xml = client.getContent();
            Map<String, String> resultMap = WXPayUtil.xmlToMap(xml);
            //4、封装返回结果集
            Map<String, Object> map = new HashMap<>();
            map.put("orderId", orderId);
            map.put("totalFee", order.getAmount());
            map.put("resultCode", resultMap.get("result_code"));
            map.put("codeUrl", resultMap.get("code_url"));

            return map;
        } catch (Exception e) {
            e.printStackTrace();
            return new HashMap<>();
        }
    }
}
