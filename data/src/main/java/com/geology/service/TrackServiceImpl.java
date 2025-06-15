package com.geology.service;

import com.geology.common.jwt.AuthStorage;
import com.geology.common.jwt.JwtUser;
import com.geology.common.utils.GeologyDistributedIdGenerator;
import com.geology.common.utils.KmlParseUtil;
import com.geology.domain.bean.PoiLocationBean;
import com.geology.domain.bean.SingleFileGeologyType;
import com.geology.domain.bean.TrackBean;
import com.geology.repository.db.entity.TrackEntity;
import com.geology.repository.db.mapper.TrackMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class TrackServiceImpl implements TrackService {

    @Autowired
    private KmlParseUtil kmlParseUtil;

    @Autowired
    private TrackMapper trackMapper;

    @Override
    public Long saveTrack(String name, String description, String kmlContent) {
        TrackEntity trackEntity = kmlParseUtil.parseKml(kmlContent);

        Long trackId = GeologyDistributedIdGenerator.getInstance().nextId();
        trackEntity.setId(trackId);

        JwtUser user = AuthStorage.getUser();
        long userId = Long.parseLong(user.getUserId());
        trackEntity.setUserId(userId);

        Date now = new Date();
        trackEntity.setCreateTime(now);

        trackEntity.setName(name);
        trackEntity.setDescription(description);

        trackMapper.insertTrack(trackEntity);

        return trackId;
    }

    @Override
    public List<TrackBean> getTracksByUserId() {
        JwtUser user = AuthStorage.getUser();
        long userId = Long.parseLong(user.getUserId());

        List<TrackBean> trackBeans = trackMapper.getTracksByUserId(userId);

        return trackBeans;
    }

    @Override
    public void deleteTrack(Long trackId) {
        JwtUser user = AuthStorage.getUser();
        long userId = Long.parseLong(user.getUserId());

        trackMapper.deleteTrack(trackId);
    }

    @Override
    public List<TrackBean> getTracksByName(String trackName) {
        JwtUser user = AuthStorage.getUser();
        long userId = Long.parseLong(user.getUserId());

        List<TrackBean> trackBeans = trackMapper.getTrackByName(trackName, userId);
        return trackBeans;
    }

    @Override
    public SingleFileGeologyType getGeologyFileByTrackBuffer(Long trackId, Integer buffer) {
        SingleFileGeologyType singleFileGeologyType = trackMapper.getGeologyFileByTrackBuffer(trackId, buffer);


        return singleFileGeologyType;
    }

    @Override
    public SingleFileGeologyType getGeologyFileByTrackBufferName(Long trackId, Integer buffer, String keyWords) {
        SingleFileGeologyType singleFileGeologyType = trackMapper.getGeologyFileByTrackBufferName(trackId, buffer, keyWords);


        return singleFileGeologyType;
    }
}
