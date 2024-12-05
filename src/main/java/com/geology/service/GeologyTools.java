package com.geology.service;

import com.geology.domain.bean.GeologyBufferStatisticBean;
import com.geology.repository.db.entity.GeologyInfoEntity;

import java.util.List;

public interface GeologyTools {
    GeologyInfoEntity getGeologyInfoByLonLat(double lon, double lat);

    List<GeologyBufferStatisticBean> getGeologyInfoWithinBuffer(double lon, double lat, double rad);
}
