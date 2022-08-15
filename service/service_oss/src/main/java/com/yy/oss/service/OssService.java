package com.yy.oss.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * @author ice
 * @date 2022/8/11 12:21
 */

public interface OssService {
    /**
     * 文件上传至阿里云
     */
    String upload(MultipartFile file);
}
