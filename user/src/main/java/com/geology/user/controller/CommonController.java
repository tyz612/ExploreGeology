package com.geology.user.controller;

import com.geology.user.dto.R;
import com.geology.user.common.utils.EmailUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Map;
import java.util.concurrent.TimeUnit;


/**
 * Date:2024/9/3
 * author:zmh
 * description: 通用接口-文件操作-邮件操作
 **/


@RequestMapping("/common")
@CrossOrigin
@RestController
@RequiredArgsConstructor
public class CommonController {

    // 邮件发送地址（from）
    @Value("${spring.mail.username}")
    private String selfMail;

    private final EmailUtil emailUtil;

    // Redis
    private final RedisTemplate<String, String> redisTemplate;

    @Resource
    @Qualifier("redisTemplateMap")
    private RedisTemplate<String, Map<String,String>> redisTemplateMap;

    /**
     * 邮件发送
     * @param emailAddress 前端验证过格式的邮箱地址
     */
    @GetMapping("/getEmailVerificationCode")
    public R<String> sendEmail(@RequestParam String emailAddress){
        // 邮件主题
        String emailSubject = "注册账号-聊天室";
        // 生成验证码
        String verificationCode = emailUtil.generateCode();
        // 构建邮件内容
        String content = "【在线聊天室】注册验证码："+verificationCode+"。5分钟内有效，请勿泄露。";
        emailUtil.sendEmail(selfMail, emailAddress, emailSubject, content);

        // 将传入邮箱地址为key，验证码为value，存入redis中，并设置超时时间为5分钟
        redisTemplate.opsForValue().set(emailAddress,verificationCode,5, TimeUnit.MINUTES);
        return R.success("邮件发送成功");
    }

    // 根据传入ID获取Redis中Map里面的名称
    @GetMapping("/getNameByReceiverId")
    public String getNameByReceiverId(String receiverId){
        Map<String, String> receiverMap = redisTemplateMap.opsForValue().get("receiver-info");
        assert receiverMap != null;
        String receiverName = receiverMap.get(receiverId);
        System.out.println(receiverName);
        return receiverName;
    }

}
