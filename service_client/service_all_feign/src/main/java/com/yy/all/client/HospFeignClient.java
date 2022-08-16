package com.yy.all.client;

import com.yy.yygh.vo.hosp.ScheduleOrderVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @author ice
 * @date 2022/8/15 10:19
 */
@FeignClient("service-hosp")
public interface HospFeignClient {

    // 根据排班Id获取排班信息
    @GetMapping("/hosp/schedule/getFeignScheduleById/{id}")
    ScheduleOrderVo getFeignScheduleById(@PathVariable("id") String ScheduleId);
}
