package com.yy.cmn.service.impl;


import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.yy.cmn.excel.DictListener;
import com.yy.cmn.mapper.DictMapper;
import com.yy.cmn.service.IDictService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yy.util.exception.RException;
import com.yy.yygh.model.cmn.Dict;
import com.yy.yygh.vo.cmn.DictEeVo;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * <p>
 * 组织架构表 服务实现类
 * </p>
 *
 * @author ice
 * @since 2022-08-02
 */
@Service
public class DictServiceImpl extends ServiceImpl<DictMapper, Dict> implements IDictService {
    // 根据父id查询子元素列表
    @Override
    @Cacheable(value = "dict")
    public List<Dict> getDictListByPId(String pid) {
        if (StringUtils.isEmpty(pid)) {
            throw new RException("传入的数据为空");
        }
        Integer id = Integer.parseInt(pid);
        QueryWrapper<Dict> wrapper = new QueryWrapper<>();
        wrapper.eq("parent_id", id);
        List<Dict> dictList = baseMapper.selectList(wrapper);
        dictList.forEach(dict -> {
            // 判断子元素是有值
            boolean result = HasChild(dict);
            dict.setHasChildren(result);
        });
        return dictList;
    }
    // 文件下载
    @Override
    public void exportData(HttpServletResponse response) {
        try {
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("utf-8");
            // 这里URLEncoder.encode可以防止中文乱码 当然和easyexcel没有关系
            String fileName = URLEncoder.encode("数据字典", "UTF-8").replaceAll("\\+", "%20");
            response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");

            List<Dict> dictList = baseMapper.selectList(null);
            if (dictList.size() <= 0) {
                throw new RException("文件数据为空失败");
            }
            List<DictEeVo> dictEeVos = new CopyOnWriteArrayList<>();
            dictList.forEach(dict -> {
                DictEeVo dictEeVo = new DictEeVo();
                BeanUtils.copyProperties(dict, dictEeVo);
                dictEeVos.add(dictEeVo);
            });
            EasyExcel.write(response.getOutputStream(), DictEeVo.class).sheet().doWrite(dictEeVos);
        } catch (Exception e) {
            throw new RException("文件下载失败");
        }
    }

    private boolean HasChild(Dict dict) {
        QueryWrapper<Dict> wrapper = new QueryWrapper<>();
        wrapper.eq("parent_id", dict.getId());
        Long count = baseMapper.selectCount(wrapper);
        return count > 0;
    }
    // 文件上传
    @Override
    public void importData(MultipartFile file) {
        try {
            EasyExcel.read(file.getInputStream(), DictEeVo.class, new DictListener(this)).sheet().doRead();
        } catch (IOException e) {
            throw new RException("文件上传失败");
        }
    }
    //  根据上级编码与值获取数据字典名称
    @Override
    public String getNameByParentDictCodeAndValue(String parentDictCode, String value) {
        if (StringUtils.isEmpty(value)) {
            throw new RException("value的值为空");
        }
        QueryWrapper<Dict> wrapper;
        if (StringUtils.isEmpty(parentDictCode)) {
            wrapper = new QueryWrapper<>();
            wrapper.eq("value", value);
            Dict dict = baseMapper.selectOne(wrapper);
            if (dict == null) {
                throw new RException("查询失败");
            }
            return dict.getName();
        }
        wrapper = new QueryWrapper<>();

        wrapper.eq("dict_code", parentDictCode);
        Dict dict = baseMapper.selectOne(wrapper);
        if (dict == null) {
            throw new RException("查询失败");
        }
        Long parentId = dict.getId();
        wrapper = new QueryWrapper<>();
        wrapper.eq("parent_id", parentId);
        wrapper.eq("value", value);
        dict = baseMapper.selectOne(wrapper);
        if (dict == null) {
            throw new RException("查询失败");
        }
        return dict.getName();
    }

    /**
     * 用于页面条件查询，多级联动
     *
     * @param dictCode 类型的编码
     * @return 返回列表数据
     */
    @Override
    @Cacheable(value = "findByDictCode",key = "#dictCode")
    public List<Dict> findByDictCode(String dictCode) {
        // 根据 dictCode 查询出id
        QueryWrapper<Dict> wrapper = new QueryWrapper<>();
        wrapper.eq("dict_code", dictCode);
        Dict dict = baseMapper.selectOne(wrapper);
        if (dict == null) {
            throw new RException("查无数据");
        }
        Long parentId = dict.getId();
        wrapper = new QueryWrapper<>();
        wrapper.eq("parent_id", parentId);
        List<Dict> dictList = baseMapper.selectList(wrapper);
        if (dictList.size() == 0) {
            throw new RException("查无数据");
        }
        return dictList;
    }
}
