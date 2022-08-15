package com.yy.hosp.service.impl;


import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.yy.all.client.DictFeignClient;
import com.yy.hosp.repository.HospitalRepository;
import com.yy.hosp.service.HospitalService;
import com.yy.hosp.vo.ResultIndex;
import com.yy.yygh.enums.DictEnum;
import com.yy.yygh.model.hosp.Hospital;
import com.yy.yygh.vo.hosp.HospitalQueryVo;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.*;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

/**
 * @author ice
 * @date 2022/8/3 12:35
 */
@Service
@Log4j2
public class HospitalServiceImpl implements HospitalService {
    @Autowired
    private HospitalRepository repository;

    @Autowired
    private DictFeignClient dictFeignClient;

    @Autowired
    private RedisTemplate redisTemplate;


    @Override
    public void save(Map<String, Object> resultMap) {
        // 将Map对象传换成 对象

        String jsonString = JSONObject.toJSONString(resultMap);
        Hospital hospital = JSONObject.parseObject(jsonString, Hospital.class);

        // 根据Hoscode 查找
        Hospital targetHospital = repository.findByHoscode(hospital.getHoscode());
        log.info(targetHospital);
        // 添加到MongoDB 如果有就进行跟新操作
        if (targetHospital == null) {
            // 添加
            // 未上线
            hospital.setStatus(0);
            hospital.setCreateTime(new Date());
            hospital.setUpdateTime(new Date());
            hospital.setIsDeleted(0);
            repository.save(hospital);

        } else {
            // 跟新
            hospital.setId(targetHospital.getId()); //根据id更新
            hospital.setStatus(targetHospital.getStatus());
            hospital.setCreateTime(targetHospital.getCreateTime());
            hospital.setUpdateTime(new Date());
            hospital.setIsDeleted(targetHospital.getIsDeleted());
            repository.save(hospital);
        }
    }

    @Override
    @Cacheable(value = "findByHosCode", key = "#hoscode")
    public Hospital findByHosCode(String hoscode) {
        return repository.findByHoscode(hoscode);
    }

    @Override
    @Cacheable(value = "hopsPageList", key = "#page+'=='+#limit")
    public Map<String, Object> getPageList(Integer page, Integer limit) {

        PageRequest pageRequest = PageRequest.of(page - 1, limit);

        Page<Hospital> all = repository.findAll(pageRequest);
        // 所有的医院信息进行封装
        all.getContent().forEach(this::packHospital);
        List<Hospital> content = all.getContent();
        int totalPages = all.getTotalPages();
        Map<String, Object> map = new HashMap<>();
        map.put("content", content);
        map.put("totalPages", totalPages);
        return map;
    }


    public Map<String,Object> getPageList(Integer page, Integer limit, HospitalQueryVo hospitalQueryVo) {
        Hospital hospital = new Hospital();
        BeanUtils.copyProperties(hospitalQueryVo, hospital);


        Sort sort = Sort.by(Sort.Direction.ASC, "crateTime");
        PageRequest pageRequest = PageRequest.of(page - 1, limit, sort);
        //创建匹配器，即如何使用查询条件
        ExampleMatcher matcher = ExampleMatcher.matching() //构建对象
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING) //改变默认字符串匹配方式：模糊查询
                .withIgnoreCase(true); //改变默认大小写忽略方式：忽略大小写
        Example<Hospital> example = Example.of(hospital, matcher);
        Page<Hospital> allAndQuery = repository.findAll(example, pageRequest);
        // 所有的医院信息进行封装
        List<Hospital> hospitals = allAndQuery.getContent();
        hospitals.forEach(this::packHospital);

