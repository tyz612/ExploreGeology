package com.geology.domain.bean;

import lombok.Data;

@Data
public class SingleCoordinateTransformBean {
    private double oriLon;

    private double oriLat;

    private double dstLon;

    private double dstLat;

    private Integer srcSRS;

    private Integer dstSRS;
}
