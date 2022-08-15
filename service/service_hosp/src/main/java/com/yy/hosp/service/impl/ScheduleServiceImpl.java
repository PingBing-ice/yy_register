package com.yy.hosp.service.impl;
import java.util.Date;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.yy.hosp.repository.HospitalRepository;
import com.yy.hosp.repository.ScheduleRepository;
import com.yy.hosp.service.DepartmentService;
import com.yy.hosp.service.HospitalService;
import com.yy.hosp.service.ScheduleService;
import com.yy.util.exception.RException;
import com.yy.yygh.model.hosp.BookingRule;
import com.yy.yygh.model.hosp.Department;
import com.yy.yygh.model.hosp.Hospital;
import com.yy.yygh.model.hosp.Schedule;
import com.yy.yygh.vo.hosp.BookingScheduleRuleVo;
import com.yy.yygh.vo.hosp.ScheduleOrderVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author ice
 * @date 2022/8/4 12:45
 */
@Service
public class ScheduleServiceImpl implements ScheduleService {
    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private HospitalRepository hospitalRepository;

    @Resource
    private DepartmentService departmentService;

    @Resource
    private HospitalService hospitalService;


    @Override
    public ScheduleOrderVo getFeignScheduleById(String id) {


        Schedule schedule = getScheduleById(id);
        if (schedule == null) {
            throw new RException("数据错误");
        }

        Hospital hospital = hospitalService.findByHosCode(schedule.getHoscode());
        if (hospital == null) {
            throw new RException("数据错误");
        }
        BookingRule bookingRule = hospital.getBookingRule();
        ScheduleOrderVo scheduleOrderVo = new ScheduleOrderVo();
        scheduleOrderVo.setHoscode(schedule.getHoscode());
        scheduleOrderVo.setHosname((String) schedule.getParam().get("hosname"));
        scheduleOrderVo.setDepcode(schedule.getDepcode());
        scheduleOrderVo.setDepname((String) schedule.getParam().get("depname"));
        scheduleOrderVo.setHosScheduleId(schedule.getHosScheduleId());
        scheduleOrderVo.setTitle(schedule.getTitle());
        scheduleOrderVo.setReserveDate(schedule.getWorkDate());
        scheduleOrderVo.setAvailableNumber(schedule.getAvailableNumber());
        scheduleOrderVo.setReserveTime(schedule.getWorkTime());
        scheduleOrderVo.setAmount(schedule.getAmount());
        //退号截止天数（如：就诊前一天为-1，当天为0）
        int quitDay = bookingRule.getQuitDay();
        DateTime dateTime = this.getDateTime(new org.joda.time.DateTime(schedule.getWorkDate()).plusDays(quitDay).toDate(), bookingRule.getQuitTime());
        scheduleOrderVo.setQuitTime(dateTime.toJdkDate());

        //预约开始时间
        DateTime startTime = this.getDateTime(new Date(), bookingRule.getReleaseTime());
        scheduleOrderVo.setStartTime(startTime.toJdkDate());

        //预约截止时间
        DateTime endTime = this.getDateTime(new org.joda.time.DateTime().plusDays(bookingRule.getCycle()).toDate(), bookingRule.getStopTime());
        scheduleOrderVo.setEndTime(endTime.toJdkDate());

        //当天停止挂号时间
        DateTime stopTime = this.getDateTime(new Date(), bookingRule.getStopTime());
        scheduleOrderVo.setStopTime(stopTime.toJdkDate());

        return scheduleOrderVo;
    }

    // 根据排班Id查询排班信息
    @Override
    public Schedule getScheduleById(String id) {
        Schedule schedule = scheduleRepository.findById(id).get();
        return packageSchedule(schedule);
    }

