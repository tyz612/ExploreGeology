package com.geology.domain.bean;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.util.Date;
@Data
public class UserPhotoBean {
    private Long photoId;

    private Long userId;

    private Long markerId;

    private String picName;

    private String filePath;

    private Date createTime;

    private Date photoTime;

    private Integer status;

    private String geom;
}
