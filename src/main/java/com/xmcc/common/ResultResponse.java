package com.xmcc.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResultResponse<T> {
    private int code;
    private String msg;
    @JsonInclude(JsonInclude.Include.NON_NULL)//返回json时忽略为null的属性
    private T data;

    //失败了或者成功了 有时候不需要返回 data
    public ResultResponse(int code,String msg){
        this.code = code;
        this.msg = msg;
    }
    //失败了 不携带数据、不携带信息的方法
    public static ResultResponse  fail(){
        return  new ResultResponse<>(ResultEnums.FAIL.getCode(),ResultEnums.FAIL.getMsg());
    }
    //失败了 不携带数据、携带信息的方法
    public static ResultResponse  fail(String msg){
        return  new ResultResponse<>(ResultEnums.FAIL.getCode(),msg);
    }
    //失败了 携带数据、携带信息的方法
    public static <T>ResultResponse  fail(String msg,T t){
        return  new ResultResponse<>(ResultEnums.FAIL.getCode(),msg,t);
    }
    //失败了 携带数据、不携带信息的方法
    public static <T>ResultResponse  fail(T t){
        return  new ResultResponse<>(ResultEnums.FAIL.getCode(),ResultEnums.FAIL.getMsg(),t);
    }
    //成功 携带数据
    public static <T> ResultResponse success(T t){
        return new ResultResponse(ResultEnums.SUCCESS.getCode(),ResultEnums.SUCCESS.getMsg(),t);
    }
    //成功 不携带数据
    public static <T> ResultResponse success(){
        return new ResultResponse(ResultEnums.SUCCESS.getCode(),ResultEnums.SUCCESS.getMsg());
    }
}
