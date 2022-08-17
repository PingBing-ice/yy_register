package com.yy.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yy.yygh.model.order.PaymentInfo;
import com.yy.yygh.model.order.RefundInfo;

/**
 * <p>
 * 退款信息表 服务类
 * </p>
 *
 * @author ice
 * @since 2022-08-13
 */
public interface IRefundInfoService extends IService<RefundInfo> {

    RefundInfo saveRefundInfo(PaymentInfo paymentInfo);
}