    private Schedule packageSchedule(Schedule schedule) {
        String hoscode = schedule.getHoscode();
        Hospital byHoscode = hospitalRepository.findByHoscode(hoscode);
        String hosname = byHoscode.getHosname();
        String depcode = schedule.getDepcode();
        Department department = departmentService.getDepartment(hoscode, depcode);
        String depname = department.getDepname();
        schedule.getParam().put("hosname", hosname);
        schedule.getParam().put("dayOfWeek", dayOfWeek(schedule.getWorkDate()));
        schedule.getParam().put("depname", depname);
        return schedule;
    }

    // 获取可预约排班数据
    @Override
    public Map<String, Object> getBookingScheduleRule(Integer page, Integer limit, String hoscode, String depcode) {


        // 查询医院的预约信息
        Hospital hospital = hospitalService.findByHosCode(hoscode);
        if (hospital == null) {
            throw new RException("无医院");
        }
        BookingRule bookingRule = hospital.getBookingRule();
        //获取可预约日期分页数据
        IPage<Date> iPage = this.getListDate(page, limit, bookingRule);
        // 当前可预约的日期
        List<Date> dateList = iPage.getRecords();

        //获取可预约日期科室剩余预约数
        Criteria criteria = Criteria.where("hoscode").is(hoscode).and("depcode").is(depcode).and("workDate").in(dateList);
        Aggregation agg = Aggregation.newAggregation(
                Aggregation.match(criteria), // 创建查询条件
                Aggregation.group("workDate") // 根据workDate进行分组
                        .first("workDate").as("workDate") // 查询这个字段
                        .count().as("docCount")  // 统计分组数量
                        .sum("reservedNumber").as("reservedNumber") // 可预约数求和
                        .sum("availableNumber").as("availableNumber") // 剩余预约数求和
        );
        AggregationResults<BookingScheduleRuleVo> aggregate = mongoTemplate.
                aggregate(agg, Schedule.class, BookingScheduleRuleVo.class);
        // 获取聚合的数据
        List<BookingScheduleRuleVo> results = aggregate.getMappedResults();

        Map<Date, BookingScheduleRuleVo> ruleVoMap = results.stream().
                collect(Collectors.toMap(BookingScheduleRuleVo::getWorkDate, BookingScheduleRuleVo -> BookingScheduleRuleVo));

        List<BookingScheduleRuleVo> bookingScheduleRuleVos = new ArrayList<>();
        for (int i = 0; i < dateList.size(); i++) {
            Date date = dateList.get(i);
            BookingScheduleRuleVo bookingScheduleRuleVo = ruleVoMap.get(date);
            if (bookingScheduleRuleVo == null) {
                bookingScheduleRuleVo = new BookingScheduleRuleVo();
                bookingScheduleRuleVo.setDocCount(0);
                //科室剩余预约数  -1表示无号
                bookingScheduleRuleVo.setAvailableNumber(-1);
                bookingScheduleRuleVos.add(bookingScheduleRuleVo);
            }
            bookingScheduleRuleVo.setWorkDate(date);
            bookingScheduleRuleVo.setWorkDateMd(date);
            bookingScheduleRuleVo.setDayOfWeek(dayOfWeek(date));
            //最后一页最后一条记录为即将预约   状态 0：正常 1：即将放号 -1：当天已停止挂号
            if (i == dateList.size() - 1 && page == iPage.getPages()) {
                bookingScheduleRuleVo.setStatus(1);
            } else {
                bookingScheduleRuleVo.setStatus(0);
            }
            //当天预约如果过了停号时间， 不能预约
            if (i == 0 && page == 1) {
                DateTime stopTime = this.getDateTime(new Date(), bookingRule.getStopTime());
                if (stopTime.isAfter(new Date())) {
                    //停止预约
                    bookingScheduleRuleVo.setStatus(-1);
                }
            }
            bookingScheduleRuleVos.add(bookingScheduleRuleVo);
        }

        Map<String, Object> result = new HashMap<>();
        //可预约日期规则数据
        result.put("bookingScheduleList", bookingScheduleRuleVos);
        result.put("total", iPage.getTotal());
        //其他基础数据
        Map<String, String> baseMap = new HashMap<>();
        //医院名称
        baseMap.put("hosname", hospitalService.getByHoscode(hoscode).getHosname());
        //科室
        Department department = departmentService.getDepartment(hoscode, depcode);
        //大科室名称
        baseMap.put("bigname", department.getBigname());
        //科室名称
        baseMap.put("depname", department.getDepname());
        //月
        baseMap.put("workDateString", new DateTime().toString("yyyy年MM月"));
        //放号时间
        baseMap.put("releaseTime", bookingRule.getReleaseTime());
        //停号时间
        baseMap.put("stopTime", bookingRule.getStopTime());
        result.put("baseMap", baseMap);
        return result;
    }

