package com.yy.order.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yy.order.service.IOrderInfoService;
import com.yy.util.result.R;
import com.yy.util.utils.JwtUtils;
import com.yy.yygh.enums.OrderStatusEnum;
import com.yy.yygh.model.order.OrderInfo;
import com.yy.yygh.vo.order.OrderQueryVo;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.stereotype.Controller;

import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 * 订单表 前端控制器
 * </p>
 *
 * @author ice
 * @since 2022-08-13
 */
@RestController
@RequestMapping("/order/orderInfo")
public class OrderInfoController {
    @Autowired
    private IOrderInfoService orderInfoService;

    @ApiOperation(value = "创建订单")
    @PostMapping("/auth/submitOrder/{scheduleId}/{patientId}")
    public R submitOrder(
            @PathVariable("scheduleId") String scheduleId,
            @PathVariable("patientId") String patientId) {

        String orderId = orderInfoService.saveOrder(scheduleId, patientId);
        return R.ok().data("orderId",orderId);
    }

    //订单列表（条件查询带分页）
    @GetMapping("getPageListByOrder/{page}/{limit}")
    public R list(@PathVariable Long page,
                  @PathVariable Long limit,
                  OrderQueryVo orderQueryVo, HttpServletRequest request) {
        //设置当前用户id
        orderQueryVo.setUserId(JwtUtils.getTokenById(request));
        Page<OrderInfo> pageParam = new Page<>(page,limit);
        IPage<OrderInfo> pageModel =
                orderInfoService.selectPage(pageParam,orderQueryVo);
        return R.ok().data("pageModel",pageModel);
    }

    @ApiOperation(value = "获取订单状态")
    @GetMapping("auth/getStatusList")
    public R getStatusList() {
        return R.ok().data("statusList", OrderStatusEnum.getStatusList());
    }

    @GetMapping("/auth/getOrders")
    public R getOrderInfoById(@RequestParam(value = "orderId",required = false)String orderId) {
        if (StringUtils.isEmpty(orderId)) {
            return R.error().message("订单为空");
        }
        OrderInfo orderInfo = orderInfoService.getById(orderId);
        if (orderInfo == null) {
            return R.error().message("订单为空");
        }
        return R.ok().data("orderInfo", orderInfo);
    }


}
