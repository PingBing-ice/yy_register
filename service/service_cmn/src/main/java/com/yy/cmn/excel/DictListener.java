package com.yy.cmn.excel;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.metadata.data.ReadCellData;
import com.alibaba.excel.read.listener.ReadListener;
import com.alibaba.excel.util.ListUtils;
import com.yy.cmn.service.IDictService;
import com.yy.util.exception.RException;
import com.yy.yygh.model.cmn.Dict;
import com.yy.yygh.vo.cmn.DictEeVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;

import java.util.List;
import java.util.Map;

/**
 * @author ice
 * @date 2022/8/2 15:57
 */
@Slf4j
public class DictListener implements ReadListener<DictEeVo> {
    private final IDictService dictService;
    private List<Dict> dictList = ListUtils.newArrayListWithExpectedSize(BATCH_COUNT);
    private static final int BATCH_COUNT = 5;

    public DictListener(IDictService dictService) {
        this.dictService = dictService;
    }

    @Override
    public void invoke(DictEeVo dictEeVo, AnalysisContext analysisContext) {
        Dict dict = new Dict();
        BeanUtils.copyProperties(dictEeVo,dict);
        dictList.add(dict);
        if (dictList.size() >= BATCH_COUNT) {
            boolean saveBatch = dictService.saveBatch(dictList);
            // 存储完成清理 list
            dictList = ListUtils.newArrayListWithExpectedSize(BATCH_COUNT);
            if (!saveBatch) {
                throw new RException("文件上传失败");
            }
        }

    }

    @Override
    public void invokeHead(Map<Integer, ReadCellData<?>> headMap, AnalysisContext context) {

    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {
        saveData();
    }
    private void saveData() {
        log.info("{}条数据，开始存储数据库！", dictList.size());
        dictService.saveBatch(dictList);
        log.info("存储数据库成功！");
    }
}
