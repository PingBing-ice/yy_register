package com.yy.cmn.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.yy.yygh.model.cmn.Dict;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * <p>
 * 组织架构表 服务类
 * </p>
 *
 * @author ice
 * @since 2022-08-02
 */
public interface IDictService extends IService<Dict> {
    /**
     * 根据父id查询子元素列表
     *
     * @param pid 父id
     * @return 数据
     */
    List<Dict> getDictListByPId(String pid);

    /**
     * 文件下载
     * @param response
     */
    void exportData(HttpServletResponse response);

    /**
     * 文件上传
     * @param file
     */
    void importData(MultipartFile file);

    /**
     * 根据上级编码与值获取数据字典名称
     * @param parentDictCode
     * @param value
     */
    String getNameByParentDictCodeAndValue(String parentDictCode, String value);

    /**
     * 用于页面条件查询，多级联动
     *
     * @param dictCode 类型的编码
     * @return 返回列表数据
     */
    List<Dict> findByDictCode(String dictCode);
}
