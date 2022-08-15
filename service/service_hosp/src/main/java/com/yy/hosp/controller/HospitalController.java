package com.yy.hosp.controller;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.yy.hosp.service.HospitalService;
import com.yy.util.result.R;
import com.yy.yygh.model.hosp.Department;
import com.yy.yygh.model.hosp.Hospital;
import com.yy.yygh.vo.hosp.HospitalQueryVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @author ice
 * @date 2022/8/4 16:00
 */
@Api(description = "医院接口")
@RestController
@RequestMapping("/hosp/hospital")
@Log4j2
public class HospitalController {

    @Autowired
    private HospitalService hospitalService;


    /**
     *  根据条件分页查询所有的医院信息
     * @param page 当前页
     * @param limit 当前页查询的数量
     * @param hospitalQueryVo 封装了查询条件
     * @return 医院信息page
     */
    @ApiOperation(value = "获取分页列表")
    @GetMapping("{page}/{limit}")
    public R getPageList(@PathVariable("page") Integer page,
                         @PathVariable("limit") Integer limit, HospitalQueryVo hospitalQueryVo) {
        Map<String, Object> hospitals;
        if (StringUtils.isEmpty(hospitalQueryVo.getHoscode())&&StringUtils.isEmpty(hospitalQueryVo.getHosname())
                &&StringUtils.isEmpty(hospitalQueryVo.getCityCode())&&StringUtils.isEmpty(hospitalQueryVo.getDistrictCode())
                &&StringUtils.isEmpty(hospitalQueryVo.getHostype())&&
        hospitalQueryVo.getStatus() == null&& StringUtils.isEmpty(hospitalQueryVo.getProvinceCode())) {
            hospitals = hospitalService.getPageList(page, limit);
        }else {
            hospitals = hospitalService.getPageList(page, limit, hospitalQueryVo);
        }


        return R.ok().data("pages", hospitals);
    }

    @ApiOperation(value = "更新上线状态")
    @GetMapping("updateStatus/{id}/{status}/{page}")
    public R lock(
            @PathVariable("id") String id,
            @PathVariable("status") Integer status,
            @PathVariable("page") Integer page){

        hospitalService.updateStatus(id, status,page);
        return R.ok();
    }
    @ApiOperation(value = "获取医院详情")
    @GetMapping("/show/{id}")
    public R show(
            @PathVariable String id) {
        if (StringUtils.isEmpty(id)) {
            return R.error();
        }
        Map<String,Object> hospital =hospitalService.show(id);
        return R.ok().data("hospital",hospital);
    }

}
