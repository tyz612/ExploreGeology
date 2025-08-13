package com.geology.user.common.bean;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

@Data
public class UserInfoBean {
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;

    private String userName;

    private String avatarUrl;

    private String email;

    private String token;
}
