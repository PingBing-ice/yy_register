package com.yy.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yy.yygh.model.user.UserInfo;
import com.yy.yygh.vo.user.LoginVo;
import com.yy.yygh.vo.user.UserAuthVo;
import com.yy.yygh.vo.user.UserInfoQueryVo;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * <p>
 * 用户表 服务类
 * </p>
 *
 * @author ice
 * @since 2022-08-09
 */
public interface IUserInfoService extends IService<UserInfo> {

    Map<String, Object> login(LoginVo loginVo);

    UserInfo newUserInfo(UserInfo userInfo);

    UserInfo selectByOpenId(String openid);

    /**
     * 身份证验证情况
     * @param userInfo 过滤
     */
    void selectByAuthStatus(UserInfo userInfo);

    boolean saveUserAuth(UserAuthVo userAuthVo, HttpServletRequest request);


    //用户管理列表（条件查询带分页）
    Map<String, Object> selectPageList(int page, int limit, UserInfoQueryVo userInfoQueryVo);

    /**
     * 用户锁定
     * @param userId 用户id
     * @param status 状态
     */
    void lock(String userId, Integer status);

    Map<String, Object> show(String userId);

    void approval(String userId, Integer authStatus);
}
