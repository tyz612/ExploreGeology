package com.geology.user.common.bean;

import lombok.Data;

@Data
public class UserInfoBean {
    private String userName;

    private String avatarUrl;

    private String email;

    private String token;
}
