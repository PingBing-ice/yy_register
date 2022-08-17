package com.yy.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yy.yygh.enums.PaymentTypeEnum;
import com.yy.yygh.model.order.OrderInfo;
import com.yy.yygh.model.order.PaymentInfo;

import java.util.Map;

/**
 * <p>
 * 支付信息表 服务类
 * </p>
 *
 * @author ice
 * @since 2022-08-13
 */
public interface IPaymentInfoService extends IService<PaymentInfo> {

    void savePaymentInfo(OrderInfo orderInfo, Integer paymentType);

    // 修改订单状态
    void paySuccess(String out_trade_no, Integer status, Map<String, String> resultMap,String orderId);
}
