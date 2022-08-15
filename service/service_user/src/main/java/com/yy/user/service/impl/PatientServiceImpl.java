package com.yy.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yy.all.client.DictFeignClient;
import com.yy.user.mapper.PatientMapper;
import com.yy.user.service.IPatientService;
import com.yy.util.exception.RException;
import com.yy.util.utils.JwtUtils;
import com.yy.yygh.enums.DictEnum;
import com.yy.yygh.model.user.Patient;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * <p>
 * 就诊人表 服务实现类
 * </p>
 *
 * @author ice
 * @since 2022-08-09
 */
@Service
public class PatientServiceImpl extends ServiceImpl<PatientMapper, Patient> implements IPatientService {


    @Resource
    private DictFeignClient dictFeignClient;


    private QueryWrapper<Patient> wrapper;

    // 根据就诊人id获取信息
    @Override
    public Patient getPatientId(String id) {
        Patient patient = baseMapper.selectById(id);
        return this.packPatient(patient);

    }


    @Override
    public List<Patient> selectByUserIdAllPatient(HttpServletRequest request) {

        String userId = JwtUtils.getTokenById(request);
        if (StringUtils.isEmpty(userId)) {
            throw new RException("数据有误,亲重新登录");
        }
        QueryWrapper<Patient> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);
        List<Patient> patientList = baseMapper.selectList(wrapper);
        if (patientList.size() <= 0) {
            return null;
        }
        patientList.forEach(this::packPatient);
        return patientList;
    }

    @Override
    public List<Patient> selectByUserIdAllPatient(String userId) {
        wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);
        List<Patient> patientList = baseMapper.selectList(wrapper);
        if (patientList.size() <= 0) {
            return null;
        }
        patientList.forEach(this::packPatient);
        return patientList;
    }

    //Patient对象里面其他参数封装
    private Patient packPatient(Patient patient) {
        //根据证件类型编码，获取证件类型具体指
        String certificatesTypeString =
                dictFeignClient.getName(DictEnum.CERTIFICATES_TYPE.getDictCode(), patient.getCertificatesType());//联系人证件
        //联系人证件类型
        // String contactsCertificatesTypeString =dictFeignClient.getName(DictEnum.CERTIFICATES_TYPE.getDictCode(),patient.getContactsCertificatesType());
        //省
        String provinceString = dictFeignClient.getName("", patient.getProvinceCode());
        //市
        String cityString = dictFeignClient.getName("", patient.getCityCode());
        //区
        String districtString = dictFeignClient.getName("", patient.getDistrictCode());
        patient.getParam().put("certificatesTypeString", certificatesTypeString);
        // patient.getParam().put("contactsCertificatesTypeString", contactsCertificatesTypeString);
        patient.getParam().put("provinceString", provinceString);
        patient.getParam().put("cityString", cityString);
        patient.getParam().put("districtString", districtString);
        patient.getParam().put("fullAddress", provinceString + cityString + districtString + patient.getAddress());
        return patient;
    }
}
