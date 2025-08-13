package com.geology.user.task;


import com.geology.user.common.utils.ConvertUtil;
import com.geology.user.dao.HistoryMessagesDao;
import com.geology.user.pojo.HistoryMessages;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Date:2024/9/6
 * author:zmh
 * description: 任务类
 **/

@RequiredArgsConstructor
@Component
@Slf4j
public class TaskOfSaveMessage {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    private final HistoryMessagesDao historyMessagesDao;

    /**
     * 每 30 秒执行一次，将redis中的历史记录缓存批量存入MYSQL中
     */
    @Scheduled(fixedRate = 30000)
    public void saveMessagesToDatabase() {
        List<HistoryMessages> batch = new ArrayList<>();
        Set<String> keys = redisTemplate.keys("chat:*"); // 前提需要配置Redis的key序列化方式

        if(keys == null || keys.size() == 0){
            return;
        }

        for (String key : keys) {
            Object o = redisTemplate.opsForValue().get(key);
            HistoryMessages historyMessages = ConvertUtil.JsonToObject(ConvertUtil.ObjectToJson(o), HistoryMessages.class);

            if (historyMessages != null) {
                batch.add(historyMessages);
                redisTemplate.delete(key); // 删除 Redis 中已读取的消息
            }
        }

        // 批量保存到 MySQL
        if (!batch.isEmpty()) {
            log.info("正在阶段性数据持久化操作===>");
            historyMessagesDao.insert((HistoryMessages) batch);
            log.info("++++阶段性持久化成功，成功批量存入{}条数据++++",batch.size());
        }
    }

}
