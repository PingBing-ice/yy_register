package com.yy.user.controller.userclient;

import com.yy.user.service.IUserInfoService;
import com.yy.util.result.R;
import com.yy.util.utils.JwtUtils;
import com.yy.yygh.model.user.UserInfo;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * @author ice
 * @date 2022/8/13 15:58
 */
@RestController
@RequestMapping("/user/client")
public class UserClientController {
    @Resource
    private IUserInfoService userInfoService;

    @GetMapping("/verifyUser")
    public UserInfo VerifyUser(@RequestParam("userId") String userId) {
        return userInfoService.getById(userId);
    }
}
