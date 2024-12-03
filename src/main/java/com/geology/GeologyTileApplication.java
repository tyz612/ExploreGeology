package com.geology;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan(basePackages = {"com.geology.**.db.mapper"})
public class GeologyTileApplication {

    public static void main(String[] args) {
        SpringApplication.run(GeologyTileApplication.class, args);
    }

}
