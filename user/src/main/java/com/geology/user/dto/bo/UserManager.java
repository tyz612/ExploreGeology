package com.geology.user.dto.bo;

import com.geology.user.contant.CommonStatic;
import com.geology.user.contant.MessageType;
import com.geology.user.pojo.HistoryMessages;
import com.geology.user.common.utils.ConvertUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Date:2024/9/4
 * author:zmh
 * description:用户管理器，用来管理所有的websocket连接 和 一些常用方法封装
 **/

@Component
@RequiredArgsConstructor
@Slf4j
public class UserManager {
    private final StringRedisTemplate redisTemplate;

    // 存储用户 ID 与 WebSocket Session 的映射
    private final Map<String, WebSocketSession> userSessions = new ConcurrentHashMap<>();

    private final RabbitTemplate rabbitTemplate;

    /**
     * 添加用户并存储 WebSocket Session
     *
     * @param userId 用户 ID
     * @param session WebSocket 会话
     */
    public void addUser(String userId, WebSocketSession session) {
        userSessions.put(userId, session);
        // 在 Redis 中标记用户上线
        redisTemplate.opsForSet().add("onlineUsers", userId);
    }

    /**
     * 根据 WebSocketSession 删除用户
     *
     * @param session WebSocket 会话
     */
    public void removeUserBySession(WebSocketSession session) {
        // 查找对应的用户ID
        String userId = null;
        for (Map.Entry<String, WebSocketSession> entry : userSessions.entrySet()) {
            if (entry.getValue().equals(session)) {
                userId = entry.getKey();
                break;
            }
        }

        // 如果找到了对应的用户ID，则移除用户
        if (userId != null) {
            userSessions.remove(userId);
            // 在 Redis 中标记用户下线
            redisTemplate.opsForSet().remove("onlineUsers", userId);
            log.info("用户已下线: {}", userId);
        } else {
            log.info("未找到对应的用户Session，无法删除");
        }
    }

    /**
     * 获取所有已连接websocket服务的用户ID
     * @return ·
     */
    public List<String> getConnectedUserId(){
        ArrayList<String> userIds = new ArrayList<>();
        for (Map.Entry<String, WebSocketSession> entry : userSessions.entrySet()) {
            userIds.add(entry.getKey());
        }
        return userIds;
    }


    /**
     * 移除用户及其 Session
     *
     * @param userId 用户 ID
     */
    public void removeUser(String userId) {
        userSessions.remove(userId);
        // 在 Redis 中标记用户下线
        redisTemplate.opsForSet().remove("onlineUsers", userId);
    }

    /**
     * 发送私聊消息
     * @param historyMessages 构造好的历史记录对象，内含私聊的信息
     */
    public void sendPrivateMessage(HistoryMessages historyMessages) {
        historyMessages.setId(null);
        WebSocketSession session = userSessions.get(historyMessages.getReceiver());
        if (session != null && session.isOpen()) {
            try {
                session.sendMessage(new TextMessage(ConvertUtil.ObjectToJson(historyMessages)));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            log.info("【私发-用户不在线】");
        }
    }

    /**
     * 发送群发消息
     *
     * @param fromUserId 发送者 ID
     * @param message    消息内容
     */
    public void sendToPublicGroup(String fromUserId, String message) {
        // 获取所有在线用户的 ID
        Set<String> userIds = redisTemplate.opsForSet().members("onlineUsers");
        if (userIds == null) return;


        // 构造历史消息对象
        HistoryMessages historyMessages = new HistoryMessages(1,
                fromUserId, CommonStatic.PUBLIC_GROUP_ID, message, MessageType.PUBLIC, CommonStatic.PUBLIC_GROUP_NAME);
        // 数据持久化策略：先存rabbitMQ，在存入redis，定时任务取redis批量存入mysql
        rabbitTemplate.convertAndSend("saveQueue",historyMessages);

        // 将对象转为JSON字符串后发送给前端
        historyMessages.setId(null);
        String hisString = ConvertUtil.ObjectToJson(historyMessages);

        // 遍历所有用户并发送消息
        for (String userId : userIds) {
            WebSocketSession session = userSessions.get(userId);
            if (session != null && session.isOpen()) {
                try {
                    // 将构造好历史记录消息，供前端存储到indexDB中
                    session.sendMessage(new TextMessage(hisString));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 检查用户是否在线/连接到服务
     *
     * @param userId 用户 ID
     * @return 是否在线
     */
    public boolean isUserOnline(String userId) {
        return userSessions.containsKey(userId);
    }

    /**
     * 根据用户id取出用户session
     * @param userId `
     * @return `
     */
    public WebSocketSession getSessionByUserId(String userId){
        return userSessions.get(userId);
    }

}
