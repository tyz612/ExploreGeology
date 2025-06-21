package com.geology.domain.bean;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.stereotype.Component;

@Data
@Component
public class TrackGeomBean {
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;

    private Long userId;

    private String geom;
}
