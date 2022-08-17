package com.yy.order.controller.wx;

import com.yy.order.service.IOrderInfoService;
import com.yy.order.service.IPaymentInfoService;
import com.yy.order.service.WxService;
import com.yy.util.result.R;
import com.yy.yygh.enums.PaymentTypeEnum;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @author ice
 * @date 2022/8/16 11:38
 */
@RestController
@RequestMapping("/order/wx")
public class WxController {

    @Autowired
    private WxService wxService;

    @Autowired
    private IPaymentInfoService paymentInfoService;

    @Autowired
    private IOrderInfoService orderInfoService;

    @ApiOperation(value = "取消预约")
    @GetMapping("/cancelOrder/{orderId}")
    public R cancelOrder(@PathVariable String orderId) {
        boolean flag = orderInfoService.cancelOrder(orderId);
        return R.ok().data("flag",flag);
    }

    /**
     * 下单 生成二维码
     */
    @GetMapping("/createNative/{orderId}")
    public R createNative(
            @PathVariable("orderId") String orderId) {
        Map<String,Object> map = wxService.createNative(orderId);
        return R.ok().data(map);
    }

    /**
     * 查看订单的状态
     * @param orderId 订单的Id
     * @return
     */
    @GetMapping("/queryPayStatus/{orderId}")
    public R queryPayStatus(@PathVariable("orderId")String orderId ) {
        Map<String, String> resultMap = wxService.queryPayStatus(orderId, PaymentTypeEnum.WEIXIN.getStatus());
        if (resultMap == null) {
            return R.error().message("支付失败");
        }
        if (resultMap.get("trade_state").equals("SUCCESS")) {
            // 支付成功 修改订单的支付状态 和
            //更改订单状态，处理支付结果
            String out_trade_no = resultMap.get("out_trade_no");
            paymentInfoService.paySuccess(out_trade_no, PaymentTypeEnum.WEIXIN.getStatus(), resultMap,orderId);
            return R.ok().message("支付成功");
        }
        return R.ok().message("支付中");
    }
}
