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
import java.nio.file.Files;
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



import net.coobird.thumbnailator.Thumbnails;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

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
                                     @RequestParam("image") MultipartFile file, @RequestParam("name") String name,
                                     @RequestParam("avatar") String avatar, @RequestParam("userName") String userName) throws IOException {
        Long savePoi = imageService.savePoi(lon,lat,description,name,file,avatar, userName);

        return ApiResponse.success(savePoi);
    }

//    @CrossOrigin(origins = "*")
    @CrossOrigin(origins = "https://geologymine.fun", allowCredentials = "true")
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


    @CrossOrigin(origins = "https://geologymine.fun", allowCredentials = "true")
    @GetMapping("/getPublicPois")
    public ApiResponse<List<PoiLocationBean>> getPublicPois() {
        List<PoiLocationBean> poiLocationBeans = imageService.getPublicPoi();

        return ApiResponse.success(poiLocationBeans);
    }

    @CrossOrigin(origins = "https://geologymine.fun")
    @GetMapping("/getPoiImage")
    public void getImage(@RequestParam("fileName") String filename, HttpServletResponse response) throws IOException {
        // 构建图片的完整路径
        String imagePath = "/data/" + filename;

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

    @CrossOrigin(origins = "https://geologymine.fun")
    @GetMapping("/getAvatarImage")
    public void getAvatarImage(@RequestParam("fileName") String filename, HttpServletResponse response) throws IOException {
        // 构建图片的完整路径
        String imagePath = "/data/userImage/" + filename;

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




    @CrossOrigin(origins = "https://geologymine.fun", allowCredentials = "true")
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

    @CrossOrigin(origins = "https://geologymine.fun", allowCredentials = "true")
    @GetMapping("/deletePoi")
    public ApiResponse<String> deletePoi(@RequestParam("markerId") Long markerId) {
       imageService.deletePoi(markerId);
       return ApiResponse.success("deleted");
    }


    @CrossOrigin(origins = "https://geologymine.fun", allowCredentials = "true")
    @GetMapping("/publicPoi")
    public ApiResponse<String> publicPoi(@RequestParam("markerId") Long markerId) {
        imageService.publicPoi(markerId);
        return ApiResponse.success("updated");
    }



    @CrossOrigin(origins = "https://geologymine.fun", allowCredentials = "true")
    @GetMapping("/getPoiThumbnailImage")
    public void getImage(
            @RequestParam("fileName") String filename,
            @RequestParam(value = "width", defaultValue = "300") int width,
            @RequestParam(value = "height", defaultValue = "300") int height,
            HttpServletResponse response
    ) throws IOException {
        // 原图路径
        String originalPath = "/data/" + filename;
        File originalFile = new File(originalPath);

        // 缩略图路径（在原目录下创建 thumbnails 子目录）
        String thumbnailDir = "/data/thumbnails/";
        String thumbnailPath = thumbnailDir + filename;

        // 检查缩略图是否存在
        File thumbnailFile = new File(thumbnailPath);

        // 如果缩略图不存在，则生成
        if (!thumbnailFile.exists()) {
            // 确保缩略图目录存在
            new File(thumbnailDir).mkdirs();

            try {
                // 生成缩略图（保持比例，质量压缩）
                Thumbnails.of(originalFile)
                        .size(width, height)
                        .outputQuality(0.8)
                        .allowOverwrite(true)
                        .toFile(thumbnailPath);
            } catch (IOException e) {
                // 生成失败则返回原图
                sendOriginalImage(response, originalFile);
                return;
            }
        }

        // 发送缩略图
        sendThumbnailImage(response, thumbnailFile);
    }


    @CrossOrigin(origins = "https://geologymine.fun", allowCredentials = "true")
    @GetMapping("/getAvatarThumbnailImage")
    public void getAvatarThumbnailImage(
            @RequestParam("fileName") String filename,
            @RequestParam(value = "width", defaultValue = "200") int width,
            @RequestParam(value = "height", defaultValue = "200") int height,
            HttpServletResponse response
    ) throws IOException {
        // 原图路径
        String originalPath = "/data/userImage/" + filename;
        //本地环境
//        String originalPath = "D:/userImage/" + filename;
        File originalFile = new File(originalPath);

        // 缩略图路径（在原目录下创建 thumbnails 子目录）
        String thumbnailDir = "/data/userImage/thumbnails/";
        //本地环境
//        String thumbnailDir = "D:/userImage/thumbnails/" + filename;
        String thumbnailPath = thumbnailDir + filename;

        // 检查缩略图是否存在
        File thumbnailFile = new File(thumbnailPath);

        // 如果缩略图不存在，则生成
        if (!thumbnailFile.exists()) {
            // 确保缩略图目录存在
            new File(thumbnailDir).mkdirs();

            try {
                // 生成缩略图（保持比例，质量压缩）
                Thumbnails.of(originalFile)
                        .size(width, height)
                        .outputQuality(0.8)
                        .allowOverwrite(true)
                        .toFile(thumbnailPath);
            } catch (IOException e) {
                // 生成失败则返回原图
                sendOriginalImage(response, originalFile);
                return;
            }
        }

        // 发送缩略图
        sendThumbnailImage(response, thumbnailFile);
    }

    private void sendThumbnailImage(HttpServletResponse response, File thumbnailFile) throws IOException {
        if (!thumbnailFile.exists()) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "缩略图生成失败");
            return;
        }

        String mimeType = Files.probeContentType(thumbnailFile.toPath());
        response.setContentType(mimeType != null ? mimeType : "image/jpeg");
        response.setContentLengthLong(thumbnailFile.length());

        try (InputStream is = new FileInputStream(thumbnailFile);
             OutputStream os = response.getOutputStream()) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
        }
    }

    // 保留原图返回逻辑备用
    private void sendOriginalImage(HttpServletResponse response, File originalFile) throws IOException {
        if (!originalFile.exists()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "图片未找到");
            return;
        }

        String mimeType = Files.probeContentType(originalFile.toPath());
        response.setContentType(mimeType != null ? mimeType : "image/jpeg");
        response.setContentLengthLong(originalFile.length());

        try (InputStream is = new FileInputStream(originalFile);
             OutputStream os = response.getOutputStream()) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
        }
    }

}