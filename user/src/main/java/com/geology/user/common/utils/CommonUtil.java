package com.geology.user.common.utils;


import cn.hutool.jwt.JWTUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.geology.user.contant.MessageType;
import com.geology.user.dto.bo.UserManager;
import com.geology.user.dto.vo.AcceptMessage;
import com.geology.user.pojo.HistoryMessages;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;

/**
 * Date:2024/9/4
 * author:zmh
 * description: 通用工具类
 **/


@RequiredArgsConstructor
@Component
@Slf4j
public class CommonUtil {

    // JWT工具验证token
//    private final JWTUtil jwtUtils;

    private final ObjectMapper objectMapper;

//    private final DynamicGroupManager groupManager;

    private final RabbitTemplate rabbitTemplate;

    private final UserManager userManager;


    // 解析-转换前端传入的消息为VO对象
    public void convertMessage(TextMessage messageJson) throws JsonProcessingException {
        // 转换消息为VO对象
        AcceptMessage acceptMessage = objectMapper.readValue(messageJson.getPayload(), AcceptMessage.class);
        // 分发消息
        dispatcherMessage(acceptMessage);
    }

    /**
     * 消息分发器
      * @param acceptMessage 消息VO
     */
    public void dispatcherMessage(AcceptMessage acceptMessage){
        if(acceptMessage.getMessageType().equals(MessageType.PUBLIC)){
            // 将消息先加到rabbitMQ中，在消费者处发送消息。
            rabbitTemplate.convertAndSend("chatQueue",acceptMessage);
            log.info("公共群发分发");
        }else if(acceptMessage.getMessageType().equals(MessageType.GROUP)){
            // 消息持久化-消息转为历史记录对象，然后使用存储策略
            HistoryMessages historyMessages = new HistoryMessages(1,
                    acceptMessage.getFromUserId(), acceptMessage.getToId(), acceptMessage.getContent(), MessageType.GROUP, acceptMessage.getGroupName());
            // 数据持久化策略：先存rabbitMQ，在存入redis，定时任务取redis批量存入mysql
            rabbitTemplate.convertAndSend("saveQueue",historyMessages);

        }else{
            // 构造历史记录对象
            HistoryMessages historyMessages = new HistoryMessages(1,
                    acceptMessage.getFromUserId(), acceptMessage.getToId(), acceptMessage.getContent(), MessageType.PERSON, acceptMessage.getGroupName());
            // 数据持久化策略：先存rabbitMQ，在存入redis，定时任务取redis批量存入mysql
            rabbitTemplate.convertAndSend("saveQueue",historyMessages);

            // 向私有用户发送消息
            userManager.sendPrivateMessage(historyMessages);
        }
    }

}
