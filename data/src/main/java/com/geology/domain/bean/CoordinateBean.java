package com.geology.domain.bean;

import lombok.Data;

@Data
public class CoordinateBean {
    private double lon;

    private double lat;

    public CoordinateBean(double lon, double lat) {
        this.lon = lon;
        this.lat = lat;
    }
}
