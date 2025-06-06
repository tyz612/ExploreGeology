package com.geology.repository.db.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import javax.persistence.*;
import java.time.Instant;
import java.util.Date;

@Data
@TableName("tracks")
public class TrackEntity {
    @TableId("id")
    private Long id;

    @TableField("user_id")
    private Long userId;

    @TableField("description")
    private String description;

    @TableField("start_time")
    private String startTime;

    @TableField("end_time")
    private String endTime;

    @TableField("create_time")
    private Date createTime;

    @TableField("name")
    private String name;

    @TableField("status")
    private Integer status;

    @TableField("geom")
    private String geom;

    @TableField("max_altitude")
    private float maxAltitude;

    @TableField("min_altitude")
    private float minAltitude;

    @TableField("distance")
    private float distance;
}
