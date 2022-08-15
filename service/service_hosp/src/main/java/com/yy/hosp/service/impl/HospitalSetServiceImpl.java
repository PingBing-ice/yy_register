package com.yy.hosp.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yy.hosp.mapper.HospitalSetMapper;
import com.yy.hosp.service.IHospitalSetService;
import com.yy.util.exception.RException;
import com.yy.yygh.model.hosp.HospitalSet;
import com.yy.yygh.vo.hosp.HospitalSetQueryVo;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 医院设置表 服务实现类
 * </p>
 *
 * @author baomidou
 * @since 2022-07-29
 */
@Service
public class HospitalSetServiceImpl extends ServiceImpl<HospitalSetMapper, HospitalSet> implements IHospitalSetService {


    @Override
    @Cacheable(value = "page",key = "#limit")
    public Map<String, Object> selectQueryByPage(long current, long limit, HospitalSetQueryVo hospitalSetQueryVo) {
        QueryWrapper<HospitalSet> wrapper = new QueryWrapper<>();
        if (current < 0 && limit <= 0) {
            throw new RException("数据为空");
        }
        if (hospitalSetQueryVo != null) {
            String hoscode = hospitalSetQueryVo.getHoscode();
            String hosname = hospitalSetQueryVo.getHosname();
            if (!StringUtils.isEmpty(hoscode)) {
                wrapper.like("hoscode", hoscode);
            }
            if (!StringUtils.isEmpty(hosname)) {
                wrapper.like("hosname", hosname);
            }
        }
        Page<HospitalSet> page = new Page<>(current, limit);
        baseMapper.selectPage(page, wrapper);
        long total = page.getTotal();
        List<HospitalSet> records = page.getRecords();
        Map<String, Object> map = new HashMap<>();
        map.put("total", total);
        map.put("records", records);
        return map;
    }

    /**
     *  获取签名
     * @param hoscode
     * @return
     */
    @Override
    public String getSignKey(String hoscode) {
        QueryWrapper<HospitalSet> wrapper = new QueryWrapper<>();
        wrapper.eq("hoscode", hoscode);
        HospitalSet hospitalSet = baseMapper.selectOne(wrapper);
        return hospitalSet.getSignKey();
    }
}
