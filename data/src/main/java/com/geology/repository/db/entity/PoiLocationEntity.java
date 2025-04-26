package com.geology.repository.db.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.postgis.Point;

import java.util.Date;

@TableName("picture_locations")
@Data
public class PoiLocationEntity {
    @TableId("id")
    private Long id;

    @TableField("pic_id")
    private Long picId;

    @TableField("user_id")
    private Long userId;

    @TableField("description")
    private String description;

    @TableField("create_time")
    private Date createTime;

    @TableField("geom")
    private String geom;

    @TableField("name")
    private String name;

}
