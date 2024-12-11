package com.geology.repository.db.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.geology.domain.bean.GeologyBufferStatisticBean;
import com.geology.domain.bean.GeologyTypeGeometryBean;
import com.geology.domain.bean.SingleFileGeologyType;
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

    @Select("SELECT\n" +
            "t.gid,\n" +
            "t.qduecd,\n" +
            "t.qduecc,\n" +
            "t.yshb,\n" +
            "t.mdaec,\n" +
            "ST_Area(ST_Intersection(t.geom, r.rect))*10000 AS intersection_area,\n" +
            "json_build_object(\n" +
            "    'type', 'Feature',\n" +
            "            'geometry', ST_AsGeoJSON(ST_Intersection(t.geom, r.rect))::json,\n" +
            "        'properties', json_build_object(\n" +
            "      'gid', t.gid,\n" +
            "      'qduecd', t.qduecd,\n" +
            "      'qduecc', t.qduecc,\n" +
            "      'yshb', t.yshb,\n" +
            "      'mdaec', t.mdaec\n" +
            ")\n" +
            "  ) ::text AS geojson_feature\n" +
            "FROM\n" +
            "merge t,\n" +
            "  (SELECT ST_MakeEnvelope(#{minLon}, #{minLat}, #{maxLon}, #{maxLat}, 4326) AS rect) AS r\n" +
            "WHERE\n" +
            "ST_Intersects(t.geom, r.rect);")
    List<GeologyTypeGeometryBean> getGeologyTypesByRectangle(@Param("minLon") double minLon,
                                                             @Param("minLat") double minLat,
                                                             @Param("maxLon") double maxLon,
                                                             @Param("maxLat") double maxLat);


    @Select("SELECT\n" +
            "    json_build_object(\n" +
            "            'type', 'FeatureCollection',\n" +
            "            'features', json_agg(json_build_object(\n" +
            "            'type', 'Feature',\n" +
            "            'geometry', ST_AsGeoJSON(ST_Intersection(t.geom, r.rect))::json,\n" +
            "            'properties', json_build_object(\n" +
            "                    'gid', t.gid,\n" +
            "                    'qduecd', t.qduecd,\n" +
            "                    'qduecc', t.qduecc,\n" +
            "                    'yshb', t.yshb,\n" +
            "                    'mdaec', t.mdaec\n" +
            "                          )\n" +
            "                                 ))\n" +
            "    )::text AS geojson_featurecollection\n" +
            "FROM\n" +
            "    merge t,\n" +
            "    (SELECT ST_MakeEnvelope(#{minLon}, #{minLat}, #{maxLon}, #{maxLat}, 4326) AS rect) AS r\n" +
            "WHERE\n" +
            "    ST_Intersects(t.geom, r.rect);")
    SingleFileGeologyType getGeologyFileByRectangle(@Param("minLon") double minLon,
                                                    @Param("minLat") double minLat,
                                                    @Param("maxLon") double maxLon,
                                                    @Param("maxLat") double maxLat);


    @Select("SELECT\n" +
            "    json_build_object(\n" +
            "            'type', 'FeatureCollection',\n" +
            "            'features', json_agg(json_build_object(\n" +
            "            'type', 'Feature',\n" +
            "            'geometry', ST_AsGeoJSON(ST_Intersection(t.geom, c.geom))::json,\n" +
            "            'properties', json_build_object(\n" +
            "                    'gid', t.gid,\n" +
            "                    'qduecd', t.qduecd\n" +
            "                -- 在这里添加更多的属性字段\n" +
            "                          )\n" +
            "                                 ))\n" +
            "    )::json AS geojson_featurecollection\n" +
            "FROM\n" +
            "    merge t,\n" +
            "    county c\n" +
            "WHERE\n" +
            "    c.adcode = #{countyCode} and\n" +
            "    ST_Intersects(t.geom, c.geom);")
    SingleFileGeologyType getGeologyFileByCountyCode(@Param("countyCode") Long countyCode);

}


