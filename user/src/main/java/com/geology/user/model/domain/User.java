package com.geology.user.model.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户实体
 *

 */
@TableName(value = "userinfo")
@Data
public class User {
    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户昵称
     */
    @TableField("user_name")
    private String userName;

    /**
     * 账号
     */
    @TableField("user_account")
    private String userAccount;

    /**
     * 用户头像
     */
    @TableField("avatar_url")
    private String avatarUrl;

    /**
     * 性别
     */
    @TableField("gender")
    private Integer gender;

    /**
     * 密码
     */
    @TableField("user_password")
    private String userPassword;

    /**
     * 电话
     */
    @TableField("phone")
    private String phone;

    /**
     * 邮箱
     */
    @TableField("email")
    private String email;

    /**
     * 状态 0 - 正常
     */
    @TableField("user_status")
    private Integer userStatus;

    /**
     * 创建时间
     */
    @TableField("create_time")
    private String createTime;

    /**
     *
     */
    @TableField("update_time")
    private String updateTime;

    /**
     * 是否删除
     */
//    @TableLogic
    @TableField("is_delete")
    private Integer isDelete;

    /**
     * 用户角色 0 - 普通用户 1 - 管理员
     */
    @TableField("user_role")
    private Integer userRole;

    /**
     * 星球编号
     */
    @TableField("planet_code")
    private String planetCode;
}
