package com.geology.repository.db.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@TableName("dzt02")
@Data
public class ChinaGeologyInfo {
    @TableField("symbol")
    private String symbol;

    @TableField("unitname")
    private String unitname;

    @TableField("character")
    private String character;

}
