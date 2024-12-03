package com.geology.service;

import com.geology.domain.DTO.RectifyDTO;
import com.geology.domain.DTO.SingleCoordinateTransformDTO;
import com.geology.domain.bean.RectifyInfoBean;
import com.geology.domain.bean.RectifyTIFInfoBean;
import com.geology.domain.bean.SingleCoordinateTransformBean;
import com.geology.domain.bean.TIFInfoBean;
import org.gdal.gdal.GCP;

import java.util.ArrayList;

public interface GeoTools {
    TIFInfoBean getTIFInfo(String fileName);

    String clipImage(String inputRaster, String inputShp, String outputRaster);

    void testDriver();

    RectifyInfoBean generateGCPsFile(RectifyDTO rectifyDTO);

    ArrayList<GCP> generateGCPs(RectifyTIFInfoBean rectifyTIFInfoBean);

    String rectifyByGCP(RectifyTIFInfoBean rectifyTIFInfoBean);

    SingleCoordinateTransformBean singleCoordinateTransform(SingleCoordinateTransformDTO singleCoordinateTransformDTO);

}
