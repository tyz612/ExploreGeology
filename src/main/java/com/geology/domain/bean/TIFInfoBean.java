package com.geology.domain.bean;

import lombok.Data;
import org.gdal.osr.SpatialReference;

@Data
public class TIFInfoBean {

    private String driver;

    private int xSize;

    private int ySize;

    private int bands;

    private String projection;

    private int driverCount;

    private double[] geotransform;

}