        int totalPages = allAndQuery.getTotalPages();
        Map<String, Object> map = new HashMap<>();
        map.put("content", hospitals);
        map.put("totalPages", totalPages);
        return map;
    }

    @Override
    @CacheEvict(value = "hopsPageList",key = "#page")
    public void updateStatus(String id, Integer status,Integer page) {
        if (status == 0 || status == 1) {
            Hospital hospital = repository.findById(id).get();
            hospital.setStatus(status);
            hospital.setUpdateTime(new Date());
            repository.save(hospital);
        }
    }

    @Override
    public Map<String, Object> show(String id) {
        Map<String, Object> map = new HashMap<>();
        Optional<Hospital> optional = repository.findById(id);
        Hospital hospital = this.packHospital(optional.get());
        map.put("hospital", hospital);
        map.put("bookingRule", hospital.getBookingRule());
        return map;
    }

    // 远程调用接口
    public Hospital packHospital(Hospital item) {
        String HostName = dictFeignClient.getName(DictEnum.HOSTYPE.getDictCode(), item.getHostype());
        String provinceCode = dictFeignClient.getName("", item.getProvinceCode());
        String cityCode = dictFeignClient.getName("", item.getCityCode());
        String districtCode = dictFeignClient.getName("", item.getDistrictCode());

        item.getParam().put("hostypeString", HostName);
        item.getParam().put("fullAddress", provinceCode + cityCode + districtCode + item.getAddress());
        return item;
    }

    @Override
    public List<Hospital> selectVagueByHospName(String hosname) {
        List<Hospital> list = repository.findByHosnameLike(hosname);
        return list;
    }

    @Override
    @Cacheable(value = "index", key = "#limit")
    public List<ResultIndex> getList(Integer page, Integer limit) {

        Sort sort = Sort.by(Sort.Direction.ASC, "crateTime");
        PageRequest pageRequest = PageRequest.of(page - 1, limit, sort);

        Page<Hospital> all = repository.findAll(pageRequest);
        List<Hospital> content = all.getContent();

        List<ResultIndex> resultIndices = getResultList(content);
        log.info(resultIndices.size() + "=======================================================================");
        return resultIndices;
    }

    @Override
    public List<ResultIndex> getQueryList(String hostype, String districtCode) {
        Sort sort = Sort.by(Sort.Direction.ASC, "crateTime");
        //创建匹配器，即如何使用查询条件
        ExampleMatcher matcher = ExampleMatcher.matching() //构建对象
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING) //改变默认字符串匹配方式：模糊查询
                .withIgnoreCase(true); //改变默认大小写忽略方式：忽略大小写

        Hospital hospital = new Hospital();
        if (!StringUtils.isEmpty(districtCode)) {
            hospital.setDistrictCode(districtCode);
        }
        if (!StringUtils.isEmpty(hostype)) {
            hospital.setHostype(hostype);
        }
        Example<Hospital> example = Example.of(hospital, matcher);
        List<Hospital> all = repository.findAll(example);
        List<ResultIndex> resultList = getResultList(all);
        return resultList;

    }

    public List<ResultIndex> getResultList(List<Hospital> list) {
        List<ResultIndex> resultIndices = new CopyOnWriteArrayList<>();
        list.parallelStream().forEach(hospital -> {
            String hoscode = hospital.getHoscode();
            String hosname = hospital.getHosname();
            String hosTypeString = dictFeignClient.getName(DictEnum.HOSTYPE.getDictCode(), hospital.getHostype());
            String releaseTime = hospital.getBookingRule().getReleaseTime();
            String logoData = hospital.getLogoData();
            String id = hospital.getId();


            ResultIndex resultIndex = new ResultIndex();
            resultIndex.setId(id);
            resultIndex.setHoscode(hoscode);
            resultIndex.setHosname(hosname);
            resultIndex.setHosTypeString(hosTypeString);
            resultIndex.setReleaseTime(releaseTime);
            resultIndex.setLogoData(logoData);
            resultIndices.add(resultIndex);
        });
        return resultIndices;
    }

    @Override
    public Hospital getByHoscode(String hoscode) {

        return repository.findByHoscode(hoscode);
    }
}
