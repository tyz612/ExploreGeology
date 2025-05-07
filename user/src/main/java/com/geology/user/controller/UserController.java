package com.geology.user.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.geology.user.common.utils.ApiResponse;
import com.geology.user.common.BaseResponse;
import com.geology.user.common.ErrorCode;
import com.geology.user.common.ResultUtils;
import com.geology.user.common.utils.GenerateCaptchaUtil;
import com.geology.user.common.utils.MailClientUtil;
import com.geology.user.contant.UserConstant;
import com.geology.user.exception.BusinessException;
import com.geology.user.jwt.JwtUser;
import com.geology.user.jwt.TokenProvider;
import com.geology.user.model.domain.User;
import com.geology.user.model.domain.request.UserLoginRequest;
import com.geology.user.model.domain.request.UserRegisterRequest;
import com.geology.user.service.UserService;
import com.geology.user.service.impl.SmsSendService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.geology.user.jwt.AuthStorage;

import static com.geology.user.contant.UserConstant.USER_LOGIN_STATE;

/**
 * 用户接口
 *

 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserService userService;

    @Autowired
    private MailClientUtil mailClientUtil;

    @Autowired
    private GenerateCaptchaUtil generateCaptchaUtil;

    @Autowired
    private SmsSendService smsSendService;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    /**
     * 用户注册
     *
     * @param userRegisterRequest
     * @return
     */
    @CrossOrigin(origins = "*")
    @PostMapping("/register")
    public BaseResponse<Long> userRegister(@RequestBody UserRegisterRequest userRegisterRequest) {
        // 校验
        if (userRegisterRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();
        String planetCode = "002";
        String phoneNumber = userRegisterRequest.getPhoneNumber();
        String userName = userRegisterRequest.getUserName();
        LocalDateTime now = LocalDateTime.now(); // 获取当前日期和时间
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String createTime = now.format(formatter);
        String email = userRegisterRequest.getEmail();
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword, planetCode, userName)) {
            return null;
        }
        long result = userService.userRegister(userAccount, userPassword, checkPassword, planetCode, phoneNumber, userName, createTime, email);
        return ResultUtils.success(result);
    }

    /**
     * 用户登录
     *
     * @param userLoginRequest
     * @param request
     * @return
     */
    @CrossOrigin(origins = "*")
    @PostMapping("/login")
    public BaseResponse<String> userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request) {
        if (userLoginRequest == null) {
            return ResultUtils.error(ErrorCode.PARAMS_ERROR);
        }
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            return ResultUtils.error(ErrorCode.PARAMS_ERROR);
        }
        String token = userService.userLogin(userAccount, userPassword, request);


        return ResultUtils.success(token);
    }

    /**
     * 用户注销
     *
     * @param request
     * @return
     */
    @CrossOrigin(origins = "*")
    @PostMapping("/logout")
    public BaseResponse<Integer> userLogout(HttpServletRequest request) {
        if (request == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        int result = userService.userLogout(request);
        return ResultUtils.success(result);
    }

    /**
     * 获取当前用户
     *
     * @param request
     * @return
     */
    @CrossOrigin(origins = "*")
    @GetMapping("/current")
    public BaseResponse<User> getCurrentUser(HttpServletRequest request) {
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User currentUser = (User) userObj;
        if (currentUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        long userId = currentUser.getId();
        // TODO 校验用户是否合法
        User user = userService.getById(userId);
        User safetyUser = userService.getSafetyUser(user);
        return ResultUtils.success(safetyUser);
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/CurrentUser")
    public ApiResponse<Long> getInfo() {
        // 从全局环境中获取用户id
        JwtUser user = AuthStorage.getUser();
        long userId = Long.parseLong(user.getUserId());
        // TODO 校验用户是否合法

        return ApiResponse.success(userId);
    }



    @CrossOrigin(origins = "*")
    @GetMapping("/token/validate")
    public JwtUser tokenValidate(String token) {
        return TokenProvider.checkToken(token);
    }


    @CrossOrigin(origins = "*")
    @GetMapping("/search")
    public BaseResponse<List<User>> searchUsers(String username, HttpServletRequest request) {
        if (!isAdmin(request)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotBlank(username)) {
            queryWrapper.like("username", username);
        }
        List<User> userList = userService.list(queryWrapper);
        List<User> list = userList.stream().map(user -> userService.getSafetyUser(user)).collect(Collectors.toList());
        return ResultUtils.success(list);
    }

    @CrossOrigin(origins = "*")
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteUser(@RequestBody long id, HttpServletRequest request) {
        if (!isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean b = userService.removeById(id);
        return ResultUtils.success(b);
    }


    /**
     * 是否为管理员
     *
     * @param request
     * @return
     */
    private boolean isAdmin(HttpServletRequest request) {
        // 仅管理员可查询
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User user = (User) userObj;
        return user != null && user.getUserRole() == UserConstant.ADMIN_ROLE;
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/sendCaptcha")
    public ResponseEntity<String> sendCaptcha(@RequestParam("to") String to) {
        try {
            String verifyCode = generateCaptchaUtil.generateCaptcha();
            String redisKey = "CAPTCHA:REGISTER:" + to;
            redisTemplate.opsForValue().set(redisKey, verifyCode, 2, TimeUnit.MINUTES);
            mailClientUtil.sendMail(to, "欢迎来到奥陶纪世界，您的验证码是：", verifyCode.concat("，验证码2分钟内有效。"));
            return ResponseEntity.ok().body("success");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error sending captcha");
        }
    }


    @CrossOrigin(origins = "*")
    @GetMapping("/test")
    public String hello() {
        return "hello";
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/sendSmsCode")
    public ResponseEntity<String> sendSmsCode(@RequestParam("phoneNumber") String phoneNumber) {
        smsSendService.sendVerifyCode(phoneNumber);
        return ResponseEntity.ok().body("success");
    }

}
