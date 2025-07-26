package com.geology.user.common.utils;

import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class RockNamePicker {
    // 岩石名称数据集（包含40种常见岩石）
    private final String[] ROCK_NAMES = {
            "花岗岩", "玄武岩", "石灰岩", "大理岩", "页岩",
            "片麻岩", "砂岩", "板岩", "石英岩", "安山岩",
            "流纹岩", "辉长岩", "橄榄岩", "闪长岩", "凝灰岩",
            "角砾岩", "砾岩", "白云岩", "片岩", "千枚岩",
            "蛇纹岩", "角闪岩", "麻粒岩", "榴辉岩", "燧石",
            "煤", "盐岩", "石膏岩", "浮石", "黑曜岩",
            "珍珠岩", "火山渣", "伟晶岩", "金伯利岩", "斜长岩",
            "响岩", "粗面岩", "英安岩", "辉绿岩", "云母片岩"
    };

    private final Random RANDOM = new Random();

    /**
     * 随机返回一个岩石名称
     * @return 随机岩石名称字符串
     */
    public String pickRandomRock() {
        return ROCK_NAMES[RANDOM.nextInt(ROCK_NAMES.length)];
    }

}
