package com.geology.domain.DTO;

import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.Date;

@Data
@Component
public class PoiLocationDTO {
    private Long id;

    private Long picId;

    private Long userId;

    private String name;

    private String description;

    private Date createTime;

    private double lon;

    private double lat;
}
