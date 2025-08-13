package com.geology.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.geology.user.common.DistributedIdGenerator;
import com.geology.user.common.bean.UserInfoBean;
import com.geology.user.common.config.PasswordEncoder;
import com.geology.user.common.utils.FileStorageUtil;
import com.geology.user.common.utils.GenerateTokenUtil;
import com.geology.user.jwt.AuthStorage;
import com.geology.user.jwt.JwtUser;
import com.geology.user.jwt.TokenProvider;
import com.geology.user.mapper.UserMapper;
import com.geology.user.model.domain.User;
import com.geology.user.service.UserService;
import com.geology.user.common.ErrorCode;
import com.geology.user.exception.BusinessException;
import com.geology.user.mapper.UserMapper;
import com.geology.user.model.domain.User;
import com.geology.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Generated;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.geology.user.contant.UserConstant.USER_LOGIN_STATE;

/**
 * 用户服务实现类
 *

 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
        implements UserService {

//    @Resource
//    private UserMapper userMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private GenerateTokenUtil generateTokenUtil;

    @Autowired
    private FileStorageUtil fileStorageUtil;

    @Value("${file.upload-dir}")
    private String uploadDir;
    /**
     * 盐值，混淆密码
     */
    private static final String SALT = "yupi";

    /**
     * 用户注册
     *
     * @param userAccount   用户账户
     * @param userPassword  用户密码
     * @param checkPassword 校验密码
     * @param planetCode    星球编号
     * @return 新用户 id
     */
    @Override
    public long userRegister(String userAccount, String userPassword, String checkPassword, String planetCode,
                             String phoneNumber, String userName, String createTime, String email, String emailVericode) {
        // 1. 校验
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword, planetCode)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户账号过短");
        }
        if (userPassword.length() < 8 || checkPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户密码过短");
        }
        if (planetCode.length() > 5) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "星球编号过长");
        }
        // 账户不能包含特殊字符
        String validPattern = "[`~!@#$%^&*()+=|{}':;',\\\\[\\\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        Matcher matcher = Pattern.compile(validPattern).matcher(userAccount);
        if (matcher.find()) {
            return -1;
        }
        // 密码和校验密码相同
        if (!userPassword.equals(checkPassword)) {
            return -1;
        }
        // 1. 账户不能重复
        long count = userMapper.checkUserAccount(userAccount);
        if (count > 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号重复");
        }

        String redisKey = "CAPTCHA:REGISTER:" + email;
        String storedCode = redisTemplate.opsForValue().get(redisKey).toString();

        if (storedCode.equals(emailVericode)) {
            // 2. 加密
            String encryptPassword = DigestUtils.md5DigestAsHex((userPassword).getBytes());

            // 3. 插入数据
            User user = new User();
            user.setUserAccount(userAccount);
            user.setUserPassword(encryptPassword);
            user.setPlanetCode(planetCode);
            user.setPhone(phoneNumber);
            user.setUserName(userName);
            user.setCreateTime(createTime);
            user.setEmail(email);

            Long taskId = DistributedIdGenerator.getInstance().nextId();
            user.setId(taskId);
            boolean saveResult = this.save(user);
            if (!saveResult) {
                return -1;
            }
            return user.getId();
        }
        else
        {
            throw new RuntimeException("验证码错误，请稍后重试！");
        }

    }

    /**
     * 用户登录
     *
     * @param userAccount  用户账户
     * @param userPassword 用户密码
     * @param request
     * @return 脱敏后的用户信息
     */
    @Override
    public UserInfoBean userLogin(String userAccount, String userPassword, HttpServletRequest request) {
        // 1. 校验
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            return null;
        }
        if (userAccount.length() < 4) {
            return null;
        }
        if (userPassword.length() < 8) {
            return null;
        }
        // 账户不能包含特殊字符
        String validPattern = "[`~!@#$%^&*()+=|{}':;',\\\\[\\\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        Matcher matcher = Pattern.compile(validPattern).matcher(userAccount);
        if (matcher.find()) {
            return null;
        }
        // 2. 加密
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
        // 查询用户是否存在

        User user = userMapper.getUserInfoByUserAccount(userAccount);

        // 用户不存在
        if (user == null) {
            log.info("user login failed, userAccount cannot match userPassword");
            throw new RuntimeException("用户名或密码错误！");
        }
        else {
        if (PasswordEncoder.matches(userPassword, user.getUserPassword())) {
            UserInfoBean userInfoBean = new UserInfoBean();

            String token = TokenProvider.createToken(user.getId().toString(), "web", "admin");

            userInfoBean.setId(user.getId());
            userInfoBean.setToken(token);
            userInfoBean.setAvatarUrl(user.getAvatarUrl());
            userInfoBean.setEmail(user.getEmail());
            userInfoBean.setUserName(user.getUserName());

//            this.saveTokenAfterLogin(user.getUserName().toString(), TokenProvider.createToken(user.getId().toString(), "web", "admin"), 3000);
            // 模拟一个用户的数据 用户id为1  登录端为网页web  角色是admin
            return userInfoBean;
        }
            throw new RuntimeException("用户名或密码错误！");
        }
    }

    public UserInfoBean userLoginWithMail(String email, String verifyCode, HttpServletRequest request)
    {
        if (StringUtils.isAnyBlank(email)) {
            throw new RuntimeException("邮箱不能为空！");
        }
        if (StringUtils.isAnyBlank(verifyCode)) {
            throw new RuntimeException("密码不能为空！");
        }

        User user = userMapper.getUserInfoByUserEmail(email);
        String redisKey = "CAPTCHA:REGISTER:" + email;
        String storedCode = redisTemplate.opsForValue().get(redisKey).toString();

        if (storedCode.equals(verifyCode)) {
            UserInfoBean userInfoBean = new UserInfoBean();

            String token = TokenProvider.createToken(user.getId().toString(), "web", "admin");

            userInfoBean.setToken(token);
            userInfoBean.setAvatarUrl(user.getAvatarUrl());
            userInfoBean.setEmail(user.getEmail());
            userInfoBean.setUserName(user.getUserName());

            return userInfoBean;
        }
        else
        {
            throw new RuntimeException("验证码错误，请稍后重试！");
        }
    }

    @Override
    public String uploadImage(MultipartFile file) throws IOException {
        JwtUser user = AuthStorage.getUser();
        long userId = Long.parseLong(user.getUserId());

        String originalFilename = file.getOriginalFilename();
        String storedFilename = fileStorageUtil.storeFile(file);

        userMapper.updateUserAvatar(storedFilename, userId);

        return storedFilename;
    }

    @Override
    public String updateUserName(String userName) throws IOException {
        JwtUser user = AuthStorage.getUser();
        long userId = Long.parseLong(user.getUserId());

        if (userName.length() > 15)
        {
           throw new RuntimeException("名称过长！") ;
        }

        userMapper.updateUserName(userName, userId);

        return userName;
    }


    /**
     * 用户脱敏
     *
     * @param originUser
     * @return
     */
    @Override
    public User getSafetyUser(User originUser) {
        if (originUser == null) {
            return null;
        }
        User safetyUser = new User();
        safetyUser.setId(originUser.getId());
        safetyUser.setUserName(originUser.getUserName());
        safetyUser.setUserAccount(originUser.getUserAccount());
        safetyUser.setAvatarUrl(originUser.getAvatarUrl());
        safetyUser.setGender(originUser.getGender());
        safetyUser.setPhone(originUser.getPhone());
        safetyUser.setEmail(originUser.getEmail());
        safetyUser.setPlanetCode(originUser.getPlanetCode());
        safetyUser.setUserRole(originUser.getUserRole());
        safetyUser.setUserStatus(originUser.getUserStatus());
        safetyUser.setCreateTime(originUser.getCreateTime());
        return safetyUser;
    }

    /**
     * 用户注销
     *
     * @param request
     */
    @Override
    public int userLogout(HttpServletRequest request) {
        // 移除登录态
        request.getSession().removeAttribute(USER_LOGIN_STATE);
        return 1;
    }

    private void saveTokenAfterLogin(String userAccount, String token, long tokenExpiration) {
         //序列化器，确保存储在Redis中的是JSON格式的字符串
        Jackson2JsonRedisSerializer<Object> serializer = new Jackson2JsonRedisSerializer<>(Object.class);
        redisTemplate.setKeySerializer(serializer);
        redisTemplate.setValueSerializer(serializer);

        // 将token作为值，用户ID作为key保存到Redis中
        redisTemplate.opsForValue().set(userAccount, token, tokenExpiration, TimeUnit.MINUTES);
    }
}