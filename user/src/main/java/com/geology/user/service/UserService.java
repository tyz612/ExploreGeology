package com.geology.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.geology.user.common.bean.UserInfoBean;
import com.geology.user.model.domain.User;
import com.geology.user.model.domain.User;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * 用户服务
 *

 */
public interface UserService extends IService<User> {

    /**
     * 用户注册
     *
     * @param userAccount   用户账户
     * @param userPassword  用户密码
     * @param checkPassword 校验密码
     * @param planetCode    星球编号
     * @return 新用户 id
     *  `   `
     */
    long userRegister(String userAccount, String userPassword, String checkPassword, String planetCode, String phoneNumber, String userName, String createTime, String email, String emailVericode);

    /**
     * 用户登录
     *
     * @param userAccount  用户账户
     * @param userPassword 用户密码
     * @param request
     * @return 脱敏后的用户信息
     */
    UserInfoBean userLogin(String userAccount, String userPassword, HttpServletRequest request);

    UserInfoBean userLoginWithMail(String email, String verifyCode, HttpServletRequest request);

    String uploadImage(MultipartFile file) throws IOException;

    String updateUserName(String userName) throws IOException;

    /**
     * 用户脱敏
     *
     * @param originUser
     * @return
     */
    User getSafetyUser(User originUser);


    /**
     * 用户注销
     *
     * @param request
     * @return
     */
    int userLogout(HttpServletRequest request);
}
