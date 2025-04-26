package com.geology.controller;

import cn.hutool.json.JSONObject;
import com.drew.imaging.ImageProcessingException;
import com.geology.common.ApiResponse;
import com.geology.common.jwt.AuthStorage;
import com.geology.common.jwt.JwtUser;
import com.geology.common.utils.PaginationUtil;
import com.geology.domain.bean.PoiLocationBean;
import com.geology.repository.db.entity.UserPhotoEntity;
import com.geology.service.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;

@RestController
@RequestMapping("/images")
@RequiredArgsConstructor
public class ImageController {
    @Autowired
    private ImageService imageService;

    // Configure your upload directory path (should match where you save images)
    private final Path uploadDir = Paths.get("D:/projects/data/uploadPic/");

    @CrossOrigin(origins = "*")
    @PostMapping("/upload")
    public ResponseEntity<?> uploadImage(@RequestParam("image") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body("请选择要上传的图片");
            }

            Long uploadedImage = imageService.uploadImage(file);
            return ResponseEntity.ok(uploadedImage);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("图片上传失败: " + e.getMessage());
        } catch (ImageProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @CrossOrigin(origins = "*")
    @PostMapping("/savePoi")
    public ApiResponse<Long> savePoi(@RequestParam("lon") double lon, @RequestParam("lat") double lat,
                                     @RequestParam("description") String description,
                                     @RequestParam("image") MultipartFile file, @RequestParam("name") String name) throws IOException {
        Long savePoi = imageService.savePoi(lon,lat,description,name,file);

        return ApiResponse.success(savePoi);
    }

//    @CrossOrigin(origins = "*")
    @CrossOrigin(origins = "http://localhost:8081", allowCredentials = "true")
    @GetMapping("/getPois")
    public ApiResponse<Map<String, Object>> getPois(@RequestParam(defaultValue = "1") int currentPage,
                                                    @RequestParam(defaultValue = "5") int pageSize) {
        List<PoiLocationBean> poiLocationBeans = imageService.getAllPoi();
        // 获取分页数据
        List<PoiLocationBean> paginatedList = PaginationUtil.paginate(poiLocationBeans, currentPage, pageSize);

        Map<String, Object> map = new HashMap<>();
        map.put("data", paginatedList);
        map.put("currentPage", currentPage);
        map.put("pageSize", pageSize);
        map.put("total", poiLocationBeans.size());

        return ApiResponse.success(map);
    }

    @CrossOrigin(origins = "http://localhost:8081")
    @GetMapping("/getPoiImage")
    public void getImage(@RequestParam("fileName") String filename, HttpServletResponse response) throws IOException {
        // 构建图片的完整路径
        String imagePath = "D:/projects/data/uploadPic/" + filename;

        // 检查文件是否存在
        File file = new File(imagePath);
        if (!file.exists()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "图片未找到");
            return;
        }

        // 设置响应头
        response.setContentType("image/jpeg"); // 根据图片类型设置正确的 MIME 类型
        response.setContentLengthLong(file.length());

        // 写入文件内容
        try (InputStream inputStream = new FileInputStream(file);
             OutputStream outputStream = response.getOutputStream()) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        }
    }

    @CrossOrigin(origins = "http://localhost:8081", allowCredentials = "true")
    @GetMapping("/getPoisByName")
    public ApiResponse<Map<String, Object>> getPoisByName(@RequestParam(defaultValue = "1") int currentPage,
                                                          @RequestParam(defaultValue = "5") int pageSize,
                                                          @RequestParam("poiName") String poiName) {
        List<PoiLocationBean> poiLocationBeans = imageService.getAllPoiByName(poiName);
        // 获取分页数据
        List<PoiLocationBean> paginatedList = PaginationUtil.paginate(poiLocationBeans, currentPage, pageSize);

        Map<String, Object> map = new HashMap<>();
        map.put("data", paginatedList);
        map.put("currentPage", currentPage);
        map.put("pageSize", pageSize);
        map.put("total", poiLocationBeans.size());

        return ApiResponse.success(map);
    }
}