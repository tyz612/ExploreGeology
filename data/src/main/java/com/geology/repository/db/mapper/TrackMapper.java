package com.geology.repository.db.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.geology.domain.DTO.PoiLocationDTO;
import com.geology.domain.bean.PoiLocationBean;
import com.geology.domain.bean.SingleFileGeologyType;
import com.geology.domain.bean.TrackBean;
import com.geology.domain.bean.UserPhotoBean;
import com.geology.repository.db.entity.GeologyInfoEntity;
import com.geology.repository.db.entity.TrackEntity;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

public interface TrackMapper extends BaseMapper<TrackEntity> {
    @Insert("insert into tracks (id, user_id, description, start_time, end_time, create_time, name, status, geom, distance, min_altitude, max_altitude) " +
            "values (#{id}, #{userId}, #{description}, #{startTime}, #{endTime}, #{createTime}, #{name}, 1, ST_GeomFromText(#{geom}), #{distance}, #{minAltitude}, #{maxAltitude})")
    void insertTrack(TrackEntity trackEntity);

    @Select("SELECT t.id as id, t.user_id as userId, t.description as description, t.name as name, t.create_time as createTime,\n"+
            " ST_AsGeoJSON(t.geom) as geom, t.start_time as startTime, t.end_time as endTime, t.status as status, t.distance as distance, t.min_altitude as minAltitude, t.max_altitude as maxAltitude FROM tracks t  WHERE t.user_id = #{userId} and t.status = 1")
    List<TrackBean> getTracksByUserId(@Param("userId") Long userId);

    @Select("SELECT t.id as id, t.user_id as userId, t.description as description, t.name as name, t.create_time as createTime,\n" +
            " ST_AsGeoJSON(t.geom) as geom, t.start_time as startTime, t.end_time as endTime, t.status as status, t.distance as distance, t.min_altitude as minAltitude, t.max_altitude as maxAltitude FROM tracks t  WHERE t.name like CONCAT('%', #{trackName}, '%') and t.user_id = #{userId} and t.status = 1")
    List<TrackBean> getTrackByName(@Param("trackName") String trackName, @Param("userId") Long userId);

    @Update("UPDATE tracks SET status = 0 WHERE id = #{trackId};")
    void deleteTrack(@Param("trackId") Long trackId);

    @Select("SELECT\n" +
            "    json_build_object(\n" +
            "            'type', 'FeatureCollection',\n" +
            "            'features', COALESCE(\n" +
            "                    json_agg(\n" +
            "                            json_build_object(\n" +
            "                                    'type', 'Feature',\n" +
            "                                    'geometry', ST_AsGeoJSON(\n" +
            "                                            ST_Multi(ST_Intersection(g.geom, buffer_geom))\n" +
            "                                                )::json,\n" +
            "                                    'properties', json_build_object(\n" +
            "                                            'gid', g.gid,\n" +
            "                                            'qduecd', g.qduecd,\n" +
            "                                            'qduecc', g.qduecc,\n" +
            "                                            'yshb', g.yshb,\n" +
            "                                            'mdaec', g.mdaec\n" +
            "                                                  )\n" +
            "                            )\n" +
            "                    ),\n" +
            "                    '[]'::json  -- 处理无结果的情况\n" +
            "                        )\n" +
            "    )::text AS geojson_featurecollection\n" +
            "FROM (\n" +
            "         SELECT\n" +
            "             ST_Buffer(t.geom::geography, #{buffer})::geometry AS buffer_geom\n" +
            "         FROM tracks t\n" +
            "         WHERE t.id = #{trackId}\n" +
            "     ) AS buffer\n" +
            "         JOIN merge g ON ST_Intersects(buffer.buffer_geom, g.geom);")
    SingleFileGeologyType getGeologyFileByTrackBuffer(@Param("trackId") Long trackId,
                                                      @Param("buffer") Integer buffer);

}
