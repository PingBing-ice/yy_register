package com.yy.hosp.controller.api;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.yy.hosp.service.DepartmentService;
import com.yy.hosp.service.HospitalService;
import com.yy.hosp.service.IHospitalSetService;
import com.yy.hosp.service.ScheduleService;
import com.yy.hosp.util.HttpRequestHelper;
import com.yy.util.result.R;
import com.yy.yygh.model.hosp.Department;
import com.yy.yygh.model.hosp.Hospital;
import com.yy.yygh.model.hosp.Schedule;
import io.swagger.annotations.ApiOperation;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @author ice
 * @date 2022/8/3 15:00
 * <p>
 * 平台对接医院信息
 */
@RestController
@RequestMapping("/api/hosp")
@Log4j2
public class HospitalApiController {

    @Autowired
    private HospitalService hospitalService;

    @Autowired
    private IHospitalSetService hospitalSetService;

    @Autowired
    private DepartmentService departmentService;

    @Autowired
    private ScheduleService scheduleService;

    /**
     * 保存传过来的医院的信息
     *
     * @param request 穿过来的数据
     * @return 将数据返回回去
     */
    @PostMapping("/saveHospital")
    public R saveHospital(HttpServletRequest request) {
        Map<String, String[]> parameterMap = request.getParameterMap();
        Map<String, Object> map = HttpRequestHelper.switchMap(parameterMap);

        String logoData = (String) map.get("logoData");
        logoData = logoData.replaceAll(" ", "+");
        map.put("logoData", logoData);
        // 验证是否
        boolean verifyKey = VerifyKey(map);
        if (!verifyKey) {
            return R.error().code(201);
        }
        hospitalService.save(map);
        return R.ok().code(200);
    }

    /**
     * 查找医院的信息
     *
     * @param request 传过来的数据
     * @return 将数据返回回去
     */
    @PostMapping("/hospital/show")
    public R getHospitalList(HttpServletRequest request) {
        log.info("===========================================");
        Map<String, String[]> parameterMap = request.getParameterMap();
        Map<String, Object> map = HttpRequestHelper.switchMap(parameterMap);
        String hoscode = (String) map.get("hoscode");
        boolean verifyKey = VerifyKey(map);
        if (!verifyKey) {
            return R.error().code(201);
        }
        Hospital hospital = hospitalService.findByHosCode(hoscode);
        return R.ok().code(200).data("data", hospital);
    }


    /**
     * 保存医院科室的信息
     *
     * @param request 数据
     * @return 返回 200
     */
    @PostMapping("/saveDepartment")
    public R saveDepartment(HttpServletRequest request) {
        Map<String, String[]> parameterMap = request.getParameterMap();
        Map<String, Object> map = HttpRequestHelper.switchMap(parameterMap);
        // 验证是否
        boolean verifyKey = VerifyKey(map);
        if (!verifyKey) {
            return R.error().code(201);
        }
        departmentService.save(map);
        return R.ok().code(200);
    }

    @ApiOperation(value = "获取分页列表")
    @PostMapping("/department/list")
    public R departmentList(HttpServletRequest request) {
        Map<String, String[]> map = request.getParameterMap();
        Map<String, Object> paramMap = HttpRequestHelper.switchMap(map);
        // 验证是否
        boolean verifyKey = VerifyKey(paramMap);
        if (!verifyKey) {
            return R.error().code(201);
        }
        Page<Department> page = departmentService.findDepartmentPage(paramMap);

        return R.ok().code(200).data("data", page);

    }

    @PostMapping("/department/remove")
    public R removeDepartment(HttpServletRequest request) {
        Map<String, String[]> map = request.getParameterMap();
        Map<String, Object> paramMap = HttpRequestHelper.switchMap(map);
        boolean verifyKey = VerifyKey(paramMap);
        if (!verifyKey) {
            return R.error().code(201);
        }
        departmentService.removeDepartment(paramMap);

        return R.ok().code(200);
    }

    /**
     *  保存排班信息
     * @param request
     * @return
     */
    @PostMapping("/saveSchedule")
    public R saveSchedule(HttpServletRequest request) {
        Map<String, Object> switchMap = HttpRequestHelper.switchMap(request.getParameterMap());
        boolean verifyKey = VerifyKey(switchMap);
        if (!verifyKey) {
            return R.error().code(201);
        }
        scheduleService.save(switchMap);
        return R.ok().code(200);
    }

    @PostMapping("/schedule/list")
    public R scheduleList(HttpServletRequest request) {
        Map<String, Object> switchMap = HttpRequestHelper.switchMap(request.getParameterMap());
        boolean verifyKey = VerifyKey(switchMap);
        if (!verifyKey) {
            return R.error().code(201);
        }
        Page<Schedule> page =scheduleService.getList(switchMap);
        return R.ok().code(200).data("data", page);
    }
    @PostMapping("/schedule/remove")
    public R RemoveSchedule(HttpServletRequest request) {
        Map<String, String[]> parameterMap = request.getParameterMap();
        Map<String, Object> switchMap = HttpRequestHelper.switchMap(parameterMap);
        boolean verifyKey = VerifyKey(switchMap);
        if (!verifyKey) {
            return R.error().code(201);
        }
        boolean removeSchedule = scheduleService.removeSchedule(switchMap);
        if (!removeSchedule) {
            return R.error().code(201).message("删除失败");
        }

        return R.ok().code(200);
    }

    /**
     *  验证key
     * @param map
     * @return
     */
    public boolean VerifyKey(Map<String, Object> map) {
        String signKey = (String) map.get("sign");
        String hoscode = (String) map.get("hoscode");
        if (StringUtils.isEmpty(signKey) && StringUtils.isEmpty(hoscode)) {
            return false;
        }
        String signKeySet = hospitalSetService.getSignKey(hoscode);
        if (!signKeySet.equals(signKey)) {
            log.error("验证失败");
            return false;
        }
        return true;
    }
}
