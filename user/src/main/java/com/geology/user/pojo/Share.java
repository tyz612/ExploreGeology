package com.geology.user.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.criteria.CriteriaBuilder;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Share {
    //自增主键
    private Integer id;

    //用户ID
    private String userId;

    //好友ID
    private String contactId;

    //分享数据id
    private String dataId;

    //分享数据类型
    private Integer dataType;

    //数据类型
    private Integer state;

    // 数据名称
    private String dataName;

    // 用户名称
    private String userName;

    // 数据确认状态
    private Integer status;

    // 用户头像
    private String avatar;
}
