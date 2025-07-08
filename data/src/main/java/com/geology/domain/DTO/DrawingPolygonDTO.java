package com.geology.domain.DTO;


import lombok.Data;

@Data
public class DrawingPolygonDTO {

    private String polygonGeoJSON;

    private String name;

    private String description;

    private int type;

    private double minLon;

    private double minLat;

    private double maxLon;

    private double maxLat;
}
