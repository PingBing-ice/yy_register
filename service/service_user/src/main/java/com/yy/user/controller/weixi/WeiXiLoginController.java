package com.yy.user.controller.weixi;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.yy.user.service.IUserInfoService;
import com.yy.user.util.ConstantPropertiesUtil;
import com.yy.user.util.HttpClientUtils;
import com.yy.util.exception.RException;
import com.yy.util.result.R;
import com.yy.util.utils.JwtUtils;
import com.yy.yygh.model.user.UserInfo;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * @author ice
 * @date 2022/8/10 16:02
 */
@RequestMapping("/user/weixilogin")
@Controller
public class WeiXiLoginController {


    @Resource
    private IUserInfoService userInfoService;

    // callback
    @GetMapping("/callback")
    public String callback(String code,String state) {
        String accessTokenUrl = "https://api.weixin.qq.com/sns/oauth2/access_token?" +
                "appid=%s&" +
                "secret=%s&" +
                "code=%s&grant_type=authorization_code";
        accessTokenUrl = String.format(accessTokenUrl, ConstantPropertiesUtil.WX_OPEN_APP_ID,
                ConstantPropertiesUtil.WX_OPEN_APP_SECRET,
                code);
        try {
            String jsonStr = HttpClientUtils.get(accessTokenUrl);
            System.out.println(jsonStr);
            JSONObject jsonObject = JSONObject.parseObject(jsonStr);
            String access_token =  jsonObject.getString("access_token");
            String openid =  jsonObject.getString("openid");
            UserInfo userInfo = userInfoService.selectByOpenId(openid);
            if (userInfo == null) {
                // 解析用户信息
                accessTokenUrl = "https://api.weixin.qq.com/sns/userinfo?access_token=%s&openid=%s";
                accessTokenUrl = String.format(accessTokenUrl, access_token, openid);
                jsonStr = HttpClientUtils.get(accessTokenUrl);
                JSONObject parseObject = JSONObject.parseObject(jsonStr);
                //用户昵称
                String nickname = parseObject.getString("nickname");
                //用户头像
                String headimgurl = parseObject.getString("headimgurl");
                userInfo = new UserInfo();
                userInfo.setNickName(nickname);
                userInfo.setOpenid(openid);
                userInfoService.save(userInfo);
            }
            HashMap<String, String> map = new HashMap<>();
            String name = userInfo.getName();
            if(StringUtils.isEmpty(name)) {
                name = userInfo.getNickName();
            }
            if(StringUtils.isEmpty(name)) {
                name = userInfo.getPhone();
            }
            map.put("name", name);
            if (StringUtils.isEmpty(userInfo.getPhone())) {
                map.put("openid", openid);
            }else {
                map.put("openid", "");
            }
            String token = JwtUtils.createToken(userInfo.getId(), name);
            map.put("token", token);
            // 跳转前端页面
            return "redirect:http://localhost:3000/weixin/callback?token="+map.get("token")+ "&openid="+map.get("openid")+"&name="+URLEncoder.encode(map.get("name"),"utf-8");
        } catch (Exception e) {
            throw new RException("登录失败");
        }
    }



    @GetMapping("/getLoginParam")
    @ResponseBody
    public R genQrConnect() throws UnsupportedEncodingException {
        String redirectUri = URLEncoder.encode(ConstantPropertiesUtil.WX_OPEN_REDIRECT_URL, "UTF-8");
        Map<String, Object> map = new HashMap<>();
        map.put("appid", ConstantPropertiesUtil.WX_OPEN_APP_ID);
        map.put("redirectUri", redirectUri);
        map.put("scope", "snsapi_login");
        map.put("state", System.currentTimeMillis()+"");//System.currentTimeMillis()+""
        return R.ok().data(map);
    }

}
