package com.yy.order.service.impl;


import com.yy.order.mapper.RefundInfoMapper;
import com.yy.order.service.IRefundInfoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yy.yygh.model.order.RefundInfo;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 退款信息表 服务实现类
 * </p>
 *
 * @author ice
 * @since 2022-08-13
 */
@Service
public class RefundInfoServiceImpl extends ServiceImpl<RefundInfoMapper, RefundInfo> implements IRefundInfoService {

}
