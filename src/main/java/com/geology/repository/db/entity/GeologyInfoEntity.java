package com.geology.repository.db.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
@TableName("merge")
@Data
public class GeologyInfoEntity {
    @TableId("gid")
    private String gid;

    @TableField("QDUECD")
    private String QDUECD;

    @TableField("QDUECC")
    private String QDUECC;

    @TableField("YSHB")
    private String YSHB;

    @TableField("YSC")
    private String YSC;

    @TableField("YSJB")
    private String YSJB;

    @TableField("YSBBAV")
    private String YSBBAV;

    @TableField("MDAEC")
    private String MDAEC;

}
