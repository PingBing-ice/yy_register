package com.yy.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yy.yygh.model.user.Patient;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * <p>
 * 就诊人表 服务类
 * </p>
 *
 * @author ice
 * @since 2022-08-09
 */
public interface IPatientService extends IService<Patient> {

    Patient getPatientId(String id);

    List<Patient> selectByUserIdAllPatient(HttpServletRequest request);
    List<Patient> selectByUserIdAllPatient(String userId);
}
