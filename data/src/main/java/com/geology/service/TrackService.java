package com.geology.service;

import com.geology.domain.DTO.RectangleDTO;
import com.geology.domain.bean.SingleFileGeologyType;
import com.geology.domain.bean.TrackBean;
import com.geology.domain.bean.TrackGeomBean;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface TrackService {
    Long saveTrack(String name, String description, String kmlContent);

    List<TrackBean> getTracksByUserId();

    void deleteTrack(Long trackId);

    List<TrackBean> getTracksByName(String trackName);

    TrackGeomBean getTrackGeomById(Long trackId);

    SingleFileGeologyType getGeologyFileByTrackBuffer(Long trackId, Integer buffer);

    SingleFileGeologyType getGeologyFileByTrackBufferName(Long trackId, Integer buffer, String keyWords, String tong);

    String getBufferGeojson(Long trackId, Integer buffer);

    String saveSharedTrack(String fromUserId, String dataId, String receiveUserId);
}
