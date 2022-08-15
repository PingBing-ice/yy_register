package com.yy.hosp;

import cn.hutool.core.date.DateUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.digest.MD5;

import java.util.Arrays;
import java.util.Date;

/**
 * @author ice
 * @date 2022/7/29 10:40
 */

public class MysqlTest {
    public static void main(String[] args) {
        String ice = SecureUtil.md5("ice");

        System.out.println(ice);

        String s = dayOfWeek(new Date());
        System.out.println(s);
    }

    public static String dayOfWeek(Date date){
        int day= DateUtil.dayOfWeek(date);
        switch (day){
            case 1:return "星期天";
            case 2:return "星期一";
            case 3:return "星期二";
            case 4:return "星期三";
            case 5:return "星期四";
            case 6:return "星期五";
            case 7:return "星期六";
            default:return "";
        }
    }
}
