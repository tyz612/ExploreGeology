package com.geology.user.common.utils;

import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class GenerateCaptchaUtil {
    public String generateCaptcha() {
        Random random = new Random();
        return String.format("%06d", random.nextInt(900000) + 100000);
    }
}
