package com.yy.user.service.impl;

import cn.hutool.core.util.IdcardUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yy.user.mapper.UserInfoMapper;
import com.yy.user.service.IPatientService;
import com.yy.user.service.IUserInfoService;
import com.yy.util.exception.RException;
import com.yy.util.utils.JwtUtils;
import com.yy.yygh.model.user.Patient;
import com.yy.yygh.model.user.UserInfo;
import com.yy.yygh.vo.user.LoginVo;
import com.yy.yygh.vo.user.UserAuthVo;
import com.yy.yygh.vo.user.UserInfoQueryVo;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @author ice
 * @since 2022-08-09
 */
@Service
public class UserInfoServiceImpl extends ServiceImpl<UserInfoMapper, UserInfo> implements IUserInfoService {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private IPatientService patientService;

    @Override
    @Transactional
    public Map<String, Object> login(LoginVo loginVo) {
        String phone = loginVo.getPhone();
        String code = loginVo.getCode();
        if (StringUtils.isEmpty(phone) || StringUtils.isEmpty(code)) {
            throw new RException("用户名和验证码不能为空");
        }
        Pattern compile = Pattern.compile("^((13[0-9])|(14[0,1,4-9])|(15[0-3,5-9])|(16[2,5,6,7])|(17[0-8])|(18[0-9])|(19[0-3,5-9]))\\d{8}$");
        boolean matches = compile.matcher(phone).matches();
        if (!matches) {
            throw new RException("手机号不合法");
        }
        if (code.length() != 4) {
            throw new RException("验证码错误");
        }
        String redisCode = stringRedisTemplate.opsForValue().get(phone);
        //  验证码 验证
        if (StringUtils.isEmpty(redisCode)) {
            throw new RException("验证码错误");
        }
        if (!redisCode.equals(code)) {
            throw new RException("验证码错误");
        }
        HashMap<String, Object> map = new HashMap<>();
        if (!StringUtils.isEmpty(loginVo.getOpenid())) {
            // 绑定手机号
            QueryWrapper<UserInfo> wrapper = new QueryWrapper<>();
            wrapper.eq("openid", loginVo.getOpenid());
            UserInfo userInfo = baseMapper.selectOne(wrapper);
            if (userInfo == null) {
                throw new RException("绑定失败");
            }
            userInfo.setPhone(phone);
            wrapper = new QueryWrapper<>();
            wrapper.eq("phone", phone);
            int delete = baseMapper.delete(wrapper);
            if (delete > 1) {
                throw new RException("绑定失败");
            }
            baseMapper.updateById(userInfo);

            String token = JwtUtils.createToken(userInfo.getId(), userInfo.getNickName());
            map.put("name", userInfo.getNickName());
            map.put("token", token);
            return map;
        }


        QueryWrapper<UserInfo> wrapper = new QueryWrapper<>();
        wrapper.eq("phone", phone);
        UserInfo userInfo = baseMapper.selectOne(wrapper);

        String name;
        String id;
        if (userInfo != null) {
            if (userInfo.getStatus() == 0) {
                throw new RException("用户已经拉黑");
            }
            name = userInfo.getName();
            if (StringUtils.isEmpty(name)) {
                name = userInfo.getNickName();
            }
            if (StringUtils.isEmpty(name)) {
                name = userInfo.getPhone();
            }
            id = userInfo.getId();
        } else {
            // 首次登陆注册
            UserInfo info = new UserInfo();
            info.setPhone(phone);
            baseMapper.insert(info);
            name = phone;
            id = info.getId();
        }
        map.put("name", name);
        String token = JwtUtils.createToken(id, name);
        map.put("token", token);
        return map;
    }

    @Override
    public UserInfo newUserInfo(UserInfo userInfo) {
        UserInfo newUserInfo = new UserInfo();
        newUserInfo.setId(userInfo.getId());
        newUserInfo.setCreateTime(userInfo.getCreateTime());
        newUserInfo.setParam(userInfo.getParam());
        newUserInfo.setNickName(userInfo.getNickName());
        newUserInfo.setPhone(userInfo.getPhone());
        newUserInfo.setName(userInfo.getName());
        newUserInfo.setCertificatesUrl(userInfo.getCertificatesUrl());
        return newUserInfo;
    }

    @Override
    public UserInfo selectByOpenId(String openid) {
        QueryWrapper<UserInfo> wrapper = new QueryWrapper<>();
        wrapper.eq("openid", openid);
        return baseMapper.selectOne(wrapper);
    }

