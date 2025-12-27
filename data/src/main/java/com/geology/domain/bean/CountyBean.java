package com.geology.domain.bean;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.stereotype.Component;

@Data
@Component
public class CountyBean {
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long gid;

    private String gb;

    private String geom;
}