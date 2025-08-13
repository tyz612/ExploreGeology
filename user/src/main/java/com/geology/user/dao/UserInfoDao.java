package com.geology.user.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.geology.user.pojo.UserInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * 用户信息表(UserInfo)表数据库访问层
 *
 * @author zmh
 * @since 2024-09-02 17:22:05
 */
@Mapper
public interface UserInfoDao extends BaseMapper<UserInfo> {

    /**
     * 根据用户ID查询用户名称
     */
    @Select("select name from userinfo where id = #{userId}")
    String selectUserNameByUserId(String userId);

}

