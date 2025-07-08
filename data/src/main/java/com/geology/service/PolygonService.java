package com.geology.service;

import com.geology.domain.DTO.DrawingPolygonDTO;
import com.geology.domain.DTO.PolygonGeojsonDTO;
import com.geology.domain.bean.PolygonBean;
import com.geology.domain.bean.TrackBean;
import com.sun.org.apache.xpath.internal.operations.Mult;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;

public interface PolygonService {
    Long savePolygon(String name, String description, MultipartFile file) throws IOException;

    List<PolygonBean> getPolygonsByUserId();

    void deleteTrack(Long polygonId);

    List<PolygonBean> getPolygonsByGroupId(Long polygonId);

    Long saveDrawingPolygon(DrawingPolygonDTO drawingPolygonDTO);

    Long saveDrawingRectangle(DrawingPolygonDTO drawingPolygonDTO);
}
