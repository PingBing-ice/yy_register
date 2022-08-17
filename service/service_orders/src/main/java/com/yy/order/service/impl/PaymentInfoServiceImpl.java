package com.yy.order.service.impl;


import cn.hutool.core.date.DateTime;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yy.order.mapper.PaymentInfoMapper;
import com.yy.order.service.IOrderInfoService;
import com.yy.order.service.IPaymentInfoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yy.util.exception.RException;
import com.yy.yygh.enums.PaymentStatusEnum;
import com.yy.yygh.model.order.OrderInfo;
import com.yy.yygh.model.order.PaymentInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Map;

/**
 * <p>
 * 支付信息表 服务实现类
 * </p>
 *
 * @author ice
 * @since 2022-08-13
 */
@Service
public class PaymentInfoServiceImpl extends ServiceImpl<PaymentInfoMapper, PaymentInfo> implements IPaymentInfoService {

    @Autowired
    private IOrderInfoService orderInfoService;

    // 修改订单的状态
    @Override
    @Transactional
    public void paySuccess(String out_trade_no, Integer status, Map<String, String> resultMap,String orderId) {
        QueryWrapper<PaymentInfo> wrapper = new QueryWrapper<>();
        wrapper.eq("out_trade_no", out_trade_no);
        wrapper.eq("order_id", orderId);
        wrapper.eq("payment_type", status);
        PaymentInfo paymentInfo = new PaymentInfo();
        paymentInfo.setPaymentStatus(PaymentStatusEnum.PAID.getStatus());
        baseMapper.update(paymentInfo, wrapper);


        QueryWrapper<OrderInfo> orderInfoQueryWrapper = new QueryWrapper<>();
        orderInfoQueryWrapper.eq("out_trade_no", out_trade_no);
        OrderInfo orderInfo = orderInfoService.getOne(orderInfoQueryWrapper);
        orderInfo.setOrderStatus(PaymentStatusEnum.PAID.getStatus());
        orderInfoService.updateById(orderInfo);
    }

    @Override
    public void savePaymentInfo(OrderInfo orderInfo, Integer paymentType) {
        QueryWrapper<PaymentInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("order_id", orderInfo.getId());
        queryWrapper.eq("payment_type", paymentType);
        Long count = baseMapper.selectCount(queryWrapper);
        if(count >0) return;
        // 保存交易记录
        PaymentInfo paymentInfo = new PaymentInfo();
        paymentInfo.setCreateTime(new Date());
        paymentInfo.setOrderId(orderInfo.getId());
        paymentInfo.setPaymentType(paymentType);
        paymentInfo.setOutTradeNo(orderInfo.getOutTradeNo());
        paymentInfo.setPaymentStatus(PaymentStatusEnum.UNPAID.getStatus());
        String subject = new DateTime(orderInfo.getReserveDate()).toString("yyyy-MM-dd")+"|"+orderInfo.getHosname()+"|"+orderInfo.getDepname()+"|"+orderInfo.getTitle();
        paymentInfo.setSubject(subject);
        paymentInfo.setTotalAmount(orderInfo.getAmount());
        int insert = baseMapper.insert(paymentInfo);
        if (insert != 1) {
            throw new RException("保存失败");
        }
    }
}
