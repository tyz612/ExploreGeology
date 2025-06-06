package com.geology.service;

import com.geology.domain.bean.TrackBean;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface TrackService {
    Long saveTrack(String name, String description, String kmlContent);

    List<TrackBean> getTracksByUserId();

    void deleteTrack(Long trackId);

    List<TrackBean> getTracksByName(String trackName);
}
