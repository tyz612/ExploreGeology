package com.geology.service;

import com.geology.domain.DTO.PolygonGeojsonDTO;
import com.geology.domain.DTO.RectangleDTO;
import com.geology.domain.bean.EnvolopeBean;
import com.geology.domain.bean.GeologyBufferStatisticBean;
import com.geology.domain.bean.GeologyTypeGeometryBean;
import com.geology.domain.bean.SingleFileGeologyType;
import com.geology.repository.db.entity.GeologyInfoEntity;

import java.util.List;

public interface GeologyTools {
    GeologyInfoEntity getGeologyInfoByLonLat(double lon, double lat);

    List<GeologyBufferStatisticBean> getGeologyInfoWithinBuffer(double lon, double lat, double rad);

    SingleFileGeologyType getGeologyTypesByRectangle(RectangleDTO rectangleDTO);

    SingleFileGeologyType getGeologyFileByRectangle(RectangleDTO rectangleDTO);

    SingleFileGeologyType getGeologyFileByPolygon(PolygonGeojsonDTO polygonGeoJSON);

    SingleFileGeologyType getGeologyFileByPolygonByName(PolygonGeojsonDTO polygonGeoJSON);

    SingleFileGeologyType getGeologyFileByCountyCode(String countyCode);

}
