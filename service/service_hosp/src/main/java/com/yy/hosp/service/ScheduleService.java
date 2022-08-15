package com.yy.hosp.service;

import com.yy.yygh.model.hosp.Schedule;
import com.yy.yygh.vo.hosp.ScheduleOrderVo;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

/**
 * @author ice
 * @date 2022/8/4 12:45
 */

public interface ScheduleService {
    /**
     * 保存排班信息
     * @param switchMap
     */
    void save(Map<String, Object> switchMap);

    Page<Schedule> getList(Map<String, Object> switchMap);

    boolean removeSchedule(Map<String, Object> switchMap);


    /**
     *  根据医院编号 和 科室编号 ，查询排班规则数据
     * @param page 当前页码
     * @param limit 当前页面的总记录数
     * @param hoscode 医院编号
     * @param depcode 科室编号
     * @return map
     */
    Map<String, Object> getRuleSchedule(Long page, Long limit, String hoscode, String depcode);
    /**
     *  根据医院编号 、科室编号和工作日期，查询排班详细信息
     * @param hoscode 医院编号
     * @param depcode 科室编号
     * @param workDate 工作日期
     * @return 排班详细信息
     */
    List<Schedule> getDetailSchedule(String hoscode, String depcode, String workDate);


    // 获取可预约排班数据
    Map<String, Object> getBookingScheduleRule(Integer page, Integer limit, String hoscode, String depcode);

    // 根据排班Id查询排班信息
    Schedule getScheduleById(String id);

    ScheduleOrderVo getFeignScheduleById(String id);
}
