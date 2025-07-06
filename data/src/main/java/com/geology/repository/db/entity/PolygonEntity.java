package com.geology.repository.db.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("polygon")
public class PolygonEntity {
    @TableId("id")
    private Long id;

    @TableField("group_id")
    private Long groupid;

    @TableField("user_id")
    private Long userId;

    @TableField("description")
    private String description;

    @TableField("create_time")
    private Date createTime;

    @TableField("polygon_name")
    private String polygonName;

    @TableField("status")
    private Integer status;

    @TableField("geom")
    private String geom;

}
