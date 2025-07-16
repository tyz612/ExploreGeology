package com.geology.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.geology.user.common.bean.UserInfoBean;
import com.geology.user.model.domain.User;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

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

    @Update("UPDATE userinfo SET avatar_url = #{fileName} WHERE id = #{userId};")
    void updateUserAvatar(@Param("fileName") String fileName, @Param("userId") Long userId);


    @Update("UPDATE userinfo SET user_name = #{newName} WHERE id = #{userId};")
    void updateUserName(@Param("newName") String newName, @Param("userId") Long userId);

    @Select("select u.user_name as userName, u.avatar_url as avatarUrl, u.email as email from userinfo u where id = #{userId}")
    UserInfoBean getUserInfoByUserId(@Param("userId") Long userId);
}


