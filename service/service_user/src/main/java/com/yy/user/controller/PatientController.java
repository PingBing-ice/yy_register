package com.yy.user.controller;

import com.yy.user.service.IPatientService;
import com.yy.util.result.R;
import com.yy.util.utils.JwtUtils;
import com.yy.yygh.model.user.Patient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.stereotype.Controller;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.List;

/**
 * <p>
 * 就诊人表 前端控制器
 * </p>
 *
 * @author ice
 * @since 2022-08-09
 */
@RestController
@RequestMapping("/user/patient")
public class PatientController {

    @Autowired
    private IPatientService patientService;


    /**
     *  保存就诊人信息
     * @param patient
     * @param request
     * @return
     */
    @PostMapping("/auth/save")
    public R savePatient(@RequestBody Patient patient, HttpServletRequest request) {
        if (patient == null) {
            return R.error().message("请填写数据");
        }
        String id = JwtUtils.getTokenById(request);
        if (StringUtils.isEmpty(id)) {
            return R.error().message("数据有误,请重新登录");
        }
        patient.setUserId(id);
        boolean save = patientService.save(patient);
        if (!save) {
            return R.error().message("保存失败");
        }
        return R.ok();
    }

    // 更具就诊人id进行删除
    @DeleteMapping("/auth/remove/{id}")
    public R removePatient(@PathVariable String id) {
        boolean remove = patientService.removeById(id);
        if (remove) {
            return R.ok();
        } else return R.error().message("删除失败");
    }

    // 更具就诊人进行修改
    @PostMapping("/auth/update")
    public R updatePatient(@RequestBody Patient patient) {
        if (patient == null) {
            return R.error().message("请填写数据");
        }
        patientService.updateById(patient);
        return R.ok();
    }

    //根据id获取就诊人信息
    @GetMapping("/auth/get/{id}")
    public R getPatient(@PathVariable String id) {
        Patient patient = patientService.getPatientId(id);
        return R.ok().data("patient",patient);
    }


    //获取就诊人列表
    @GetMapping("/auth/findAll")
    public R findAll(HttpServletRequest request) {
        List<Patient> patientList = patientService.selectByUserIdAllPatient(request);
        return R.ok().data("list", patientList);
    }
}
