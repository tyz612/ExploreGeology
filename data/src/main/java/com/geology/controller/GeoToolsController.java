package com.geology.controller;

import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.extension.api.ApiController;
import com.geology.common.ApiResponse;
import com.geology.common.jwt.AuthStorage;
import com.geology.common.jwt.JwtUser;
import com.geology.common.utils.CoordinateTransformUtil;
import com.geology.common.utils.BandReductionUtil;
import com.geology.domain.DTO.*;
import com.geology.domain.bean.*;
//import geologyTest.domain.bean.*;
import com.geology.repository.db.entity.GeologyInfoEntity;
import com.geology.repository.db.mapper.GetGeologyInfoMapper;
import com.geology.service.*;
import lombok.extern.slf4j.Slf4j;
import org.gdal.gdal.GCP;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


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

    @Autowired
    private PoiSearch poiSearch;

    @Autowired
    private GeoJsonService geoJsonService;

    /**
     * 裁切影像接口
     */
    @CrossOrigin(origins = "*")
    @GetMapping("/TIFInfo")
    public TIFInfoBean TIFInfo(@RequestParam("inputRaster") String inputRaster) {
        if (inputRaster == null) {
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

    @CrossOrigin(origins = "*")
    @GetMapping("/test")
    public void test() {
        geoTools.testDriver();
        System.out.println(System.getProperty("java.library.path"));
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/clip")
    public String clip(@RequestParam("inputRaster") String inputRaster,
                       @RequestParam("inputShp") String inputShp,
                       @RequestParam("outputRaster") String outputRaster) {
        String outputPath = geoTools.clipImage(inputRaster, inputShp, outputRaster);
        return outputPath;
    }


    @CrossOrigin(origins = "*")
    @PostMapping("/getGCJ02fromWGS84")
    public CoordinateBean getGCJ02fromWGS84(@RequestBody CoordinateDTO coordinateDTO) {
        if (coordinateDTO == null) {
            throw new RuntimeException("params empty.");
        }
        CoordinateBean coordinateBean = new CoordinateBean(coordinateDTO.getLon(), coordinateDTO.getLat());
        log.info("input lon is: ".concat(String.valueOf(coordinateBean.getLon()).concat("\t input lat is: ").concat(String.valueOf(coordinateBean.getLat()))));
        CoordinateBean GCJResult = coordinateTransform.wgs84togcj02(coordinateBean);

        return GCJResult;
    }

    @CrossOrigin(origins = "*")
    @PostMapping("/generateGCPsTxt")
    public RectifyInfoBean generateGCPsTxt(@RequestBody RectifyDTO rectifyDTO) {
        if (rectifyDTO == null) {
            throw new RuntimeException("Params empty.");
        }

        RectifyInfoBean rectifyInfoBean = geoTools.generateGCPsFile(rectifyDTO);

        return rectifyInfoBean;
    }


    @CrossOrigin(origins = "*")
    @PostMapping("/generateGCPs")
    public ArrayList<GCP> generateGCPs(@RequestBody RectifyTIFInfoBean rectifyTIFInfoBean) {
        if (rectifyTIFInfoBean == null) {
            throw new RuntimeException("Params empty.");
        }

        log.info("GCP numers are ".concat(String.valueOf(rectifyTIFInfoBean.getGcpNumbers() * rectifyTIFInfoBean.getGcpNumbers())));
        ArrayList<GCP> gcps = geoTools.generateGCPs(rectifyTIFInfoBean);

        return gcps;
    }

    @CrossOrigin(origins = "*")
    @PostMapping("/rectifyByGCP")
    public ApiResponse<String> rectifyByGCP(@RequestBody RectifyTIFInfoBean rectifyTIFInfoBean) {
        if (rectifyTIFInfoBean == null) {
            throw new RuntimeException("Params empty.");
        }

        log.info("GCP numers are ".concat(String.valueOf(rectifyTIFInfoBean.getGcpNumbers() * rectifyTIFInfoBean.getGcpNumbers())));
        String outPath = geoTools.rectifyByGCP(rectifyTIFInfoBean);

        return ApiResponse.success(outPath);
    }


    @CrossOrigin(origins = "*")
    @PostMapping("/singleCoordinateTransform")
    public SingleCoordinateTransformBean singleCoordinateTransform(@RequestBody SingleCoordinateTransformDTO singleCoordinateTransformDTO) {
        if (singleCoordinateTransformDTO == null) {
            throw new RuntimeException("Params empty.");
        }

        SingleCoordinateTransformBean resultCoordinateInfo = geoTools.singleCoordinateTransform(singleCoordinateTransformDTO);

        return resultCoordinateInfo;
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/generateRGBImage")
    public void generateRGBImage() {
        bandReductionUtil.generateRGBImage();
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/getGeologyType")
    public ApiResponse<GeologyInfoEntity> getGeologyType(@RequestParam("lon") double lon, @RequestParam("lat") double lat) {
        GeologyInfoEntity geologyInfoEntity = geologyTools.getGeologyInfoByLonLat(lon, lat);

        return ApiResponse.success(geologyInfoEntity);
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/getGeologyInfoWithinBuffer")
    public ApiResponse<List> getGeologyInfoWithinBuffer(@RequestParam("lon") double lon, @RequestParam("lat") double lat, @RequestParam("rad") double rad) {
        List<GeologyBufferStatisticBean> geologyBufferStatisticBeans = geologyTools.getGeologyInfoWithinBuffer(lon, lat, rad);

        return ApiResponse.success(geologyBufferStatisticBeans);
    }

    @PostMapping("/getGeologyInfoByRectangle")
    public ApiResponse<SingleFileGeologyType> getGeologyInfoByRectangle(@RequestBody RectangleDTO rectangleDTO) {
        if (rectangleDTO == null) {
            throw new RuntimeException("Params empty.");
        }

        SingleFileGeologyType geologyTypeGeometryBeans = geologyTools.getGeologyTypesByRectangle(rectangleDTO);

        return ApiResponse.success(geologyTypeGeometryBeans);
    }

    @CrossOrigin(origins = "*")
    @PostMapping("/getGeologyFileByRectangle")
    public ApiResponse<SingleFileGeologyType> getGeologyFileByRectangle(@RequestBody RectangleDTO rectangleDTO) {
        if (rectangleDTO == null) {
            throw new RuntimeException("Params empty.");
        }

        SingleFileGeologyType singleFileGeologyType = geologyTools.getGeologyFileByRectangle(rectangleDTO);

        return ApiResponse.success(singleFileGeologyType);
    }


    @CrossOrigin(origins = "*")
    @PostMapping("/getGeologyFileByPolygon")
    public ApiResponse<SingleFileGeologyType> getGeologyFileByPolygon(@RequestBody PolygonGeojsonDTO polygonGeojsonDTO) {
        if (polygonGeojsonDTO == null) {
            throw new RuntimeException("Params empty.");
        }

        SingleFileGeologyType singleFileGeologyType = geologyTools.getGeologyFileByPolygon(polygonGeojsonDTO);

        return ApiResponse.success(singleFileGeologyType);
    }


    @CrossOrigin(origins = "*")
    @PostMapping("/getGeologyFileByPolygonName")
    public ApiResponse<SingleFileGeologyType> getGeologyFileByPolygonName(@RequestBody PolygonGeojsonDTO polygonGeojsonDTO) {
        if (polygonGeojsonDTO == null) {
            throw new RuntimeException("Params empty.");
        }

        SingleFileGeologyType singleFileGeologyType = geologyTools.getGeologyFileByPolygonByName(polygonGeojsonDTO);

        return ApiResponse.success(singleFileGeologyType);
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/getGeologyFileByCountyCode")
    public ApiResponse<SingleFileGeologyType> getGeologyFileByCountyCode(@RequestParam("countyCode") String countyCode) {
        if (countyCode == null) {
            throw new RuntimeException("Params empty.");
        }

        SingleFileGeologyType singleFileGeologyType = geologyTools.getGeologyFileByCountyCode(countyCode);

        return ApiResponse.success(singleFileGeologyType);
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/CurrentUser")
    public ApiResponse<Long> getInfo() {
        // 从全局环境中获取用户id
        JwtUser user = AuthStorage.getUser();
        long userId = Long.parseLong(user.getUserId());
        // TODO 校验用户是否合法

        return ApiResponse.success(userId);
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/searchPoi")
    public ApiResponse<Map<String, Object>> searchPoi(@RequestParam(value = "keyWords") String keyWords,
                                                      @RequestParam(value = "key") String key,
                                                      @RequestParam(value = "address", required = false) String address,
                                                      @RequestParam(defaultValue = "1") int currentPage,
                                                      @RequestParam(defaultValue = "5") int pageSize) {
        if (keyWords == null | key == null) {
            throw new RuntimeException("Params empty.");
        }

        List<JSONObject> poiResponse = poiSearch.searchPoi(keyWords, key, address, currentPage, pageSize);

        Map<String, Object> map = new HashMap<>();
        map.put("data", poiResponse);
        map.put("currentPage", currentPage);
        map.put("pageSize", pageSize);

        return ApiResponse.success(map);
    }


    @CrossOrigin(origins = "https://geologymine.fun")
    @GetMapping("/getGeologyImage")
    public void getGeologyImage(@RequestParam("fileName") String filename, HttpServletResponse response) throws IOException {
        // 构建图片的完整路径
        String imagePath = "/data/gpics/" + filename;

        // 检查文件是否存在
        File file = new File(imagePath);
        if (!file.exists()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "图片未找到");
            return;
        }

        // 设置响应头
        response.setContentType("image/jpeg"); // 根据图片类型设置正确的 MIME 类型
        response.setContentLengthLong(file.length());

        // 写入文件内容
        try (InputStream inputStream = new FileInputStream(file);
             OutputStream outputStream = response.getOutputStream()) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        }
    }

    @CrossOrigin(origins = "https://geologymine.fun")
    @GetMapping("/getGeojson")
    public ApiResponse<String> getGeoJson() {
        // 读取GeoJSON文件内容
        String geoJsonContent = geoJsonService.readGeoJsonFile("/data/gCurrent.geojson");
        return ApiResponse.success(geoJsonContent);
    }

}
