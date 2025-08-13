package com.geology.user.dto.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Date:2024/9/14
 * author:zmh
 * description: 处理好友申请参数VO对象 - 同意或拒绝时传入的参数对象
 **/

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApplyUpdateAgrsVo {

    // 当前操作/登录用户
//    private String userId;

    // 被操作用户
    private String contactId;

    // 操作状态 ，【0：主动申请】，【1：已同意】，【2：被动申请】，【3：已拒绝】
    private Integer status;
}
