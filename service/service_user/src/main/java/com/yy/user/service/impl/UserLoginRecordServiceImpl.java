package com.yy.user.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yy.user.mapper.UserLoginRecordMapper;
import com.yy.user.service.IUserLoginRecordService;
import com.yy.yygh.model.user.UserLoginRecord;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户登录记录表 服务实现类
 * </p>
 *
 * @author ice
 * @since 2022-08-09
 */
@Service
public class UserLoginRecordServiceImpl extends ServiceImpl<UserLoginRecordMapper, UserLoginRecord> implements IUserLoginRecordService {

}
