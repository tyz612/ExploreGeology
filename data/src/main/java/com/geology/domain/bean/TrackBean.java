package com.geology.domain.bean;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.Date;

@Data
@Component

public class TrackBean {
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;

    private Long userId;

    private String geom;

    private String description;

    private String startTime;

    private String endTime;

    private Date createTime;

    private String name;

    private Integer status;

    private float distance;

    private float minAltitude;

    private float maxAltitude;
}
