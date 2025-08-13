package com.geology.user.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * 历史消息表(HistoryMessages)表实体类
 *
 * @author zmh
 * @since 2024-09-02 17:22:05
 */
@SuppressWarnings("serial")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HistoryMessages implements Serializable {
    //自增主键
    private Integer id;
    //消息发送者的用户唯一标识
    private String sender;
    //消息接收者的用户唯一标识或群组ID
    private String receiver;
    //消息内容
    private String message;
    //消息类型：public=公共群组，private=私有群组，person=私聊
    private String msgType;
    //群组名称（私聊时可为空）
    private String groupName;
    //消息发送时间
    private Date timestamp;
    
    

    // 返回数据对象构造器
    public HistoryMessages(Integer id, String sender, String receiver, String message, String msgType, String groupName) {
        this.id = id;
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
        this.msgType = msgType;
        this.groupName = groupName;
        this.timestamp = new Date();
    }
}

