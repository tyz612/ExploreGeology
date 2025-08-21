package com.geology.user.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * (Friends)表实体类
 *
 * @author zmh
 * @since 2024-09-10 16:51:10
 */
@SuppressWarnings("serial")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Friends {
    //自增主键
    private Integer id;

    //用户ID
    private String userId;

    //好友ID
    private String contactId;

    //好友状态
    private Integer state;
}

