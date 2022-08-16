package com.yy.user.controller.userclient;

import com.yy.user.service.IPatientService;
import com.yy.util.result.R;
import com.yy.yygh.model.user.Patient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * @author ice
 * @date 2022/8/15 11:52
 */
@RestController
@RequestMapping("/user/client/patient")
public class PatientClientController {
    @Autowired
    private IPatientService patientService;

    //根据id获取就诊人信息
    @GetMapping("/auth/get/{id}")
    public Patient getPatientClientById(@PathVariable String id) {
        return patientService.getPatientId(id);
    }
}
