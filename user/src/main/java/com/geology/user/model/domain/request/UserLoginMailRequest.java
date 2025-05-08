package com.geology.user.model.domain.request;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户登录请求体
 *

 */
@Data
public class UserLoginMailRequest implements Serializable {

    private static final long serialVersionUID = 3191241716373120793L;

    /**
     * 用户账号
     */
    private String email;

    /**
     * 用户密码
     */
    private String verifyCode;

}