package com.geology.user.dto.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Date:2024/9/14
 * author:zmh
 * description: 好友申请信息返回DTO
 **/

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FriendsInfoDto {

    // 申请者用户ID
    private String userID;

    // 用户名
    private String userName;

    // 邮箱
    private String email;

    // 头像
    private String avatar;

}
