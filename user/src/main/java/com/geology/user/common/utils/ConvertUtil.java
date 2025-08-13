package com.geology.user.common.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Date:2024/9/7
 * author:zmh
 * description:转换工具
 **/


public class ConvertUtil {

    /**
     * 将对象转为Json格式数据
     * @param object 需要转换的对象
     * @return ·
     */
    public static String ObjectToJson(Object object){
        ObjectMapper objectMapper = new ObjectMapper();
        String returnStr = "默认字符串";
        try {
            // 将历史消息记录数据 转为JSON字符串返回给前端
            returnStr = objectMapper.writeValueAsString(object);

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return returnStr;
    }

    /**
     * JSON字符串转为对象
     * @param jsonString JSON
     * @param clazz 对象类型
     * @return ·
     * @param <T> ·
     */
    public static <T> T JsonToObject(String jsonString, Class<T> clazz) {
        ObjectMapper objectMapper = new ObjectMapper();
        T object = null;
        try {
            // 将 JSON 字符串转为指定类型的对象
            object = objectMapper.readValue(jsonString, clazz);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("JSON 解析失败", e);
        }
        return object;
    }
}