    @Override
    public void selectByAuthStatus(UserInfo userInfo) {
        Integer authStatus = userInfo.getAuthStatus();
        String status;
        if (authStatus == 0) {
            status = "未认证";
        } else if (authStatus == 1) {
            status = "认证中";
        } else if (authStatus == 2) {
            status = "认证成功";
        } else {
            status = "认证失败";
        }
        userInfo.getParam().put("authStatusString", status);
    }

    @Override
    public boolean saveUserAuth(UserAuthVo userAuthVo, HttpServletRequest request) {
        if (userAuthVo == null) return false;
        String certificatesNo = userAuthVo.getCertificatesNo();
        if (StringUtils.isEmpty(certificatesNo)) {
            throw new RException("身份证为空");
        }
        boolean validCard = IdcardUtil.isValidCard(certificatesNo);
        UserInfo userInfo = new UserInfo();
        BeanUtils.copyProperties(userAuthVo, userInfo);
        userInfo.setId(JwtUtils.getTokenById(request));
        userInfo.setAuthStatus(1);
        int update = baseMapper.updateById(userInfo);
        return update == 1;
    }


    @Override
    public Map<String, Object> selectPageList(int page, int limit, UserInfoQueryVo userInfoQueryVo) {
        //UserInfoQueryVo获取条件值
        String name = userInfoQueryVo.getKeyword(); //用户名称
        Integer status = userInfoQueryVo.getStatus();//用户状态
        Integer authStatus = userInfoQueryVo.getAuthStatus(); //认证状态
        String createTimeBegin = userInfoQueryVo.getCreateTimeBegin(); //开始时间
        String createTimeEnd = userInfoQueryVo.getCreateTimeEnd(); //结束时间

        //对条件值进行非空判断
        QueryWrapper<UserInfo> wrapper = new QueryWrapper<>();
        if (!StringUtils.isEmpty(name)) {
            wrapper.like("name", name).or().eq("phone", name);
        }
        if (!StringUtils.isEmpty(status)) {
            wrapper.eq("status", status);
        }
        if (!StringUtils.isEmpty(authStatus)) {
            wrapper.eq("auth_status", authStatus);
        }
        if (!StringUtils.isEmpty(createTimeBegin)) {
            wrapper.ge("create_time", createTimeBegin);
        }
        if (!StringUtils.isEmpty(createTimeEnd)) {
            wrapper.le("create_time", createTimeEnd);
        }
        Page<UserInfo> userInfoPage = new Page<>(page, limit);
        Page<UserInfo> selectPage = baseMapper.selectPage(userInfoPage, wrapper);

        List<UserInfo> records = selectPage.getRecords();
        records.parallelStream().forEach(userInfo -> {
            this.selectByAuthStatus(userInfo);
            //处理用户状态 0  1
            String statusString = userInfo.getStatus() == 0 ? "锁定" : "正常";
            userInfo.getParam().put("statusString", statusString);
        });
        Map<String, Object> map = new HashMap<>();
        map.put("records", records);
        map.put("total", selectPage.getTotal());

        return map;
    }

    // 用户锁定
    @Override
    public void lock(String userId, Integer status) {
        if (StringUtils.isEmpty(userId)) {
            throw new RException("数据为空");
        }
        if (status == 0 || status == 1) {
            UserInfo userInfo = baseMapper.selectById(userId);
            userInfo.setStatus(status);
            baseMapper.updateById(userInfo);
        }
    }


    // 更具id查询用户信息
    @Override
    public Map<String, Object> show(String userId) {
        Map<String, Object> map = new HashMap<>();
        //根据userid查询用户信息
        UserInfo userInfo = baseMapper.selectById(userId);
        this.selectByAuthStatus(userInfo);
        map.put("userInfo", userInfo);
        //根据userid查询就诊人信息
        List<Patient> patientList = patientService.selectByUserIdAllPatient(userId);
        map.put("patientList", patientList);
        return map;
    }

    @Override
    public void approval(String userId, Integer authStatus) {
        if (StringUtils.isEmpty(userId)) {
            throw new RException("数据错误,请刷新重试");
        }
        if(authStatus ==2 || authStatus ==-1) {
            UserInfo userInfo = baseMapper.selectById(userId);
            userInfo.setAuthStatus(authStatus);
            baseMapper.updateById(userInfo);
        }

    }
}
