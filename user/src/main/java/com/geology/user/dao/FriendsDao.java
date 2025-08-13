package com.geology.user.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.geology.user.pojo.Friends;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Component;

/**
 * (Friends)表数据库访问层
 *
 * @author zmh
 * @since 2024-09-10 16:51:10
 */
@Mapper
public interface FriendsDao extends BaseMapper<Friends> {

    @Update("UPDATE friends SET state = #{state} WHERE user_id = #{userId} AND contact_id = #{contactId}")
    int updateState(@Param("userId") String userId, @Param("contactId") String contactId, @Param("state") Integer state);

}

