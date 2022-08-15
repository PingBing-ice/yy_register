package com.yy.util.encryption;

import org.springframework.util.DigestUtils;

/**
 * @author ice
 * @date 2022/7/29 18:46
 */

public class MD5 {
    private final static String SALT = "ice";

    public static String getMD5(String password) {
        return DigestUtils.md5DigestAsHex((SALT + password).getBytes());
    }

    public static void main(String[] args) {
        String md5 = getMD5("1");
        System.out.println(md5);
    }
}
