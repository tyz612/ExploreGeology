package com.geology.service;

import com.geology.common.utils.DownloadFileUtil;
import com.geology.domain.DTO.PolygonGeojsonDTO;
import com.geology.domain.DTO.RectangleDTO;
import com.geology.domain.bean.GeologyBufferStatisticBean;
import com.geology.domain.bean.GeologyXiBean;
import com.geology.domain.bean.PolygonXiBean;
import com.geology.domain.bean.SingleFileGeologyType;
import com.geology.repository.db.entity.GeologyInfoEntity;
import com.geology.repository.db.mapper.GetGeologyInfoMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

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
    public SingleFileGeologyType getGeologyFileByPolygon(PolygonGeojsonDTO polygonGeoJSON) {
        SingleFileGeologyType singleFileGeologyType = getGeologyInfoMapper.getGeologyFileByPolygon(polygonGeoJSON.getPolygonGeoJSON());

        return singleFileGeologyType;
    }


    @Override
    public SingleFileGeologyType getGeologyFileByPolygonByName(PolygonGeojsonDTO polygonGeoJSON) {
        SingleFileGeologyType singleFileGeologyType = getGeologyInfoMapper.getGeologyFileByPolygonByName(polygonGeoJSON.getPolygonGeoJSON(), polygonGeoJSON.getKeywords(), polygonGeoJSON.getTong());

        return singleFileGeologyType;
    }


    @Override
    public SingleFileGeologyType getGeologyFileByPolygonByXi(PolygonGeojsonDTO polygonGeoJSON) {
        SingleFileGeologyType singleFileGeologyType = getGeologyInfoMapper.getGeologyFileByPolygonByXi(polygonGeoJSON.getPolygonGeoJSON(), polygonGeoJSON.getXi());

        return singleFileGeologyType;
    }


    @Override
    public List<GeologyXiBean> getGeologyFileByPolygonallXis(PolygonGeojsonDTO polygonGeoJSON) {
        List<GeologyXiBean> geologyXiBeans = getGeologyInfoMapper.getGeologyFileByPolygonallXis(polygonGeoJSON.getPolygonGeoJSON());

        return geologyXiBeans;
    }

    @Override
    public SingleFileGeologyType getGeologyFileByCountyCode(String countyCode) {
        SingleFileGeologyType singleFileGeologyType = getGeologyInfoMapper.getGeologyFileByCountyCode(Long.parseLong(countyCode));
        downloadFileUtil.writeGeoJsonToFile(singleFileGeologyType.getGeojsonFeaturecollection().toString(), geoJsonFilePath.concat(countyCode).concat(".json"));

        return singleFileGeologyType;
    }


    @Override
    public SingleFileGeologyType getGeologyFileByPolygonId(Long polygonId) {
        SingleFileGeologyType singleFileGeologyType = getGeologyInfoMapper.getGeologyFileByPolygonId(polygonId);

        return singleFileGeologyType;
    }

    @Override
    public SingleFileGeologyType getGeologyFileByPolygonIdandXi(PolygonXiBean polygonXiBean) {
        SingleFileGeologyType singleFileGeologyType = getGeologyInfoMapper.getGeologyFileByPolygonIdandXi(polygonXiBean.getPolygonId(), polygonXiBean.getXi());

        return singleFileGeologyType;
    }


    @Override
    public SingleFileGeologyType getGeologyFileByPolygonIdandAge(PolygonGeojsonDTO polygonGeojsonDTO) {
        SingleFileGeologyType singleFileGeologyType = getGeologyInfoMapper.getGeologyFileByPolygonIdandAge(polygonGeojsonDTO.getPolygonGeoJSON(), polygonGeojsonDTO.getAge());

        return singleFileGeologyType;
    }

    @Override
    public SingleFileGeologyType getGeologyFileBySavePolygonIdandXi(PolygonXiBean polygonXiBean) {
        SingleFileGeologyType singleFileGeologyType = getGeologyInfoMapper.getGeologyFileBySavePolygonIdandAge(polygonXiBean.getPolygonId(), polygonXiBean.getAge());

        return singleFileGeologyType;
    }


    @Override
    public SingleFileGeologyType getGeologyFileByPolygonName(Long polygonId, String keyword, String tong) {
        SingleFileGeologyType singleFileGeologyType = getGeologyInfoMapper.getGeologyFileByPolygonName(polygonId, keyword, tong);

        return singleFileGeologyType;
    }

}
