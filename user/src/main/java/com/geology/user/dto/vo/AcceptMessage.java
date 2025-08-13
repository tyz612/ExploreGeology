package com.geology.user.dto.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Date:2024/9/4
 * author:zmh
 * description: 接收前端消息的格式定义
 **/

@AllArgsConstructor
@NoArgsConstructor
@Data
public class AcceptMessage {

    // 消息来自谁
    private String fromUserId;

    // 谁接收（用户id 或 群组id）
    private String toId;

    // 消息类型（public-公共群组, group-私有组, person-私聊）
    private String messageType;

    // 消息内容
    private String content;

    // 群组名称 - 私聊可为空
    private String groupName;
}
