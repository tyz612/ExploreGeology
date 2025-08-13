package com.geology.user.common.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;

/**
 * Date:2024/9/3
 * author:zmh
 * description: 邮件发送工具类
 **/

@Component
@RequiredArgsConstructor
public class EmailUtil {

    @Autowired
    private JavaMailSender javaMailSender;

    // Redis
    private final RedisTemplate<String, String> redisTemplate;

    /**
     * 发送邮件
     * @param from 发送方
     * @param to 接收方
     * @param subject 主题
     * @param contents 内容
     * @return 返回执行结果状态
     */
    public void sendEmail(String from, String to, String subject, String contents){
        // 创建一个简单消息对象，用于发送简单消息（不带附件或连接等）
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        // 封装邮件信息
        simpleMailMessage.setFrom(from);
        simpleMailMessage.setTo(to);
        simpleMailMessage.setSubject(subject);
        simpleMailMessage.setText(contents);
        // 发送动作
        javaMailSender.send(simpleMailMessage);
    }

    /**
     * 生成4位随机数字验证码
     * @return `
     */
    public String generateCode() {
        SecureRandom random = new SecureRandom();
        int code = 1000 + random.nextInt(9000); // 生成1000-9999之间的随机数
        return String.valueOf(code);
    }

    /**
     *
     * @param emailAddress 注册的邮箱地址
     * @param verificationCode 注册验证码
     * @return ·
     */
    public Boolean verificationEmailCode(String emailAddress, String verificationCode){
        Boolean result = false;
        // 判断传入的邮箱地址是否存在redis中
        if(Boolean.TRUE.equals(redisTemplate.hasKey(emailAddress))){
            // 取出对应邮箱地址为key的value值
            String storyCode = redisTemplate.opsForValue().get(emailAddress);
            // 判断存在redis中的验证码和传入验证码是否相同，相同返回true
            result =  verificationCode.equals(storyCode);
        }
        return result;
    }

}
