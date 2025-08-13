package com.geology.user.task;


import com.geology.user.common.utils.ConvertUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Date:2024/9/7
 * author:zmh
 * description:项目初始化加载数据到Redis
 **/

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskOfLoadDataToRedis {

    @Resource
    private RedisTemplate<String, Set<String>> redisTemplate;
//    private final GroupUserDao groupUserDao;

    /**
     * - 项目生命周期内仅执行一次
     * 项目启动将数据库中的群组信息（群组与用户关系）存入Redis缓存
     */
    @PostConstruct
    public void loadDataToRedis() {
        // 获取所有群组与用户的对应关系（群中包含的用户）key:群组名称，value:群组中所有的用户id
        // 过滤出确定已经加入群组的用户;
//        List<GroupUsersBo> groupUserDetails = groupUserDao.findGroupUserDetails();
        log.info("正在初始化项目群组数据===>将数据库中群组数据加到Redis中缓存<===");

        // 使用 HashMap 来分组，key 为群组名称，value 为 Set 集合来存储群组内的用户 ID
        HashMap<String, Set<String>> groupUserMap = new HashMap<>();

        // 分拣操作，将用户 ID 按照群组名称分组
//        for (GroupUsersBo groupUsersBo : groupUserDetails) {
////            String groupName = groupUsersBo.getGroupName();
//            String groupId = groupUsersBo.getGroupId();
//            String userId = groupUsersBo.getUserId();
//
//            // 如果 Map 中还没有这个群组名称的 key，先创建一个新的 Set
//            groupUserMap.computeIfAbsent(groupId, k -> new HashSet<>()).add(userId);
//        }

        // 将分拣后的数据存入 Redis 中
        groupUserMap.forEach((groupId, userIds) -> {
            // 使用 RedisTemplate 将群组信息存入 Redis
            redisTemplate.opsForValue().set("chat-group:"+groupId, userIds);
            log.info("群组 '{}' 的数据已成功存入 Redis: {}", groupId, ConvertUtil.ObjectToJson(userIds));
        });

        log.info("+++群组数据初始化完成+++");
    }
}
