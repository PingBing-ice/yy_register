package com.yy.hosp.controller;

import com.yy.hosp.service.ScheduleService;
import com.yy.util.exception.RException;
import com.yy.util.result.R;
import com.yy.yygh.model.hosp.Schedule;
import com.yy.yygh.vo.hosp.ScheduleOrderVo;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * 排班信息
 *
 * @author ice
 * @date 2022/8/6 9:18
 */
@RequestMapping("/hosp/schedule")
@RestController
@Slf4j
public class ScheduleController {

    @Autowired
    private ScheduleService scheduleService;


    /**
     * 根据Id返回排班信息
     *
     * @param id
     * @return
     */
    @GetMapping("getSchedule/{id}")
    public R getScheduleList(@PathVariable("id") String id) {
        Schedule schedule = scheduleService.getScheduleById(id);
        return R.ok().data("schedule", schedule);
    }


    //根据医院编号 和 科室编号 ，查询排班规则数据
    @ApiOperation(value = "查询排班规则数据")
    @GetMapping("/ScheduleList/{page}/{limit}")
    public R getSchedulePageMap(@PathVariable("page") Long page,
                                @PathVariable("limit") Long limit,
                                @RequestParam(value = "hoscode", required = false) String hoscode,
                                @RequestParam(value = "depcode", required = false) String depcode) {
        if (page <= 0 || limit <= 0) {
            return R.error().message("数据错误");
        }
        //根据医院编号 和 科室编号 ，查询排班规则数据
        Map<String, Object> map = scheduleService.getRuleSchedule(page, limit, hoscode, depcode);
        return R.ok().data(map);
    }

    /**
     * 根据医院编号 、科室编号和工作日期，查询排班详细信息
     *
     * @param hoscode  医院编号
     * @param depcode  科室编号
     * @param workDate 工作日期
     * @return 排班详细信息
     */
    @GetMapping("/getScheduleDetail")
    public R getSchedulePageList(@RequestParam(value = "hoscode", required = false) String hoscode,
                                 @RequestParam(value = "depcode", required = false) String depcode,
                                 @RequestParam(value = "workDate", required = false) String workDate) {
        if (StringUtils.isEmpty(hoscode) && StringUtils.isEmpty(depcode) && StringUtils.isEmpty(workDate)) {
            return R.error().message("数据为空");
        }
        log.info(hoscode, depcode, workDate);
        List<Schedule> list = scheduleService.getDetailSchedule(hoscode, depcode, workDate);
        return R.ok().data("list", list);
    }

    @GetMapping("/getFeignScheduleById/{id}")
    public ScheduleOrderVo getFeignScheduleById(@PathVariable("id")String ScheduleId) {
        if (StringUtils.isEmpty(ScheduleId)) {
            throw new RException("数据错误");
        }
        ScheduleOrderVo scheduleOrderVo = scheduleService.getFeignScheduleById(ScheduleId);
        if (scheduleOrderVo == null) {
            throw new RException("数据错误");
        }
        return scheduleOrderVo;
    }

}
