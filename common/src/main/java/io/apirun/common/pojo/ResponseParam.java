package io.apirun.common.pojo;

import com.google.common.collect.Maps;

import java.io.Serializable;
import java.util.Map;

public class ResponseParam<T> implements Serializable {

    private  int code = 0;

    private Map<String, Object> data = null;

    private String msg = "ok";

    private T t;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public T getT() {
        return t;
    }

    public void setT(T t) {
        this.t = t;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }

    public void put(String key, Object value) {
        if(data == null) {
            data = Maps.newHashMap();
        }
        data.put(key, value);
    }

    public Object get(String key) {
        if(data == null) {
            return null;
        }
        return data.get(key);
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

}