    private IPage<Date> getListDate(Integer page, Integer limit, BookingRule bookingRule) {


        // 当天的放号时间
        DateTime releaseTime = this.getDateTime(new Date(), bookingRule.getReleaseTime());
        // 预约周期
        Integer cycle = bookingRule.getCycle();
        // 如果预约时间已过则周期加一
        if (releaseTime.isAfter(new Date())) cycle = cycle + 1;

        // 获取预约周期的日期
        List<Date> dateList = new ArrayList<>();
        for (int i = 0; i < cycle; i++) {
            DateTime curDateTime = DateUtil.offsetDay(new DateTime(), i);
            String dateString = curDateTime.toString("yyyy-MM-dd");
            dateList.add(new DateTime(dateString).toJdkDate());
        }


        int begin = (page - 1) * limit;
        int end = begin + limit;


        if (end > dateList.size()) end = dateList.size();
        List<Date> pageDateList = new ArrayList<>();
        for (int i = begin; i < end; i++) {
            pageDateList.add(dateList.get(i));
        }

        com.baomidou.mybatisplus.extension.plugins.pagination.Page<Date> dataPage =
                new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(page, limit, dateList.size());
        dataPage.setRecords(pageDateList);
        return dataPage;
    }

    /**
     * 将Date日期（yyyy-MM-dd HH:mm）转换为DateTime
     */
    private DateTime getDateTime(Date date, String timeString) {
        String dateTimeString = new DateTime(date).toString("yyyy-MM-dd") + " " + timeString;
        DateTime dt = DateUtil.parse(dateTimeString);
        return dt;
    }

    @Override
    public void save(Map<String, Object> switchMap) {
        Schedule schedule = JSONObject.parseObject(JSONObject.toJSONString(switchMap), Schedule.class);
        //根据医院编号 和 排班编号查询
        Schedule scheduleExist = scheduleRepository.findByHoscodeAndHosScheduleId(schedule.getHoscode(), schedule.getHosScheduleId());
        //判断
        if (scheduleExist != null) {
            scheduleExist.setId(schedule.getId());
            scheduleExist.setUpdateTime(new Date());
            scheduleExist.setIsDeleted(0);
            scheduleExist.setStatus(1);
            scheduleRepository.save(scheduleExist);

        } else {
            schedule.setCreateTime(new Date());
            schedule.setUpdateTime(new Date());
            schedule.setIsDeleted(0);
            schedule.setStatus(1);
            scheduleRepository.save(schedule);
        }
    }

    @Override
    public Page<Schedule> getList(Map<String, Object> switchMap) {
        String hoscode = (String) switchMap.get("hoscode");
        String page = (String) switchMap.get("page");
        String limit = (String) switchMap.get("limit");

        Schedule schedule = new Schedule();
        schedule.setHoscode(hoscode);

        Example<Schedule> example = Example.of(schedule);
        Sort sort = Sort.by(Sort.Direction.ASC, "createTime");
        PageRequest pageRequest = PageRequest.of(Integer.parseInt(page), Integer.parseInt(limit), sort);
        return scheduleRepository.findAll(example, pageRequest);
    }

