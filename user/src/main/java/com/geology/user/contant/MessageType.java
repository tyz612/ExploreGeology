package com.geology.user.contant;

/**
 * Date:2024/9/5
 * author:zmh
 * description:消息类型-静态数据定义
 **/


public final class MessageType {

    // 公共群聊
    public static final String PUBLIC = "public";

    // 私有群聊
    public static final String GROUP = "group";

    // 用户私聊
    public static final String PERSON = "person";

    // 私有构造方法防止实例化
    private MessageType() {
        throw new UnsupportedOperationException("静态数据类，无法实例化");
    }
}
