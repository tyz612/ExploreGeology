package com.geology.repository.db.entity;

import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@TableName("photos")
@Data
public class UserPhotoEntity {
    @TableId("photo_id")
    private Long photoId;

    @TableField("user_id")
    private Long userId;

    @TableField("pic_name")
    private String picName;

    @TableField("file_path")
    private String filePath;

    @TableField("create_time")
    private Date createTime;

    @TableField("photo_time")
    private Date photoTime;

    @TableField("status")
    private Integer status;

    @TableField("geom")
    private String geom;
}
