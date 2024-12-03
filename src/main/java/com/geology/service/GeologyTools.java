package com.geology.service;

import com.geology.repository.db.entity.GeologyInfoEntity;

public interface GeologyTools {
    GeologyInfoEntity getGeologyInfoByLonLat(double lon, double lat);
}
