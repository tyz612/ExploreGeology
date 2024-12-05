package com.geology.service;

import com.geology.domain.bean.GeologyBufferStatisticBean;
import com.geology.repository.db.entity.GeologyInfoEntity;
import com.geology.repository.db.mapper.GetGeologyInfoMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class GeologyToolsImpl implements GeologyTools{

    @Autowired
    private GetGeologyInfoMapper getGeologyInfoMapper;

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
}
