package com.geology.user.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.geology.user.pojo.Share;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface ShareDao extends BaseMapper<Share> {

    @Update("UPDATE share SET status = #{status} WHERE  data_id = #{dataId}")
    int updateStatus(@Param("status") Integer status, @Param("dataId") String dataId);

}
