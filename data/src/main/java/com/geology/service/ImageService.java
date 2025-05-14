package com.geology.service;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import com.drew.metadata.exif.PanasonicRawIFD0Descriptor;
import com.geology.common.jwt.AuthStorage;
import com.geology.common.jwt.JwtUser;
import com.geology.common.utils.GeologyDistributedIdGenerator;
import com.geology.common.utils.FileStorageUtil;
import com.geology.domain.DTO.PoiLocationDTO;
import com.geology.domain.bean.PoiLocationBean;
import com.geology.domain.bean.UserPhotoBean;
import com.geology.repository.db.entity.UserPhotoEntity;
import com.geology.repository.db.mapper.GetGeologyInfoMapper;
import lombok.RequiredArgsConstructor;
import org.h2.engine.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ImageService {

    @Autowired
    private FileStorageUtil fileStorageUtil;

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Autowired
    private GetGeologyInfoMapper getGeologyInfoMapper;

    public Long uploadImage(MultipartFile file) throws IOException, ImageProcessingException {
        JwtUser user = AuthStorage.getUser();
        long userId = Long.parseLong(user.getUserId());

        String originalFilename = file.getOriginalFilename();
        String storedFilename = fileStorageUtil.storeFile(file);
        UserPhotoBean image = new UserPhotoBean();

        Long photoId = GeologyDistributedIdGenerator.getInstance().nextId();
        image.setPhotoId(photoId);

        image.setPicName(originalFilename);
        image.setFilePath(Paths.get(uploadDir, storedFilename).toString());
        image.setUserId(userId);

        Date now = new Date();
        image.setCreateTime(now);
        image.setStatus(1);

        Long uploadPhotoInfo = getGeologyInfoMapper.insertUserPhoto(image);
        uploadPhotoInfo = photoId;

        return uploadPhotoInfo;
    }

    public Long savePoi(double lon, double lat, String description,String name, MultipartFile file) throws IOException {
        JwtUser user = AuthStorage.getUser();
        long userId = Long.parseLong(user.getUserId());

        PoiLocationDTO poiLocationDTO = new PoiLocationDTO();
        UserPhotoBean userPhotoBean = new UserPhotoBean();

        Long markerId = GeologyDistributedIdGenerator.getInstance().nextId();
        poiLocationDTO.setId(markerId);
        poiLocationDTO.setDescription(description);
        poiLocationDTO.setCreateTime(new Date());
        poiLocationDTO.setUserId(userId);
        poiLocationDTO.setLon(lon);
        poiLocationDTO.setLat(lat);
        poiLocationDTO.setName(name);


        String originalFilename = file.getOriginalFilename();
        String storedFilename = fileStorageUtil.storeFile(file);
        UserPhotoBean image = new UserPhotoBean();
        Long photoId = GeologyDistributedIdGenerator.getInstance().nextId();
        image.setPhotoId(photoId);
        image.setMarkerId(markerId);
        image.setPicName(originalFilename);
        image.setFilePath(storedFilename);
        image.setUserId(userId);
        Date now = new Date();
        image.setCreateTime(now);
        image.setStatus(1);

        Long uploadPhotoInfo = getGeologyInfoMapper.insertUserPhoto(image);
        Long savePoiInfo = getGeologyInfoMapper.insertPoi(poiLocationDTO);
        savePoiInfo = poiLocationDTO.getId();

        return savePoiInfo;
    }

    public List<PoiLocationBean> getAllPoi() {
        JwtUser user = AuthStorage.getUser();
        long userId = Long.parseLong(user.getUserId());

        List<PoiLocationBean> poiLocationBeans = getGeologyInfoMapper.getPoiByUserId(userId);

        return poiLocationBeans;
    }

    public List<PoiLocationBean> getAllPoiByName(String poiName)
    {
        JwtUser user = AuthStorage.getUser();
        long userId = Long.parseLong(user.getUserId());

        List<PoiLocationBean> poiLocationBeans = getGeologyInfoMapper.getPoiByName(poiName, userId);
        return poiLocationBeans;
    }


    public void deletePoi(Long markerId)
    {
        JwtUser user = AuthStorage.getUser();
        long userId = Long.parseLong(user.getUserId());

        getGeologyInfoMapper.deletePoi(markerId);
    }

}
