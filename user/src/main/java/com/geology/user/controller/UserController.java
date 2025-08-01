package com.geology.user.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.geology.user.common.bean.UserInfoBean;
import com.geology.user.common.utils.ApiResponse;
import com.geology.user.common.BaseResponse;
import com.geology.user.common.ErrorCode;
import com.geology.user.common.ResultUtils;
import com.geology.user.common.utils.GenerateCaptchaUtil;
import com.geology.user.common.utils.MailClientUtil;
import com.geology.user.common.utils.RockNamePicker;
import com.geology.user.contant.UserConstant;
import com.geology.user.exception.BusinessException;
import com.geology.user.jwt.JwtUser;
import com.geology.user.jwt.TokenProvider;
import com.geology.user.mapper.UserMapper;
import com.geology.user.model.domain.User;
import com.geology.user.model.domain.request.UserLoginMailRequest;
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
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.geology.user.jwt.AuthStorage;
import org.springframework.web.multipart.MultipartFile;

import static com.geology.user.contant.UserConstant.USER_LOGIN_STATE;

/**
 * 用户接口
 *

 */
@RestController
@RequestMapping("/user")
//@CrossOrigin(origins = "http://localhost:8081")
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

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RockNamePicker rockNamePicker;

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

//        String userName = userRegisterRequest.getUserName();
        String userName = rockNamePicker.pickRandomRock();

        LocalDateTime now = LocalDateTime.now(); // 获取当前日期和时间
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String createTime = now.format(formatter);
        String email = userRegisterRequest.getEmail();
        String emailVericode = userRegisterRequest.getEmailVericode();

        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword, planetCode, userName)) {
            return null;
        }
        long result = userService.userRegister(userAccount, userPassword, checkPassword, planetCode, phoneNumber, userName, createTime, email,emailVericode);
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
    public BaseResponse<UserInfoBean> userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request) {
        if (userLoginRequest == null) {
            return ResultUtils.error(ErrorCode.PARAMS_ERROR);
        }
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            return ResultUtils.error(ErrorCode.PARAMS_ERROR);
        }
        UserInfoBean userInfoBean = userService.userLogin(userAccount, userPassword, request);


        return ResultUtils.success(userInfoBean);
    }


    @CrossOrigin(origins = "*")
    @PostMapping("/loginWithMail")
    public BaseResponse<UserInfoBean> loginWithMail(@RequestBody UserLoginMailRequest userLoginMailRequest, HttpServletRequest request) {
        if (userLoginMailRequest == null) {
            return ResultUtils.error(ErrorCode.PARAMS_ERROR);
        }
        String email = userLoginMailRequest.getEmail();
        String verifyCode = userLoginMailRequest.getVerifyCode();
        if (StringUtils.isAnyBlank(email, verifyCode)) {
            return ResultUtils.error(ErrorCode.PARAMS_ERROR);
        }
        UserInfoBean userInfoBean = userService.userLoginWithMail(email, verifyCode, request);


        return ResultUtils.success(userInfoBean);
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
    @GetMapping("/info")
    public BaseResponse<UserInfoBean> getInfo() {
        // 从全局环境中获取用户id
        JwtUser user = AuthStorage.getUser();
        long userId = Long.parseLong(user.getUserId());
        // TODO 校验用户是否合法

        UserInfoBean userInfoBean = userMapper.getUserInfoByUserId(userId);

        return ResultUtils.success(userInfoBean);
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
    public ApiResponse<String> sendCaptcha(@RequestParam("to") String to) {
        try {
            String verifyCode = generateCaptchaUtil.generateCaptcha();
            String redisKey = "CAPTCHA:REGISTER:" + to;
            redisTemplate.opsForValue().set(redisKey, verifyCode, 2, TimeUnit.MINUTES);
            mailClientUtil.sendMail(to, "欢迎来到奥陶纪世界，您的验证码是：", verifyCode.concat("，验证码2分钟内有效。"));
            return ApiResponse.success("success");
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @CrossOrigin(origins = "*")
    @PostMapping("/uploadAvatar")
    public ApiResponse<String> uploadAvatar(@RequestParam("image") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return ApiResponse.fail(400,"请选择要上传的图片");
            }

            String uploadedImage = userService.uploadImage(file);
            return ApiResponse.success(uploadedImage);

        } catch (IllegalArgumentException e) {
            return ApiResponse.fail(400, e.getMessage());
        } catch (IOException e) {
            return ApiResponse.fail(400,"图片上传失败: " + e.getMessage());
        }
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/updateName")
    public ApiResponse<String> updateName(@RequestParam("newName") String newName) {
        try {
            String userNewName = userService.updateUserName(newName);
            return ApiResponse.success(userNewName);

        } catch (IllegalArgumentException e) {
            return ApiResponse.fail(400, e.getMessage());
        } catch (IOException e) {
            return ApiResponse.fail(400,"昵称修改失败: " + e.getMessage());
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
