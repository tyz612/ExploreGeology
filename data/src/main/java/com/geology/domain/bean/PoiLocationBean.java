package com.geology.domain.bean;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.Date;

@Data
@Component
public class PoiLocationBean {
    private Long id;

    private Long picId;

    private Long userId;

    private String name;

    private String description;

    private Date createTime;

    private String geom;

    private String filePath;
}
