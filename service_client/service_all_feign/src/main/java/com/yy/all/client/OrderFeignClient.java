package com.yy.all.client;

import com.yy.yygh.vo.order.OrderCountQueryVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

/**
 * @author ice
 * @date 2022/8/17 11:54
 */
@FeignClient("service-orders")
public interface OrderFeignClient {
    // 获取订单统计数据
    @PostMapping("/order/orderInfo/inner/getCountMap")
    Map<String, Object> getCountMap(@RequestBody OrderCountQueryVo orderCountQueryVo);
}
