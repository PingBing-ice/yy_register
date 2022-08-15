package com.yy.all.client;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 数据字典API接口
 *
 * @author ice
 * @date 2022/8/4 18:44
 */
@FeignClient("service-cmn")
public interface DictFeignClient {

    /**
     * 获取数据字典名称
     *
     * @param parentDictCode 省份的编码
     * @param value 具体的值
     * @return 返回名字
     */
    @GetMapping("/cmn/dict/getName")
    String getName(
            @RequestParam(value = "parentDictCode", required = false, defaultValue = "") String parentDictCode,
            @RequestParam(value = "value", required = false, defaultValue = "") String value);
}
