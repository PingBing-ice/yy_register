package com.yy.hosp.vo;

import lombok.Data;

/**
 * 主页返回的数据
 * @author ice
 * @date 2022/8/9 9:54
 */
@Data
public class ResultIndex {
    private String id;
    private String hoscode;
    private String hosname;
    private String hosTypeString;
    private String releaseTime;
    private String logoData;

}
