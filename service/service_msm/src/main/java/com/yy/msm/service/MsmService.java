package com.yy.msm.service;

import com.yy.yygh.vo.msm.MsmVo;
import org.springframework.stereotype.Service;

/**
 * @author ice
 * @date 2022/8/10 11:48
 */

public interface MsmService {
    /**
     *  短信验证
     * @param phone 手机号
     * @return 结构
     */
    boolean sendCode(String phone);

    void send(MsmVo msmVo);
}
