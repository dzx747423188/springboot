package com.springboot.example.common;

/**
 * 接口返回信息通用类
 * Created by Park on 2019-5-24.
 */
public enum  ResponseCode {
    //成功
    SUCCESS(0,"SUCCESS"),
    //失败
    ERROR(1,"ERROR"),
    //用户未登录
    NEED_LOGIN(10,"NEED_LOGIN"),
    //非法请求
    ILLEGAL_ARGUMENT(2 , "ILLEGAL_ARGUMENT");

    private  final  int code;
    private  final  String desc ;
    ResponseCode(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
