package com.yy.oss.controller;

import com.yy.oss.service.OssService;
import com.yy.util.result.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;

/**
 * @author ice
 * @date 2022/8/11 12:20
 */
@RestController
@RequestMapping("/oss")
@Slf4j
public class OssController {
    @Resource
    private OssService ossService;

    @PostMapping("/file/upload")
    public R upFile(MultipartFile file) {
        log.info("上传文件");
        String url = ossService.upload(file);
        return R.ok().data("url", url);
    }
}
