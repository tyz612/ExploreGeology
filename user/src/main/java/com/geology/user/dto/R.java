package com.geology.user.dto;

import lombok.Data;

/**
 * author:zmh
 * description:前后端数据联调对象
 **/

@Data
public class R<T> {
    private Integer code; //编码：1成功，0和其它数字为失败

    private String msg; //错误信息

    private T data; //数据

    public static <T> R<T> success(T object) {
        R<T> r = new R<T>();
        r.data = object;
        r.code = 0;
        return r;
    }

    public static <T> R<T> error(String msg) {
        R r = new R();
        r.msg = msg;
        r.code = 1;
        return r;
    }

    public static <T> R<T> errorToken(String msg) {
        R r = new R();
        r.msg = msg;
        r.code = 401;
        return r;
    }

    /**
     * 登录失败提示
     * @return ·
     * @param <T> ·
     */
    public static <T> R<T> loginError() {
        R r = new R();
        r.msg = "用户不存在或密码错误";
        r.code = 1;
        return r;
    }

}
