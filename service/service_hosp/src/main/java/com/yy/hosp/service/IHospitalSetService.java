package com.yy.hosp.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.yy.yygh.model.hosp.HospitalSet;
import com.yy.yygh.vo.hosp.HospitalSetQueryVo;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * <p>
 * 医院设置表 服务类
 * </p>
 *
 * @author baomidou
 * @since 2022-07-29
 */

public interface IHospitalSetService extends IService<HospitalSet> {
    /**
     *  更具查询条件查询消息
     * @param current 当前页
     * @param limit 总页数
     * @param hospitalSetQueryVo 查询条件
     * @return 返回数据
     */
    Map<String, Object> selectQueryByPage(long current, long limit, HospitalSetQueryVo hospitalSetQueryVo);

    String getSignKey(String hoscode);
}
