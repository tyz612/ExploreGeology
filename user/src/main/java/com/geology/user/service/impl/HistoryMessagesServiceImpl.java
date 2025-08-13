package com.geology.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.geology.user.common.utils.GeologyDistributedIdGenerator;
import com.geology.user.dao.HistoryMessagesDao;
import com.geology.user.dto.R;
import com.geology.user.jwt.AuthStorage;
import com.geology.user.jwt.JwtUser;
import com.geology.user.pojo.HistoryMessages;
import com.geology.user.service.HistoryMessagesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * 历史消息表(HistoryMessages)表服务实现类
 *
 * @author zmh
 * @since 2024-09-02 17:22:05
 */
@Service
public class HistoryMessagesServiceImpl extends ServiceImpl<HistoryMessagesDao, HistoryMessages> implements HistoryMessagesService {

    @Autowired
    private HistoryMessagesDao historyMessagesDao;

    /**
     * 分页查询
     *
     * @param page 查询页数
     * @param size 一页显示条数
     * @return ·
     */
    public R<Page<HistoryMessages>> getAllByPage(int page, int size) {
        Page<HistoryMessages> historyMessagesPage = new Page<>(page, size);
        LambdaQueryWrapper<HistoryMessages> queryWrapper = new LambdaQueryWrapper<>();
        //TODO 查询条件定制
        return R.success(historyMessagesDao.selectPage(historyMessagesPage, queryWrapper));
    }


    @Override
    public R<List<HistoryMessages>> getIncrementByTimestamp(String userId, String lastTimestamp) {

        // +++ 获取以用户ID为接收者和时间戳过滤出【用户私聊和私有群组聊天】记录 +++

        // 将字符串形式的时间戳转换为 Date 对象
        Date timestamp;
        try {
            timestamp = new Date(Long.parseLong(lastTimestamp));
        } catch (NumberFormatException e) {
            // 处理无效时间戳的情况
            return R.error("时间戳转换对比失败，请检查传入时间戳！");
        }

        // 根据用户ID 和时间戳查询私人聊天记录【1.类型为person，发送者为登录者】，以及
        List<HistoryMessages> messages = historyMessagesDao.selectList(
                new QueryWrapper<HistoryMessages>()
                        .eq("msg_type", "person")
                        .and(wrapper -> wrapper
                                .eq("receiver", userId)
                                .or()
                                .eq("sender", userId))
                        .gt("timestamp", timestamp)
                        .orderByAsc("timestamp") // 按时间戳升序排序
        );

        // +++ 获取群组（公共群组或私有群组）中的数据 +++
        LambdaQueryWrapper<HistoryMessages> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(HistoryMessages::getMsgType, "public","private");
        queryWrapper.gt(HistoryMessages::getTimestamp, timestamp);
        List<HistoryMessages> historyMessages = historyMessagesDao.selectList(queryWrapper);
        messages.addAll(historyMessages);

        // 判断查询结果是否为空
        if (messages.isEmpty()) {
            return R.success(Collections.emptyList());
        }
        return R.success(messages);

    }

    @Override
    public String saveMessage(String receiver, String message) {
        JwtUser user = AuthStorage.getUser();
        String sender = user.getUserId();

        Long id = GeologyDistributedIdGenerator.getInstance().nextId();

        Date now = new Date();
        HistoryMessages historyMessages = new HistoryMessages(null, sender, receiver, message, "0", "0", now);



        historyMessagesDao.insert(historyMessages);

        return message;
    }

    @Override
    public List<HistoryMessages> getHistoryMessagesById(String receiver) {
        JwtUser user = AuthStorage.getUser();
        String sender = user.getUserId();


        LambdaQueryWrapper<HistoryMessages> queryWrapper = new LambdaQueryWrapper<>();
//        LambdaQueryWrapper<HistoryMessages> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.and(wq -> wq
                        .eq(HistoryMessages::getSender, sender)
                        .eq(HistoryMessages::getReceiver, receiver)
                )
                .or(wq -> wq
                        .eq(HistoryMessages::getSender, receiver)
                        .eq(HistoryMessages::getReceiver, sender)
                );

        List<HistoryMessages> historyMessages = historyMessagesDao.selectList(queryWrapper);
        return historyMessages;
    }
}

