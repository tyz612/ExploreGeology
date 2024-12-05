package com.geology.controller;

import com.geology.common.utils.CoordinateTransformUtil;
import com.geology.common.utils.BandReductionUtil;
import com.geology.domain.DTO.CoordinateDTO;
import com.geology.domain.DTO.RectangleDTO;
import com.geology.domain.DTO.RectifyDTO;
import com.geology.domain.DTO.SingleCoordinateTransformDTO;
import com.geology.domain.bean.*;
//import geologyTest.domain.bean.*;
import com.geology.repository.db.entity.GeologyInfoEntity;
import com.geology.repository.db.mapper.GetGeologyInfoMapper;
import com.geology.service.GeologyTools;
import com.geology.service.GeologyToolsImpl;
import lombok.extern.slf4j.Slf4j;
import org.gdal.gdal.GCP;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.geology.service.GeoTools;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/geotools")
@Slf4j
public class GeoToolsController {

    @Autowired
    GeoTools geoTools;

    @Autowired
    private CoordinateTransformUtil coordinateTransform;

    @Autowired
    private BandReductionUtil bandReductionUtil;

    @Autowired
    private GeologyTools geologyTools;

    /**
     * 裁切影像接口
     */
    @GetMapping("/TIFInfo")
    public TIFInfoBean TIFInfo(@RequestParam("inputRaster") String inputRaster) {
        if (inputRaster == null)
        {
            throw new RuntimeException("Param is empty.");
        }

        try {
            log.info("clipImage_Param:{}", inputRaster);
            TIFInfoBean tifInfoBean = geoTools.getTIFInfo(inputRaster);
            return tifInfoBean;
        } catch (Exception e) {
//            e.printStackTrace();
            throw e;
        }
    }

    @GetMapping("/test")
    public void test() {
        geoTools.testDriver();
        System.out.println(System.getProperty("java.library.path"));
    }

    @GetMapping("/clip")
    public String clip(@RequestParam("inputRaster") String inputRaster,
                     @RequestParam("inputShp") String inputShp,
                     @RequestParam("outputRaster") String outputRaster) {
        String outputPath = geoTools.clipImage(inputRaster, inputShp, outputRaster);
        return outputPath;
    }


    @PostMapping("/getGCJ02fromWGS84")
    public CoordinateBean getGCJ02fromWGS84(@RequestBody CoordinateDTO coordinateDTO) {
        if (coordinateDTO == null)
        {
            throw new RuntimeException("params empty.");
        }
        CoordinateBean coordinateBean = new CoordinateBean(coordinateDTO.getLon(), coordinateDTO.getLat());
        log.info("input lon is: ".concat(String.valueOf(coordinateBean.getLon()).concat("\t input lat is: ").concat(String.valueOf(coordinateBean.getLat()))));
        CoordinateBean GCJResult = coordinateTransform.wgs84togcj02(coordinateBean);

        return GCJResult;
    }

    @PostMapping("/generateGCPsTxt")
    public RectifyInfoBean generateGCPsTxt(@RequestBody RectifyDTO rectifyDTO) {
        if (rectifyDTO == null)
        {
            throw new RuntimeException("Params empty.");
        }

        RectifyInfoBean rectifyInfoBean = geoTools.generateGCPsFile(rectifyDTO);

        return rectifyInfoBean;
    }


    @PostMapping("/generateGCPs")
    public ArrayList<GCP> generateGCPs(@RequestBody RectifyTIFInfoBean rectifyTIFInfoBean) {
        if (rectifyTIFInfoBean == null)
        {
            throw new RuntimeException("Params empty.");
        }

        log.info("GCP numers are ".concat(String.valueOf(rectifyTIFInfoBean.getGcpNumbers() * rectifyTIFInfoBean.getGcpNumbers())));
        ArrayList<GCP> gcps = geoTools.generateGCPs(rectifyTIFInfoBean);

        return gcps;
    }

    @PostMapping("/rectifyByGCP")
    public String rectifyByGCP(@RequestBody RectifyTIFInfoBean rectifyTIFInfoBean) {
        if (rectifyTIFInfoBean == null)
        {
            throw new RuntimeException("Params empty.");
        }

        log.info("GCP numers are ".concat(String.valueOf(rectifyTIFInfoBean.getGcpNumbers() * rectifyTIFInfoBean.getGcpNumbers())));
        String outPath = geoTools.rectifyByGCP(rectifyTIFInfoBean);

        return outPath;
    }


    @PostMapping("/singleCoordinateTransform")
    public SingleCoordinateTransformBean singleCoordinateTransform(@RequestBody SingleCoordinateTransformDTO singleCoordinateTransformDTO) {
        if (singleCoordinateTransformDTO == null)
        {
            throw new RuntimeException("Params empty.");
        }

        SingleCoordinateTransformBean resultCoordinateInfo = geoTools.singleCoordinateTransform(singleCoordinateTransformDTO);

        return resultCoordinateInfo;
    }

    @GetMapping("/generateRGBImage")
    public void generateRGBImage() {
        bandReductionUtil.generateRGBImage();
    }

    @GetMapping("/getGeologyType")
    public GeologyInfoEntity getGeologyType(@RequestParam("lon") double lon, @RequestParam("lat") double lat) {
        GeologyInfoEntity geologyInfoEntity = geologyTools.getGeologyInfoByLonLat(lon, lat);

        return geologyInfoEntity;
    }

    @GetMapping("/getGeologyInfoWithinBuffer")
    public List<GeologyBufferStatisticBean> getGeologyInfoWithinBuffer(@RequestParam("lon") double lon, @RequestParam("lat") double lat, @RequestParam("rad") double rad) {
        List<GeologyBufferStatisticBean> geologyBufferStatisticBeans = geologyTools.getGeologyInfoWithinBuffer(lon, lat, rad);

        return geologyBufferStatisticBeans;
    }

    @PostMapping("/getGeologyInfoByRectangle")
    public List<GeologyTypeGeometryBean> getGeologyInfoByRectangle(@RequestBody RectangleDTO rectangleDTO) {
        if (rectangleDTO == null)
        {
            throw new RuntimeException("Params empty.");
        }

        List<GeologyTypeGeometryBean> geologyTypeGeometryBeans = geologyTools.getGeologyTypesByRectangle(rectangleDTO);

        return geologyTypeGeometryBeans;
    }


    @PostMapping("/getGeologyFileByRectangle")
    public SingleFileGeologyType getGeologyFileByRectangle(@RequestBody RectangleDTO rectangleDTO) {
        if (rectangleDTO == null)
        {
            throw new RuntimeException("Params empty.");
        }

        SingleFileGeologyType singleFileGeologyType = geologyTools.getGeologyFileByRectangle(rectangleDTO);

        return singleFileGeologyType;
    }

}
