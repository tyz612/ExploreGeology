package com.geology.service;

import com.geology.domain.DTO.PolygonGeojsonDTO;
import com.geology.domain.DTO.PolygonInfoDTO;
import com.geology.domain.DTO.RectangleDTO;
import com.geology.domain.bean.*;
import com.geology.repository.db.entity.GeologyInfoEntity;

import java.util.List;

public interface GeologyTools {
    GeologyInfoEntity getGeologyInfoByLonLat(double lon, double lat);

    List<GeologyBufferStatisticBean> getGeologyInfoWithinBuffer(double lon, double lat, double rad);

    SingleFileGeologyType getGeologyTypesByRectangle(RectangleDTO rectangleDTO);

    SingleFileGeologyType getGeologyFileByRectangle(RectangleDTO rectangleDTO);

    SingleFileGeologyType getGeologyFileByPolygon(PolygonGeojsonDTO polygonGeoJSON);

    SingleFileGeologyType getGeologyFileByPolygonId(Long polygonId);

    SingleFileGeologyType getGeologyFileByPolygonByName(PolygonGeojsonDTO polygonGeoJSON);

    SingleFileGeologyType getGeologyFileByPolygonByXi(PolygonGeojsonDTO polygonGeoJSON);

    List<GeologyXiBean> getGeologyFileByPolygonallXis(PolygonGeojsonDTO polygonGeoJSON);

    SingleFileGeologyType getGeologyFileByCountyCode(String countyCode);

    SingleFileGeologyType getGeologyFileByPolygonIdandXi(PolygonXiBean polygonXiBean);

    SingleFileGeologyType getGeologyFileByPolygonName(Long polygonId, String keyword, String tong);


}