    @Override
    public boolean removeSchedule(Map<String, Object> switchMap) {
        String hoscode = (String) switchMap.get("hoscode");
        String hosScheduleId = (String) switchMap.get("hosScheduleId");
        Schedule schedule = scheduleRepository.findByHoscodeAndHosScheduleId(hoscode, hosScheduleId);
        if (schedule == null) {
            return false;
        }
        scheduleRepository.deleteById(schedule.getId());
        return true;
    }

    @Override
    public Map<String, Object> getRuleSchedule(Long page, Long limit, String hoscode, String depcode) {
        HashMap<String, Object> resultMap = new HashMap<>();
        Criteria criteria = Criteria.where("hoscode").is(hoscode).and("depcode").is(depcode);
        Aggregation agg = Aggregation.newAggregation(
                Aggregation.match(criteria), // 创建查询条件
                Aggregation.group("workDate") // 根据workDate进行分组
                        .first("workDate").as("workDate") // 查询这个字段
                        .count().as("docCount")  // 统计分组数量
                        .sum("reservedNumber").as("reservedNumber") // 可预约数求和
                        .sum("availableNumber").as("availableNumber"), // 剩余预约数求和
                Aggregation.sort(Sort.Direction.ASC, "workDate"), // 排序
                Aggregation.skip((page - 1) * limit), // 分页
                Aggregation.limit(limit)// 分页
        );
        AggregationResults<BookingScheduleRuleVo> aggregate = mongoTemplate.aggregate(agg, Schedule.class, BookingScheduleRuleVo.class);
        List<BookingScheduleRuleVo> resultList = aggregate.getMappedResults();

        resultList.forEach(result -> result.setDayOfWeek(dayOfWeek(result.getWorkDate())));


        resultMap.put("bookingScheduleRuleList", resultList);


        Criteria criteria1 = Criteria.where("hoscode").is(hoscode).and("depcode").is(depcode);
        Aggregation agg1 = Aggregation.newAggregation(
                Aggregation.match(criteria1), // 创建查询条件
                Aggregation.group("workDate")); // 根据workDate进行分组
        AggregationResults<BookingScheduleRuleVo> aggregate1 = mongoTemplate.aggregate(agg1, Schedule.class, BookingScheduleRuleVo.class);
        List<BookingScheduleRuleVo> totals = aggregate1.getMappedResults();
        // 总记录数
        resultMap.put("total", totals.size());

        //获取医院名称
        Hospital hospital = hospitalRepository.getByHoscode(hoscode);
        //其他基础数据
        Map<String, String> baseMap = new HashMap<>();
        baseMap.put("hosname", hospital.getHosname());
        resultMap.put("baseMap", baseMap);
        return resultMap;
    }

    /**
     * 根据医院编号 、科室编号和工作日期，查询排班详细信息
     *
     * @param hoscode  医院编号
     * @param depcode  科室编号
     * @param workDate 工作日期
     * @return 排班详细信息
     */
    @Override
    public List<Schedule> getDetailSchedule(String hoscode, String depcode, String workDate) {
        Date dateTime = DateUtil.parse(workDate);
        List<Schedule> list = scheduleRepository.findAllByHoscodeAndDepcodeAndWorkDate(hoscode, depcode, dateTime);
        if (list.size() <= 0) {
            throw new RException("查询的数据为空");
        }
        return list;
    }

    /**
     * 返回星期几
     *
     * @param date 时间
     * @return 返回
     */
    public static String dayOfWeek(Date date) {
        int day = DateUtil.dayOfWeek(date);
        switch (day) {
            case 1:
                return "星期天";
            case 2:
                return "星期一";
            case 3:
                return "星期二";
            case 4:
                return "星期三";
            case 5:
                return "星期四";
            case 6:
                return "星期五";
            case 7:
                return "星期六";
            default:
                return "";
        }
    }


}
