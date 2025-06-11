package com.geology.controller;

import com.geology.common.ApiResponse;
import com.geology.common.utils.PaginationUtil;
import com.geology.domain.DTO.RectangleDTO;
import com.geology.domain.bean.PoiLocationBean;
import com.geology.domain.bean.SingleFileGeologyType;
import com.geology.domain.bean.TrackBean;
import com.geology.repository.db.entity.TrackEntity;
import com.geology.service.TrackService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/track")
@Slf4j
public class TrackController {

    @Autowired
    private TrackService trajectoryService;

    @PostMapping("/uploadTrack")
    public ApiResponse<String> uploadKml(@RequestParam("name") String name,
                                         @RequestParam("description") String description,
                                         @RequestParam("file") MultipartFile file) throws IOException {

        try {
            String kmlContent = new String(file.getBytes(), StandardCharsets.UTF_8);
            Long trackId = trajectoryService.saveTrack(name, description, kmlContent);

            return ApiResponse.success(trackId.toString());
        } catch (Exception e) {
            throw new RuntimeException();
        }

    }

    @CrossOrigin(origins = "https://geologymine.fun", allowCredentials = "true")
    @GetMapping("/getTracks")
    public ApiResponse<Map<String, Object>> getTracks(@RequestParam(defaultValue = "1") int currentPage,
                                                      @RequestParam(defaultValue = "5") int pageSize) {
        List<TrackBean> trackBeans = trajectoryService.getTracksByUserId();
        // 获取分页数据
        List<TrackBean> paginatedList = PaginationUtil.paginate(trackBeans, currentPage, pageSize);

        Map<String, Object> map = new HashMap<>();
        map.put("data", paginatedList);
        map.put("currentPage", currentPage);
        map.put("pageSize", pageSize);
        map.put("total", trackBeans.size());

        return ApiResponse.success(map);
    }

    @CrossOrigin(origins = "https://geologymine.fun", allowCredentials = "true")
    @GetMapping("/deleteTrack")
    public ApiResponse<String> deleteTrack(@RequestParam("trackId") Long trackId) {
        trajectoryService.deleteTrack(trackId);
        return ApiResponse.success("deleted");
    }

    @CrossOrigin(origins = "https://geologymine.fun", allowCredentials = "true")
    @GetMapping("/getTracksByName")
    public ApiResponse<Map<String, Object>> getTracksByName(@RequestParam(defaultValue = "1") int currentPage,
                                                          @RequestParam(defaultValue = "5") int pageSize,
                                                          @RequestParam("trackName") String trackName) {
        List<TrackBean> trackBeans = trajectoryService.getTracksByName(trackName);
        // 获取分页数据
        List<TrackBean> paginatedList = PaginationUtil.paginate(trackBeans, currentPage, pageSize);

        Map<String, Object> map = new HashMap<>();
        map.put("data", paginatedList);
        map.put("currentPage", currentPage);
        map.put("pageSize", pageSize);
        map.put("total", trackBeans.size());

        return ApiResponse.success(map);
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/getGeologyFileByTrackBuffer")
    public ApiResponse<SingleFileGeologyType> getGeologyFileByTrackBuffer(@RequestParam("trackId") Long trackId, @RequestParam(defaultValue = "1000") int buffer) {
        SingleFileGeologyType singleFileGeologyType = trajectoryService.getGeologyFileByTrackBuffer(trackId, buffer);

        return ApiResponse.success(singleFileGeologyType);
    }

}

