package com.yc.res.bean;

import java.io.Serializable;

/**
 * web访问的结果
 */
public class JsonModel<T> implements Serializable {   //在服务器端的java bean， 为了让tomcat能缓存数据，最好实现 Serializabe
    private Integer code;    //1    0
    private T obj;      //数据
    private String msg;   //出错信息

    public Integer getCode() {
        return code;
    }

    public T getObj() {
        return obj;
    }

    public String getMsg() {
        return msg;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public void setObj(T obj) {
        this.obj = obj;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
