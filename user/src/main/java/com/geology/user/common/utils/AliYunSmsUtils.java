package com.geology.user.common.utils;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import org.springframework.stereotype.Component;

@Component
public class AliYunSmsUtils {
    public static String accessKeyId = "LTAI5tKfKGwzvtVEkYJCEkdX";
    public static String accessSecret = "I0fP5udXR9K0oTGVJNQF3RWypXwRED";
    public static String signName = "geology612";
    public static String TemplateCode = "SMS_476890036";
    public static final String PRODUCT = "Dysmsapi";
    public static final String DOMAIN = "dysmsapi.aliyuncs.com";

    public static SendSmsResponse sendSms(String phoneNumber, String code) {
        SendSmsResponse sendSmsResponse = null;
        try {
            IClientProfile profile = DefaultProfile.getProfile("cn-hangzhou", accessKeyId, accessSecret);
            DefaultProfile.addEndpoint("cn-hangzhou", "cn-hangzhou", PRODUCT, DOMAIN);
            IAcsClient acsClient = new DefaultAcsClient(profile);
            SendSmsRequest request = new SendSmsRequest();
            request.setPhoneNumbers(phoneNumber);
            request.setSignName(signName);
            request.setTemplateCode(TemplateCode);
            request.setTemplateParam("{\"code\":\"" + code + "\"}");
            sendSmsResponse = acsClient.getAcsResponse(request);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sendSmsResponse;
    }
}