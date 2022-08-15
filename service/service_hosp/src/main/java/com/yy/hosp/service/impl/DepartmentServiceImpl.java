package com.yy.hosp.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.yy.hosp.repository.DepartmentRepository;
import com.yy.hosp.service.DepartmentService;
import com.yy.util.exception.RException;
import com.yy.yygh.model.hosp.Department;
import com.yy.yygh.vo.hosp.DepartmentVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author ice
 * @date 2022/8/4 10:27
 */
@Service
@Slf4j
public class DepartmentServiceImpl implements DepartmentService {
    @Autowired
    private DepartmentRepository departmentRepository;

    @Override
    public Page<Department> findDepartmentPage(Map<String, Object> paramMap) {
        String hoscode = (String) paramMap.get("hoscode");
        String page = (String) paramMap.get("page");
        String limit = (String) paramMap.get("limit");

        Department department = new Department();
        department.setHoscode(hoscode);

        Sort sort = Sort.by(Sort.Direction.ASC, "createTime");
        Example<Department> example = Example.of(department);
        PageRequest pageRequest = PageRequest.of(Integer.parseInt(page), Integer.parseInt(limit), sort);
        return departmentRepository.findAll(example, pageRequest);

    }

    @Override
    public void save(Map<String, Object> map) {
        String jsonString = JSONObject.toJSONString(map);
        Department department = JSONObject.parseObject(jsonString, Department.class);
        String hoscode = department.getHoscode();
        String depcode = department.getDepcode();
        Department tageDepartment = departmentRepository.findByHoscodeAndDepcode(hoscode, depcode);
        if (tageDepartment != null) {
            department.setCreateTime(tageDepartment.getCreateTime());
            department.setUpdateTime(new Date());
            department.setIsDeleted(0);
            department.setId(tageDepartment.getId());
            departmentRepository.save(department);
        } else {
            department.setCreateTime(new Date());
            department.setUpdateTime(new Date());
            department.setIsDeleted(0);
            departmentRepository.save(department);
        }
    }

    @Override
    public void removeDepartment(Map<String, Object> map) {
        String hoscodes = (String) map.get("hoscode");
        String depcode = (String) map.get("depcode");
        Department department = departmentRepository.findByHoscodeAndDepcode(hoscodes, depcode);
        if (department == null) {
            throw new RException("查找科室失败");
        }
        String id = department.getId();
        departmentRepository.deleteById(id);
    }

    /**
     * 根据医院编号查询该医院地下所有的科室信息
     *
     * @param hosCode 医院的编号
     * @return 大科室嵌套子科室
     */
    @Override
    @Cacheable(value = "depart",key = "#hosCode")
    public List<DepartmentVo> getDepartList(String hosCode) {
        // 通过医院的编号查询出所有的科室信息
        List<Department> departmentList = departmentRepository.findAllByHoscode(hosCode);
        // 通过大科室的编号进行分组,key 是大科室的编号
        Map<String, List<Department>> map = departmentList.stream().collect(Collectors.groupingBy(Department::getBigcode));
        // 设置最终返回去的列表
        List<DepartmentVo> departmentVos = new ArrayList<>();
        // 遍历map 集合
        for (Map.Entry<String, List<Department>> entry : map.entrySet()) {
            // 大科室的对象
            DepartmentVo departmentVo = new DepartmentVo();
            // 获取大科室的编号
            String BigCode = entry.getKey();
            departmentVo.setDepcode(BigCode);
            // 大科室对应的所有子科室的列表列表
            List<Department> smallDepart = entry.getValue();
            // 通过第一个子科室列表获取大科室的名字
            String bigName = smallDepart.get(0).getBigname();
            departmentVo.setDepname(bigName);

            // 设置大科室对应的 children 属性
            List<DepartmentVo> children = new ArrayList<>();
            // 遍历子科室
            for (Department department : smallDepart) {
                // 创建子科室的对象
                DepartmentVo departmentVo1 = new DepartmentVo();
                // 获取子科室的编号
                String depCode = department.getDepcode();
                // 获取子科室的名字
                String depName = department.getDepname();
                departmentVo1.setDepname(depName);
                departmentVo1.setDepcode(depCode);
                // 将子科室添加到 children 里面
                children.add(departmentVo1);
            }
            // 设置children 属性
            departmentVo.setChildren(children);

            departmentVos.add(departmentVo);
        }
        return departmentVos;
    }

    @Override
    public Department getDepartment(String hoscode, String depcode) {

        return departmentRepository.findByHoscodeAndDepcode(hoscode, depcode);
    }
}
