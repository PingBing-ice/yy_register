package com.yy.order.service.impl;


import com.yy.order.mapper.PaymentInfoMapper;
import com.yy.order.service.IPaymentInfoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yy.yygh.model.order.PaymentInfo;
import org.springframework.stereotype.Service;

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

}
