package com.yy.msm.controller;

import com.yy.msm.service.MsmService;
import com.yy.util.result.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author ice
 * @date 2022/8/10 11:38
 */
@RestController
@RequestMapping("/msm")
@Slf4j
public class MsmController {

    @Resource
    private MsmService msmService;
    @GetMapping("/send")
    public R findShort(@RequestParam(required = false) String phone) {
        log.info(phone);
        if (StringUtils.isEmpty(phone)) {
            return R.error().message("手机号为空");
        }
//        boolean code =  msmService.sendCode(phone);
        if (true) {
            return R.ok();
        } else return R.error().message("验证码发送失败");

    }
}
