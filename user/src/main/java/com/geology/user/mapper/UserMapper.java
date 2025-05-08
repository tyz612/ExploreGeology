package com.geology.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.geology.user.model.domain.User;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 用户 Mapper
 *

 */
public interface UserMapper extends BaseMapper<User> {
    @Select("select * from userinfo where user_account = #{userAccount} and user_password = #{userPassword}")
    User loginByPassword(@Param("userAccount") String userAccount,
                         @Param("userPassword") String userPassword);

    @Select("select * from userinfo where user_account = #{userAccount}")
    User getUserInfoByUserAccount(@Param("userAccount") String userAccount);

    @Select("select * from userinfo where email = #{email}")
    User getUserInfoByUserEmail(@Param("email") String email);

    @Select("SELECT COUNT(*) FROM userinfo where user_account = #{userAccount};")
    Integer checkUserAccount(@Param("userAccount") String userAccount);
}


