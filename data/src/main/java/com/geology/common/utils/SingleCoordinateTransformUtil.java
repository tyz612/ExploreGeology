package com.geology.common.utils;

import com.geology.domain.DTO.SingleCoordinateTransformDTO;
import com.geology.domain.bean.SingleCoordinateTransformBean;
import lombok.extern.slf4j.Slf4j;
import org.gdal.gdal.gdal;
import org.gdal.osr.CoordinateTransformation;
import org.gdal.osr.SpatialReference;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class SingleCoordinateTransformUtil {
    public SingleCoordinateTransformBean singleCoordinateTransform(SingleCoordinateTransformDTO singleCoordinateTransformDTO) {
        gdal.AllRegister();

        SingleCoordinateTransformBean singleCoordinateTransformBean = new SingleCoordinateTransformBean();
        // 定义输入的坐标系
        SpatialReference srcSRS = new SpatialReference();
        srcSRS.ImportFromEPSG(singleCoordinateTransformDTO.getSrcSRS());
        singleCoordinateTransformBean.setSrcSRS(singleCoordinateTransformDTO.getSrcSRS());

        // 定义输出的坐标系
        SpatialReference dstSRS = new SpatialReference();
        dstSRS.ImportFromEPSG(singleCoordinateTransformDTO.getDstSRS());
        singleCoordinateTransformBean.setDstSRS(singleCoordinateTransformDTO.getDstSRS());

        // 创建坐标转换对象
        CoordinateTransformation transform = new CoordinateTransformation(srcSRS, dstSRS);

        singleCoordinateTransformBean.setOriLon(singleCoordinateTransformDTO.getOriLon());
        singleCoordinateTransformBean.setOriLat(singleCoordinateTransformDTO.getOriLat());
        // 定义Web Mercator坐标点
        double x = singleCoordinateTransformBean.getOriLon();
        double y = singleCoordinateTransformBean.getOriLat();
        // 执行坐标转换
        double[] transformedCoords = new double[3];
        transformedCoords = transform.TransformPoint(y, x);

        singleCoordinateTransformBean.setDstLat(transformedCoords[0]);
        singleCoordinateTransformBean.setDstLon(transformedCoords[1]);

        // 输出转换后的WGS84坐标
        log.info("WGS84 Coordinates: " + transformedCoords[1] + ", " + transformedCoords[0]);

        return singleCoordinateTransformBean;
    }
}
