package com.geology.repository.db.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.geology.repository.db.entity.GeologyInfoEntity;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

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
}
