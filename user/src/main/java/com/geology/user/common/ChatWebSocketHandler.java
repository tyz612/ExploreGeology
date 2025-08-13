package com.geology.user.common;

import com.geology.user.common.utils.CommonUtil;
import com.geology.user.dto.bo.UserManager;
import com.geology.user.jwt.AuthStorage;
import com.geology.user.jwt.JwtUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;


/**
 * Date:2024/9/3
 * author:zmh
 * description: WebSocket请求处理器
 **/

@Component
@RequiredArgsConstructor
@Slf4j
public class ChatWebSocketHandler extends TextWebSocketHandler {

    @Autowired
    private CommonUtil commonUtil;

    // 用户管理器，管理连接的用户，发群发，私发等函数封装
    @Autowired
    private UserManager userManager;

//    // 群组管理器，用户群组和用户关系的维护。
//    @Autowired
//    private DynamicGroupManager groupManager;

    // 存储连接的客户端会话 - 废弃，使用用户管理器来管理连接的用户
//    private static Set<WebSocketSession> sessions = new HashSet<>();

    // 连接成功，将用户session进行管理
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        // 获取 URI 中的参数，取出token验证连接是否有效
        String query = session.getUri().getQuery();
        String token = null;

        // 提取 token 参数
        if (query != null) {
            for (String param : query.split("&")) {
                String[] keyValue = param.split("=");
                if (keyValue.length == 2 && "token".equals(keyValue[0])) {
                    token = keyValue[1];
                    break;
                }
            }
        }

        // 验证 token
        if (token != null) {
            // 根据token取出当前登录的用户ID
            JwtUser user = AuthStorage.getUser();
            String userId = user.getUserId();
            // 检查该用户是否已经连接，避免存储同一用户多次连接
            boolean userOnline = userManager.isUserOnline(userId);
            if(!userOnline){
                userManager.addUser(userId, session);
            }
        } else {
            session.close(); // 关闭连接，不允许未验证的连接
        }
    }

    // 接收到客户端消息，解析消息内容进行分发
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        // 转换并分发消息
        log.info("收到消息");
        commonUtil.convertMessage(message);
    }

    // 处理断开的连接
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        userManager.removeUserBySession(session);
    }

}
