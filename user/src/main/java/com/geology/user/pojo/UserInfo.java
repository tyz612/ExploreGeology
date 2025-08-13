package com.geology.user.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Table;

/**
 * 用户实体
 *

 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("user_info") // 确认表名
public class UserInfo {
    /**
     * id
     */
    @TableId(type = IdType.AUTO) // 确认主键类型
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;

    /**
     * 用户昵称
     */
    private String userName;

    /**
     * 账号
     */
    private String userAccount;

    /**
     * 用户头像
     */
    private String avatarUrl;

    /**
     * 性别
     */

    private Integer gender;

    /**
     * 密码
     */
    private String userPassword;

    /**
     * 电话
     */
    private String phone;

    /**
     * 邮箱
     */

    private String email;

    /**
     * 状态 0 - 正常
     */

    private Integer userStatus;

    /**
     * 创建时间
     */

    private String createTime;

    /**
     *
     */

    private String updateTime;

    /**
     * 是否删除
     */
    private Integer isDelete;

    /**
     * 用户角色 0 - 普通用户 1 - 管理员
     */

    private Integer userRole;

    /**
     * 星球编号
     */
    private String planetCode;
}
