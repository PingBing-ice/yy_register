package com.yy.order.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yy.yygh.model.order.OrderInfo;
import com.yy.yygh.vo.order.OrderQueryVo;

/**
 * <p>
 * 订单表 服务类
 * </p>
 *
 * @author ice
 * @since 2022-08-13
 */
public interface IOrderInfoService extends IService<OrderInfo> {

    // 创建订单
    String saveOrder(String scheduleId, String patientId);
    //订单列表（条件查询带分页）
    IPage<OrderInfo> selectPage(Page<OrderInfo> pageParam, OrderQueryVo orderQueryVo);
}
