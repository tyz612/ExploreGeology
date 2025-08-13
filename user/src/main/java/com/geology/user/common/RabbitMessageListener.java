package com.geology.user.common;


import com.geology.user.dto.bo.UpdateReceiverBo;
import com.geology.user.dto.bo.UserManager;
import com.geology.user.dto.vo.AcceptMessage;
import com.geology.user.pojo.HistoryMessages;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Date:2024/9/5
 * author:zmh
 * description:RabbitMQ队列监听器
 **/

@Component
@RequiredArgsConstructor
@Slf4j
public class RabbitMessageListener {

    private final UserManager userManager;

    // 将消息缓存到redis中
    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Resource
    @Qualifier("redisTemplateMap")
    private RedisTemplate<String, Map<String,String>> redisTemplateMap;

    @RabbitListener(queues = "chatQueue")
    public void receiveMessage(AcceptMessage acceptMessage) {
        // 处理发送给公共群组的消息
        userManager.sendToPublicGroup(acceptMessage.getFromUserId(), acceptMessage.getContent());
    }

    /**
     * 收到消息，将消息存入redis中，定时然后取出存储，减小数据库IO次数
     * @param historyMessages 历史消息对象
     */
    @RabbitListener(queues = "saveQueue")
    public void SaveMessage(HistoryMessages historyMessages) {
        // 将消息存入Redis
        String redisKey = "chat:" + UUID.randomUUID();
        historyMessages.setId(null); // 置空，让数据库自己生成自增ID
        // 有序将消息写入 Redis，设置过期时间，避免堆积
        log.info("将历史记录数据存入redis：{}", historyMessages);
        redisTemplate.opsForValue().set(redisKey, historyMessages, 60, TimeUnit.SECONDS);
    }

    /**
     * 更新Redis中接收者信息
     * @param updateReceiverBo 参数作封装
     */
    @RabbitListener(queues = "update-receiver-queue")
    public void updateReceiverInRedis(UpdateReceiverBo updateReceiverBo) {
        Map<String, String> receiverMap = redisTemplateMap.opsForValue().get("receiver-info");
        if (receiverMap == null) {
            log.info("【错误-updateReceiverInRedis】，时间：{}，原因：Redis中无接收者信息", System.currentTimeMillis());
        }
        if (updateReceiverBo.getOps().equals("add")) {
            receiverMap.put(updateReceiverBo.getReceiverId(), updateReceiverBo.getName());
        } else if (updateReceiverBo.getOps().equals("del")) {
            receiverMap.remove(updateReceiverBo.getReceiverId());
        } else {
            log.info("传入参数错误，报错函数：RabbitMQ监听器类-updateReceiverInRedis()");
        }
        redisTemplate.opsForValue().set("receiver-info", receiverMap);
    }

}
