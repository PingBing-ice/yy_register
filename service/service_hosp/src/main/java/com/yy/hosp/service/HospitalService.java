package com.yy.hosp.service;

import com.yy.hosp.vo.ResultIndex;
import com.yy.yygh.model.hosp.Hospital;
import com.yy.yygh.vo.hosp.HospitalQueryVo;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

/**
 * @author ice
 * @date 2022/8/3 12:34
 */

public interface HospitalService {
    void save(Map<String, Object> resultMap);

    Hospital findByHosCode(String hoscode);

    Map<String,Object> getPageList(Integer page, Integer limit);

    Map<String,Object> getPageList(Integer page, Integer limit,HospitalQueryVo hospitalQueryVo);

    void updateStatus(String id, Integer status,Integer page);

    Map<String,Object> show(String id);


    List<Hospital> selectVagueByHospName(String hosname);


    List<ResultIndex> getList(Integer page,Integer limit);

    List<ResultIndex> getQueryList(String hostype,String districtCode);

    Hospital getByHoscode(String hoscode);
}
