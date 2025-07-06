package com.geology.domain.bean;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.Date;

@Data
@Component
public class PolygonBean {
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;

    private Long userId;

    private String geom;

    private String description;

    private Date createTime;

    private String polygonName;

    private Integer status;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long groupid;
}
