package com.geology.user.service.impl;

import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.geology.user.common.utils.AliYunSmsUtils;
import com.geology.user.common.utils.GenerateCaptchaUtil;
import com.geology.user.common.utils.GenerateTokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class SmsSendService {

    @Autowired
    private AliYunSmsUtils aliYunSmsUtils;

    @Autowired
    private GenerateCaptchaUtil generateCaptchaUtil;

    public void sendVerifyCode(String phoneNumber){
        String verifyCode = generateCaptchaUtil.generateCaptcha();
        SendSmsResponse sendSms = aliYunSmsUtils.sendSms(phoneNumber, verifyCode);
        log.info("Code=" + sendSms.getCode());
        log.info("Message=" + sendSms.getMessage());
    }
}
