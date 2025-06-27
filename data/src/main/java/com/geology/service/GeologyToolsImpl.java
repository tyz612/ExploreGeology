package com.geology.service;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.geology.common.utils.DownloadFileUtil;
import com.geology.common.utils.PaginationUtil;
import com.geology.domain.DTO.RectangleDTO;
import com.geology.domain.bean.EnvolopeBean;
import com.geology.domain.bean.GeologyBufferStatisticBean;
import com.geology.domain.bean.GeologyTypeGeometryBean;
import com.geology.domain.bean.SingleFileGeologyType;
import com.geology.repository.db.entity.GeologyInfoEntity;
import com.geology.repository.db.mapper.GetGeologyInfoMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class GeologyToolsImpl implements GeologyTools {

    @Autowired
    private GetGeologyInfoMapper getGeologyInfoMapper;

    @Autowired
    private DownloadFileUtil downloadFileUtil;

    @Value("${app.geojson.file-path}")
    private String geoJsonFilePath;

    @Autowired
    private RestTemplate restTemplate;

    private static final String POI_URL = "https://restapi.amap.com/v3/place/text?keywords={keywords}&key={key}";

    @Override
    public GeologyInfoEntity getGeologyInfoByLonLat(double lon, double lat) {
        Long gid = getGeologyInfoMapper.getGidByLonLat(lon, lat);
        GeologyInfoEntity geologyInfoEntity = getGeologyInfoMapper.getGeologyInfoById(gid);

        return geologyInfoEntity;
    }


    @Override
    public List<GeologyBufferStatisticBean> getGeologyInfoWithinBuffer(double lon, double lat, double rad) {
        List<GeologyBufferStatisticBean> geologyBufferStatisticBean = getGeologyInfoMapper.getGeologyInfoWithinBuffer(lon, lat, rad);

        return geologyBufferStatisticBean;
    }


    @Override
    public SingleFileGeologyType getGeologyTypesByRectangle(RectangleDTO rectangleDTO) {
        SingleFileGeologyType singleFileGeologyType = getGeologyInfoMapper.getGeologyTypesByRectangle(rectangleDTO.getMinLon(), rectangleDTO.getMinLat(), rectangleDTO.getMaxLon(), rectangleDTO.getMaxLat(), rectangleDTO.getKeywords(), rectangleDTO.getTong());


        return singleFileGeologyType;
    }

    @Override
    public SingleFileGeologyType getGeologyFileByRectangle(RectangleDTO rectangleDTO) {
        SingleFileGeologyType singleFileGeologyType = getGeologyInfoMapper.getGeologyFileByRectangle(rectangleDTO.getMinLon(), rectangleDTO.getMinLat(), rectangleDTO.getMaxLon(), rectangleDTO.getMaxLat());


        return singleFileGeologyType;
    }

    @Override
    public SingleFileGeologyType getGeologyFileByCountyCode(String countyCode) {
        SingleFileGeologyType singleFileGeologyType = getGeologyInfoMapper.getGeologyFileByCountyCode(Long.parseLong(countyCode));
        downloadFileUtil.writeGeoJsonToFile(singleFileGeologyType.getGeojsonFeaturecollection().toString(), geoJsonFilePath.concat(countyCode).concat(".json"));

        return singleFileGeologyType;
    }

}
