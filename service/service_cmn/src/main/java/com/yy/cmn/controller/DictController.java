package com.yy.cmn.controller;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.yy.cmn.service.IDictService;
import com.yy.util.result.R;
import com.yy.yygh.model.cmn.Dict;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * <p>
 * 组织架构表 前端控制器
 * </p>
 *
 * @author ice
 * @since 2022-08-02
 */
@RestController
@RequestMapping("/cmn/dict")
public class DictController {

    @Autowired
    private IDictService dictService;

    /**
     * 根据父id查询子元素列表
     *
     * @param pid 父id
     * @return 数据
     */
    @GetMapping("/childList/{pid}")
    public R getDictListByPId(@PathVariable("pid") String pid) {
        if (StringUtils.isEmpty(pid)) {
            return R.error().message("传入的数据为空");
        }
        List<Dict> dictList = dictService.getDictListByPId(pid);
        return R.ok().data("list", dictList);
    }

    /**
     * 文件导入 文件下载
     *
     * @param response 数据
     */
    @GetMapping("/exportData")
    public void exportData(HttpServletResponse response) {
        dictService.exportData(response);
    }

    /**
     * 文件上传
     *
     * @param file 文件流
     * @return 返回
     */
    @PostMapping("/importData")
    public R importData(MultipartFile file) {
        dictService.importData(file);
        return R.ok();
    }


    @ApiOperation(value = "获取数据字典名称")
    @GetMapping(value = "/getName")
    public String getName(
            @RequestParam(value = "parentDictCode", required = false, defaultValue = "") String parentDictCode,
            @RequestParam(value = "value", required = false, defaultValue = "") String value) {
        return dictService.getNameByParentDictCodeAndValue(parentDictCode, value);
    }

    /**
     * 用于页面条件查询，多级联动
     *
     * @param dictCode 类型的编码
     * @return 返回列表数据
     */
    @ApiOperation(value = "根据dictCode获取下级节点")
    @GetMapping("/findByDictCode/{dictCode}")
    public R findByDictCode(@PathVariable String dictCode) {
        if (StringUtils.isEmpty(dictCode)) {
            return R.error().message("传入的数据为空");
        }
        List<Dict> dictList = dictService.findByDictCode(dictCode);
        return R.ok().data("dictList", dictList);
    }
}
