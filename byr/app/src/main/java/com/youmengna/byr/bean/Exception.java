package com.youmengna.byr.bean;

import java.io.Serializable;

/**
 * Created by youmengna0 on 2016/10/21.
 */
public class Exception implements Serializable {
    private String request;
    private String code;
    private String msg;
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getRequest() {
        return request;
    }

    public void setRequest(String request) {
        this.request = request;
    }

}
