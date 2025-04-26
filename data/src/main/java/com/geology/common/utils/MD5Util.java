package com.geology.common.utils;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Component;

@Component
public class MD5Util {
    /**
     * 生成32位小写MD5
     */
    public static String md5(String input) {
        return DigestUtils.md5Hex(input).toLowerCase();
    }

    /**
     * 生成32位大写MD5
     */
    public static String md5Upper(String input) {
        return DigestUtils.md5Hex(input).toUpperCase();
    }
}