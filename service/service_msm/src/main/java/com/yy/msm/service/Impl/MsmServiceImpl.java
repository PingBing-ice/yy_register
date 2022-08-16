package com.yy.msm.service.Impl;

import com.yy.msm.service.MsmService;
import com.yy.msm.util.HttpUtils;


import com.yy.msm.util.RandomUtil;
import com.yy.util.exception.RException;
import com.yy.yygh.vo.msm.MsmVo;
import lombok.extern.log4j.Log4j2;
import org.apache.http.HttpResponse;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

/**
 * @author ice
 * @date 2022/8/10 11:48
 */
@Service
@Log4j2
public class MsmServiceImpl implements MsmService {

    @Resource
    private StringRedisTemplate stringRedisTemplate;


    @Override
    public void send(MsmVo msmVo) {
        log.info(msmVo);
        String fourBitRandom = RandomUtil.getFourBitRandom();
        stringRedisTemplate.opsForValue().set(msmVo.getPhone()+"=="+fourBitRandom,"发送成功",3,TimeUnit.MINUTES);

    }

    @Override
    public boolean sendCode(String phone) {
        String redisCode = stringRedisTemplate.opsForValue().get(phone);
        if (!StringUtils.isEmpty(redisCode)) {
            return true;
        }
        Pattern compile = Pattern.compile("^((13[0-9])|(14[0,1,4-9])|(15[0-3,5-9])|(16[2,5,6,7])|(17[0-8])|(18[0-9])|(19[0-3,5-9]))\\d{8}$");
        boolean matches = compile.matcher(phone).matches();
        if (!matches) {
            throw new RException("手机号不合法");
        }
        String host = "http://dingxin.market.alicloudapi.com";
        String path = "/dx/sendSms";
        String method = "POST";
        String appcode = "b9dd974f515f4b58b158c8c80694d19d";
        Map<String, String> headers = new HashMap<String, String>();
        //最后在header中的格式(中间是英文空格)为Authorization:APPCODE 83359fd73fe94948385f570e3c139105
        headers.put("Authorization", "APPCODE " + appcode);
        Map<String, String> querys = new HashMap<String, String>();
        querys.put("mobile", phone);
        String code = RandomUtil.getFourBitRandom();
        querys.put("param", "code:"+ code);
        querys.put("tpl_id", "TP1711063");
        Map<String, String> bodys = new HashMap<String, String>();

        try {
            /**
             * 重要提示如下:
             * HttpUtils请从
             * https://github.com/aliyun/api-gateway-demo-sign-java/blob/master/src/main/java/com/aliyun/api/gateway/demo/util/HttpUtils.java
             * 下载
             *
             * 相应的依赖请参照
             * https://github.com/aliyun/api-gateway-demo-sign-java/blob/master/pom.xml
             */
            HttpResponse response = HttpUtils.doPost(host, path, method, headers, querys, bodys);
            System.out.println(response.toString());
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != 200) {
                return false;
            }
            stringRedisTemplate.opsForValue().set(phone, code,3, TimeUnit.HOURS);
            return true;
            //获取response的body
            //System.out.println(EntityUtils.toString(response.getEntity()));
        } catch (Exception e) {
            throw new RException("短信发送失败");
        }
    }
}
