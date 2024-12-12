package com.geology.domain.bean;

import lombok.Data;

@Data
public class GeologyTypeGeometryBean {
    private Long gid;

    private String qduecd;

    private String qduecc;

    private String yshb;

    private String mdaec;

    private double intersectionArea;

    private String geojsonFeature;
}
