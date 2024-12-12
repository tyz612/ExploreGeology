package com.geology.service;

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

    List<GeologyTypeGeometryBean> getGeologyTypesByRectangle(RectangleDTO rectangleDTO);

    SingleFileGeologyType getGeologyFileByRectangle(RectangleDTO rectangleDTO);

    SingleFileGeologyType getGeologyFileByCountyCode(String countyCode);

}
