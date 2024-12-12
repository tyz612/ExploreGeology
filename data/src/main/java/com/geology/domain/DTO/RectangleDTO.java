package com.geology.domain.DTO;

import lombok.Data;

@Data
public class RectangleDTO {
    private double minLon;

    private double minLat;

    private double maxLon;

    private double maxLat;

    private String keywords;
}
