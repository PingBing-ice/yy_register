package com.yy.hosp.controller;

import com.yy.hosp.service.DepartmentService;
import com.yy.util.result.R;
import com.yy.yygh.vo.hosp.DepartmentVo;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 科室
 * @author ice
 * @date 2022/8/5 18:09
 */
@RequestMapping("/hosp/department")
@RestController
public class DepartmentController {
    @Autowired
    private DepartmentService departmentService;

    /**
     * 根据医院编号查询该医院地下所有的科室信息
     * @param hosCode 医院的编号
     * @return 大科室嵌套子科室
     */
    @ApiOperation(value = "查询医院所有科室列表")
    @GetMapping("getDeptList/{hoscode}")
    public R getAllDepart(@PathVariable("hoscode") String hosCode) {
        List<DepartmentVo> list= departmentService.getDepartList(hosCode);
        return R.ok().data("list",list);
    }
}
