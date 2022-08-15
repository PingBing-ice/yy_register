package com.yy.util.utils;


import cn.hutool.jwt.JWT;
import cn.hutool.jwt.JWTUtil;
import com.yy.util.exception.RException;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * @author ice
 * @date 2022/8/10 9:22
 */

public class JwtUtils {
    private static final long EXPIRE = 1000 * 60 * 60 * 24 * 15; // 设置token过期时间
    private static final String APP_SECRET = "ice6739d6649d2942bf9579623286fac8c5"; // 密钥

    public static String createToken(String id, String userName) {
        if (StringUtils.isEmpty(id)||StringUtils.isEmpty(userName)) {
            throw new RException("传入的数据为空");
        }
        HashMap<String, Object> headers = new HashMap<String, Object>() {
            {
                // 设置头信息
                put("typ", "JWT");
                put("alg", "HS256");
                put("ice", "你好");
            }
        };
        Map<String, Object> map = new HashMap<String,Object>(){
            private static final long serialVersionUID = 1L;
            {
                put("id", id);
                put("userName", userName);
                put("expire_time", System.currentTimeMillis() + EXPIRE);
            }
        };

        return JWTUtil.createToken(headers, map, APP_SECRET.getBytes(StandardCharsets.UTF_8));
    }

    public static String getTokenById(String token) {
        if (StringUtils.isEmpty(token)) {
            throw new RException("token为空");
        }
        JWT jwt = JWTUtil.parseToken(token);
        return  (String) jwt.getPayload("id");
    }
    public static String getTokenById(HttpServletRequest request) {
        String token = request.getHeader("token");
        if (StringUtils.isEmpty(token)) {
            throw new RException("token为空");
        }
        JWT jwt = JWTUtil.parseToken(token);
        return  (String) jwt.getPayload("id");
    }
    public static String getTokenByName(String token) {
        if (StringUtils.isEmpty(token)) {
            throw new RException("token为空");
        }
        JWT jwt = JWTUtil.parseToken(token);
        return  (String) jwt.getPayload("userName");
    }
    public static String getTokenByName(HttpServletRequest request) {
        String token = request.getHeader("token");
        if (StringUtils.isEmpty(token)) {
            throw new RException("token为空");
        }
        JWT jwt = JWTUtil.parseToken(token);
        return  (String) jwt.getPayload("id");
    }

    public static boolean verifyToken(String token) {
        return JWTUtil.verify(token, APP_SECRET.getBytes(StandardCharsets.UTF_8));
    }



}
