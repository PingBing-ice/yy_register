package com.yy.util.result;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * @author ice
 * @date 2022/7/29 15:56
 * <p>
 * 返回的数据
 */
@Data
public class R{
    private Integer code;
    private String message;
    private Boolean success;
    private Map<String, Object> data = new HashMap<>();
    private R(){}

    public static R ok() {
        R r = new R();
        r.setCode(2000);
        r.setMessage("成功");
        r.setSuccess(true);
        return r;
    }
    public static R error() {
        R r = new R();
        r.setCode(2001);
        r.setMessage("失败");
        r.setSuccess(false);
        return r;
    }

    public R message(String message) {
        this.setMessage(message);
        return this;
    }
    public R code(Integer code) {
        this.setCode(code);
        return this;
    }

    public R success(Boolean success) {
        this.setSuccess(success);
        return this;
    }

    public R data(String key, Object value) {
        this.data.put(key, value);
        return this;
    }
    public R data(Map<String,Object>  map) {
        this.setData(map);
        return this;
    }

}
