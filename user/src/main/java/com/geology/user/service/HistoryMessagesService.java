package com.geology.user.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.geology.user.dto.R;
import com.geology.user.pojo.HistoryMessages;


import java.util.List;

/**
 * 历史消息表(HistoryMessages)表服务接口
 *
 * @author zmh
 * @since 2024-09-02 17:22:05
 */
public interface HistoryMessagesService extends IService<HistoryMessages> {

    /**
     * 分页查询
     *
     * @param page 查询页数
     * @param size 一页显示条数
     * @return ·
     */
    R<Page<HistoryMessages>> getAllByPage(int page, int size);

    // 传入用户id和最后一条本地聊天记录的时间戳，对比获取未同步的增量数据
    R<List<HistoryMessages>> getIncrementByTimestamp(String userId, String lastTimestamp);

    String saveMessage(String receiver, String message);

    String saveShareMessage(String receiver, String dataId, String dataType);

    List<HistoryMessages> getHistoryMessagesById(String receiver);
}

