package com.geology.repository.db.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.geology.domain.bean.GeologyBufferStatisticBean;
import com.geology.repository.db.entity.GeologyInfoEntity;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface GetGeologyInfoMapper extends BaseMapper<GeologyInfoEntity> {

    @Select("select gid, QDUECD, QDUECC, YSHB, YSC, YSJB, YSBBAV, MDAEC from merge where gid = #{gid}")
    GeologyInfoEntity getGeologyInfoById(@Param("gid") Long gid);

    @Select("SELECT gid\n" +
            "FROM public.merge\n" +
            "WHERE ST_Within(\n" +
            "              ST_SetSRID(ST_MakePoint(#{lon}, #{lat}), 4326), -- 输入的经纬度点，假设使用WGS 84坐标系（SRID 4326）\n" +
            "              geom -- 面要素的几何列\n" +
            "      );")
    Long getGidByLonLat(@Param("lon") double lon, @Param("lat") double lat);

    @Select("SELECT\n" +
            "    gid,\n" +
            "    qduecd,\n" +
            "    qduecc,\n" +
            "    yshb,\n" +
            "    mdaec,\n" +
            "    ST_Area(ST_Intersection(a.buffergeom, b.geom))*10000 AS intersection_area\n" +
            "FROM\n" +
            "    (SELECT ST_Buffer(ST_SetSRID(ST_MakePoint(#{lon}, #{lat}), 4326), #{rad}) AS buffergeom) AS a,\n" +
            "    merge b\n" +
            "WHERE\n" +
            "    ST_Intersects(a.buffergeom, b.geom);")
    List<GeologyBufferStatisticBean> getGeologyInfoWithinBuffer(@Param("lon") double lon, @Param("lat") double lat, @Param("rad") double rad);
}
