package com.yy.order.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yy.yygh.model.order.OrderInfo;
import com.yy.yygh.vo.order.OrderCountQueryVo;
import com.yy.yygh.vo.order.OrderCountVo;

import java.util.List;

/**
 * <p>
 * 订单表 Mapper 接口
 * </p>
 *
 * @author ice
 * @since 2022-08-13
 */
public interface OrderInfoMapper extends BaseMapper<OrderInfo> {

    List<OrderCountVo> selectOrderCount(OrderCountQueryVo orderCountQueryVo);
}
