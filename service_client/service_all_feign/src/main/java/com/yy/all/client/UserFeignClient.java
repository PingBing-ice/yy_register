package com.yy.all.client;

import com.yy.util.result.R;
import com.yy.yygh.model.user.Patient;
import com.yy.yygh.model.user.UserInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author ice
 * @date 2022/8/13 15:56
 */
@FeignClient("service-user")
public interface UserFeignClient {
    // 更具userId 获取用户信息
    @GetMapping("/user/client/verifyUser")
    UserInfo VerifyUser(@RequestParam("userId") String userId);

    //根据id获取就诊人信息
    @GetMapping("/user/client/patient/auth/get/{id}")
     Patient getPatientClientById(@PathVariable String id);
}
