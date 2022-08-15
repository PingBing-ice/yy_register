package com.yy.user.controller;

import cn.hutool.jwt.JWTUtil;
import com.yy.user.service.IUserInfoService;
import com.yy.util.result.R;
import com.yy.util.utils.JwtUtils;
import com.yy.yygh.model.user.UserInfo;
import com.yy.yygh.vo.user.LoginVo;
import com.yy.yygh.vo.user.UserAuthVo;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * 用户表 前端控制器
 * </p>
 *
 * @author ice
 * @since 2022-08-09
 */
@RestController
@RequestMapping("/user/userInfo")
public class UserInfoController {

    @Resource
    private IUserInfoService userInfoService;

    /**
     *  保存用户的认证信息
     * @param userAuthVo
     * @param request
     * @return
     */
    @PostMapping("/auth/userAuth")
    public R saveUserAuth(@RequestBody UserAuthVo userAuthVo, HttpServletRequest request) {
        boolean  userAuth= userInfoService.saveUserAuth(userAuthVo, request);
        if (userAuth) {
            return R.ok();
        } else return R.error().message("认证失败");
    }

    // 实名认证查询
    @GetMapping("/auth/getUserInfo")
    public R getUserInfo(HttpServletRequest request) {
        String id = JwtUtils.getTokenById(request);
        if (StringUtils.isEmpty(id)) {
            R.error().message("数据为空,请重新登录");
        }
        UserInfo userInfo = userInfoService.getById(id);
        if (userInfo == null) {
            return R.error().message("数据为空,请重新登录");
        }
        userInfoService.selectByAuthStatus(userInfo);
//        UserInfo newUserInfo = userInfoService.newUserInfo(userInfo);
        return R.ok().data("userInfo", userInfo);
    }


    /**
     *  手机号登录
     * @param loginVo vo
     * @return fanhui成功
     */
    @PostMapping("/login")
    public R login(@RequestBody(required = false) LoginVo loginVo) {
        if (loginVo == null) {
            return R.error().message("数据错误");
        }
       Map<String,Object> map = userInfoService.login(loginVo);
        return R.ok().data(map);
    }
}
