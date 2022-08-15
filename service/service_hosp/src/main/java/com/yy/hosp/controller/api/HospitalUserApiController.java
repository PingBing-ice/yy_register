package com.yy.hosp.controller.api;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.google.common.collect.Maps;
import com.yy.all.client.UserFeignClient;
import com.yy.hosp.service.DepartmentService;
import com.yy.hosp.service.HospitalService;
import com.yy.hosp.service.ScheduleService;
import com.yy.hosp.vo.ResultIndex;
import com.yy.util.result.R;
import com.yy.util.utils.JwtUtils;
import com.yy.yygh.model.hosp.Hospital;
import com.yy.yygh.model.hosp.Schedule;
import com.yy.yygh.model.user.UserInfo;
import com.yy.yygh.vo.hosp.DepartmentVo;
import com.yy.yygh.vo.hosp.HospitalQueryVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * 医院显示接口
 *
 * @author ice
 * @date 2022/8/8 17:03
 */
@RestController
@RequestMapping("/hosp/user")
@Slf4j
@Api(tags = "医院显示接口")
public class HospitalUserApiController {
    @Resource
    private HospitalService hospitalService;

    @Resource
    private DepartmentService departmentService;

    @Resource
    private ScheduleService scheduleService;

    @Autowired
    private UserFeignClient userFeignClient;


    @ApiOperation(value = "获取可预约排班数据")
    @GetMapping("auth/getBookingScheduleRule/{page}/{limit}/{hoscode}/{depcode}")
    public R getBookingSchedule(
            @PathVariable("page") Integer page,
            @PathVariable("limit") Integer limit,
            @PathVariable("hoscode") String hoscode,
            @PathVariable("depcode") String depcode, HttpServletRequest request) {
        String userId = JwtUtils.getTokenById(request);
        if (StringUtils.isEmpty(userId)) {
            return R.error().message("未登录");
        }
        UserInfo userInfo = userFeignClient.VerifyUser(userId);
        if (userInfo == null) {
            return R.error().message("未登录");
        }
        if (userInfo.getStatus() == 0) {
            return R.error().message("用户已被锁定");
        }
        if (userInfo.getAuthStatus() != 2) {
            return R.error().message("用户未认证");
        }

        Map<String, Object> map = scheduleService.getBookingScheduleRule(page, limit, hoscode, depcode);
        return R.ok().data(map);
    }

    @ApiOperation(value = "获取排班数据")
    @GetMapping("auth/findScheduleList/{hoscode}/{depcode}/{workDate}")
    public R findScheduleList(
            @PathVariable("hoscode") String hoscode,
            @PathVariable("depcode") String depcode,
            @PathVariable("workDate") String workDate) {
        List<Schedule> scheduleList = scheduleService.getDetailSchedule(hoscode, depcode, workDate);
        return R.ok().data("scheduleList", scheduleList);
    }


    // 根据医院编号查询医院的详情(科室)
    @GetMapping("/department")
    public R getDepartListByHosCode(@RequestParam(value = "hoscode", required = false) String hoscode) {
        if (StringUtils.isEmpty(hoscode)) {
            return R.error().message("数据有误");
        }
        List<DepartmentVo> departmentList = departmentService.getDepartList(hoscode);
        return R.ok().data("list", departmentList);
    }

    // 根据医院编号查询医院的详情(基本信息)
    @GetMapping("/info")
    public R getHospitalInfo(@RequestParam(value = "hoscode", required = false) String hoscode) {
        if (StringUtils.isEmpty(hoscode)) {
            return R.error().message("数据有误");
        }
        Hospital byHosCode = hospitalService.findByHosCode(hoscode);
        return R.ok().data("list", byHosCode);
    }

    @GetMapping("/info/Details")
    public R getHospitalInfoDetails(@RequestParam(value = "hoscode", required = false) String hoscode) {
        if (StringUtils.isEmpty(hoscode)) {
            return R.error().message("数据有误");
        }
        Hospital byHosCode = hospitalService.findByHosCode(hoscode);
        Hospital hospital = new Hospital();
        hospital.setHoscode(byHosCode.getHoscode());
        hospital.setHosname(byHosCode.getHosname());
        hospital.setLogoData(byHosCode.getLogoData());
        hospital.setIntro(byHosCode.getIntro());
        hospital.setRoute(byHosCode.getRoute());
        hospital.setId(byHosCode.getId());
        hospital.setParam(Maps.newHashMap());

        return R.ok().data("Details", byHosCode);
    }

    // 主页展示数据
    @GetMapping("/list/{page}/{limit}")
    public R getIndexList(
            @PathVariable("limit") Integer limit,
            @PathVariable("page") Integer page,
            @RequestParam(value = "hostype", required = false) String hostype,
            @RequestParam(value = "districtCode", required = false) String districtCode) {
        if (!StringUtils.isEmpty(hostype) || !StringUtils.isEmpty(districtCode)) {
            List<ResultIndex> queryList = hospitalService.getQueryList(hostype, districtCode);
            return R.ok().data("list", queryList);
        } else {
            List<ResultIndex> list = hospitalService.getList(page, limit);
            return R.ok().data("list", list);
        }

    }


    // 首页展示数据
    @GetMapping("/{page}/{limit}")
    public R index(@PathVariable("limit") Integer limit,
                   @PathVariable("page") Integer page,
                   HospitalQueryVo hospitalQueryVo) {
        Map<String, Object> pageModel = hospitalService.getPageList(page, limit, hospitalQueryVo);
        return R.ok().data("pages", pageModel);
    }

    // 医院搜索
    @GetMapping("/findByHosName")
    public R HospIndexSearch(@RequestParam(value = "hosname", required = false) String hosname) {
        List<Hospital> list = hospitalService.selectVagueByHospName(hosname);
        return R.ok().data("list", list);
    }
}
