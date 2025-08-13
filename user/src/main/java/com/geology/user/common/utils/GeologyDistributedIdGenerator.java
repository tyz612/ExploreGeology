package com.geology.user.common.utils;

import cn.hutool.core.lang.generator.SnowflakeGenerator;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GeologyDistributedIdGenerator {
    SnowflakeGenerator snowflakeGenerator;

    private GeologyDistributedIdGenerator() {
        snowflakeGenerator = new SnowflakeGenerator();

    }

    private static GeologyDistributedIdGenerator instance = null;

    public static synchronized GeologyDistributedIdGenerator getInstance() {
        if (instance == null) {
            instance = new GeologyDistributedIdGenerator();
        }
        return instance;
    }

    public Long nextId() {
        return snowflakeGenerator.next();
    }
}
