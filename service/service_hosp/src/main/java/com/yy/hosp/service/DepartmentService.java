package com.yy.hosp.service;

import com.yy.yygh.model.hosp.Department;
import com.yy.yygh.vo.hosp.DepartmentVo;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

/**
 * @author ice
 * @date 2022/8/4 10:27
 */

public interface DepartmentService {


    /**
     *  分页查找科室
     * @param paramMap
     * @return
     */
    Page<Department> findDepartmentPage(Map<String, Object> paramMap);


    /**
     * 保存科室
     * @param map
     */
    void save(Map<String, Object> map);

    /**
     * 删除科室
     * @param map
     */
    void removeDepartment(Map<String, Object> map);

    /**
     * 根据医院编号查询该医院地下所有的科室信息
     * @param hosCode 医院的编号
     * @return 大科室嵌套子科室
     */
    List<DepartmentVo> getDepartList(String hosCode);

    Department getDepartment(String hoscode, String depcode);
}
