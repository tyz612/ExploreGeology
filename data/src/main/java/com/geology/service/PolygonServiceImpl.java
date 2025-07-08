package com.geology.service;

import com.geology.common.jwt.AuthStorage;
import com.geology.common.jwt.JwtUser;
import com.geology.common.utils.BboxWktUtil;
import com.geology.common.utils.GeoJsonToWktUtil;
import com.geology.common.utils.GeologyDistributedIdGenerator;
import com.geology.common.utils.Shape2PostgisUtil;
import com.geology.domain.DTO.DrawingPolygonDTO;
import com.geology.domain.DTO.PolygonGeojsonDTO;
import com.geology.domain.DTO.RectangleDTO;
import com.geology.domain.bean.PolygonBean;
import com.geology.repository.db.entity.PolygonEntity;
import com.geology.repository.db.mapper.GetGeologyInfoMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class PolygonServiceImpl implements PolygonService {

    @Autowired
    private Shape2PostgisUtil shape2PostgisUtil;

    @Autowired
    private GetGeologyInfoMapper getGeologyInfoMapper;

    @Autowired
    private GeoJsonToWktUtil geoJsonToWktUtil;

    @Autowired
    private BboxWktUtil bboxWktUtil;


    @Override
    public Long savePolygon(String name, String description, MultipartFile file) throws IOException {
        List<String> shpwkt = shape2PostgisUtil.parsePolygonsFromZip(file, 4326);
        if (shpwkt.isEmpty()) {
            throw new RuntimeException("未解析到任何多边形");
        }

        JwtUser user = AuthStorage.getUser();
        long userId = Long.parseLong(user.getUserId());
        Date now = new Date();

        // 生成主ID作为组ID
        Long groupId = GeologyDistributedIdGenerator.getInstance().nextId();

        for (int i = 0; i < shpwkt.size(); i++) {
            PolygonEntity polygonEntity = new PolygonEntity();

            // 为每个多边形生成独立ID
            Long polygonId = GeologyDistributedIdGenerator.getInstance().nextId();
            polygonEntity.setId(polygonId);
            polygonEntity.setGroupid(groupId); // 设置组ID
            polygonEntity.setUserId(userId);
            polygonEntity.setCreateTime(now);

            // 添加序号到名称
            polygonEntity.setPolygonName(name);
            polygonEntity.setDescription(description);
            polygonEntity.setGeom(shpwkt.get(i)); // 设置当前多边形WKT

            getGeologyInfoMapper.insertPolygon(polygonEntity);
        }

        return groupId; // 返回组ID用于关联所有多边形
    }

    @Override
    public List<PolygonBean> getPolygonsByUserId() {
        JwtUser user = AuthStorage.getUser();
        long userId = Long.parseLong(user.getUserId());

        List<PolygonBean> polygonBeans = getGeologyInfoMapper.getPolygonsByUserId(userId);

        return polygonBeans;
    }


    @Override
    public void deleteTrack(Long polygonId) {
        JwtUser user = AuthStorage.getUser();
        long userId = Long.parseLong(user.getUserId());

        getGeologyInfoMapper.deletePolygon(polygonId);
    }

    @Override
    public List<PolygonBean> getPolygonsByGroupId(Long polygonId) {
        JwtUser user = AuthStorage.getUser();
        long userId = Long.parseLong(user.getUserId());

        List<PolygonBean> polygonBeans = getGeologyInfoMapper.getPolygonsByGroupId(polygonId);

        return polygonBeans;
    }

    @Override
    public Long saveDrawingPolygon(DrawingPolygonDTO drawingPolygonDTO) {
        PolygonEntity polygonEntity = new PolygonEntity();

        // 为每个多边形生成独立ID
        Long polygonId = GeologyDistributedIdGenerator.getInstance().nextId();
        // 生成主ID作为组ID
        Long groupId = GeologyDistributedIdGenerator.getInstance().nextId();

        JwtUser user = AuthStorage.getUser();
        long userId = Long.parseLong(user.getUserId());
        Date now = new Date();

        polygonEntity.setId(polygonId);
        polygonEntity.setGroupid(groupId); // 设置组ID
        polygonEntity.setUserId(userId);
        polygonEntity.setCreateTime(now);

        // 添加序号到名称
        polygonEntity.setPolygonName(drawingPolygonDTO.getName());
        polygonEntity.setDescription(drawingPolygonDTO.getDescription());

        // 转换多边形
        String wktPolygon = geoJsonToWktUtil.convert(drawingPolygonDTO.getPolygonGeoJSON());
        polygonEntity.setGeom(wktPolygon); // 设置当前多边形WKT

        getGeologyInfoMapper.insertPolygon(polygonEntity);


        return groupId;
    }

    @Override
    public Long saveDrawingRectangle(DrawingPolygonDTO drawingPolygonDTO) {
        PolygonEntity polygonEntity = new PolygonEntity();

        // 为每个多边形生成独立ID
        Long polygonId = GeologyDistributedIdGenerator.getInstance().nextId();
        // 生成主ID作为组ID
        Long groupId = GeologyDistributedIdGenerator.getInstance().nextId();

        JwtUser user = AuthStorage.getUser();
        long userId = Long.parseLong(user.getUserId());
        Date now = new Date();

        polygonEntity.setId(polygonId);
        polygonEntity.setGroupid(groupId); // 设置组ID
        polygonEntity.setUserId(userId);
        polygonEntity.setCreateTime(now);

        // 添加序号到名称
        polygonEntity.setPolygonName(drawingPolygonDTO.getName());
        polygonEntity.setDescription(drawingPolygonDTO.getDescription());

        String wkt = bboxWktUtil.convertToWktPolygon(drawingPolygonDTO.getMinLon(), drawingPolygonDTO.getMaxLon(), drawingPolygonDTO.getMinLat(), drawingPolygonDTO.getMaxLat());
        polygonEntity.setGeom(wkt);
        getGeologyInfoMapper.insertPolygon(polygonEntity);

        return groupId;
    }

}
