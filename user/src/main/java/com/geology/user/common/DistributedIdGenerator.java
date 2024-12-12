package com.geology.user.common;

import cn.hutool.core.lang.generator.SnowflakeGenerator;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DistributedIdGenerator {
    SnowflakeGenerator snowflakeGenerator;

    private DistributedIdGenerator() {
        snowflakeGenerator = new SnowflakeGenerator();

    }

    private static DistributedIdGenerator instance = null;

    public static synchronized DistributedIdGenerator getInstance() {
        if (instance == null) {
            instance = new DistributedIdGenerator();
        }
        return instance;
    }

    public Long nextId() {
        return snowflakeGenerator.next();
    }
}
