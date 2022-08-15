package com.yy.user.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yy.yygh.model.user.UserInfo;
import org.apache.ibatis.annotations.Mapper;


/**
 * <p>
 * 用户表 Mapper 接口
 * </p>
 *
 * @author ice
 * @since 2022-08-09
 */
public interface UserInfoMapper extends BaseMapper<UserInfo> {

    int selectAuthStatusById(String id);
}
