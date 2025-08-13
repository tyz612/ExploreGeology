package com.geology.user.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.geology.user.pojo.HistoryMessages;
import org.apache.ibatis.annotations.Mapper;

/**
 * 历史消息表(HistoryMessages)表数据库访问层
 *
 * @author zmh
 * @since 2024-09-02 17:22:05
 */
@Mapper
public interface HistoryMessagesDao extends BaseMapper<HistoryMessages> {

}

