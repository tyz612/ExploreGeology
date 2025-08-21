package com.geology.user.dto.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShareDataInfoDTO {
    // 申请者用户ID
    private String userId;

    // 用户名
    private String userName;

    // 头像
    private String avatar;

    // 数据类型
    private Integer dataType;

    // 数据名称
    private String dataName;

    // 数据来源用户id
    private String fromUserId;

    // 数据ID
    private String dataId;
}
