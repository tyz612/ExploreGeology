package com.geology.common.utils;


import com.geology.domain.bean.CoordinateBean;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.locationtech.jts.geom.*;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.geotools.data.FileDataStore;
import org.geotools.data.FileDataStoreFinder;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.geometry.jts.JTS;
import org.geotools.referencing.CRS;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

@Component
@Slf4j
public class Shape2PostgisUtil {

    @Autowired
    private CoordinateTransformUtil coordinateTransformUtil;

    /**
     * 从ZIP压缩包中提取SHP文件并解析多边形
     */
    public List<String> parsePolygonsFromZip(MultipartFile zipFile, int targetSrid) throws IOException {
        // 创建临时目录
        Path tempDir = Files.createTempDirectory("shp_zip_");
        try {
            // 解压ZIP到临时目录
            Path zipPath = saveMultipartFileToTemp(zipFile, tempDir);
            // 处理ZIP文件
            return parsePolygonsFromZip(zipPath.toString(), targetSrid);
        } finally {
            // 清理临时目录
            FileUtils.deleteDirectory(tempDir.toFile());
        }
    }


    /**
     * 保存MultipartFile到临时文件
     */
    private Path saveMultipartFileToTemp(MultipartFile file, Path tempDir) throws IOException {
        String fileName = file.getOriginalFilename();
        if (fileName == null || fileName.isEmpty()) {
            fileName = "uploaded_file";
        }

        Path tempFile = tempDir.resolve(fileName);
        try (InputStream inputStream = file.getInputStream()) {
            Files.copy(inputStream, tempFile, StandardCopyOption.REPLACE_EXISTING);
        }
        return tempFile;
    }


    /**
     * 从ZIP压缩包中提取SHP文件并解析多边形
     */
    public List<String> parsePolygonsFromZip(String zipPath, int targetSrid) throws IOException {
        Path tempDir = Files.createTempDirectory("shp_extract_");
        try {
            List<File> shpFiles = extractShapefile(zipPath, tempDir.toString());
            List<String> results = new ArrayList<>();
            for (File shpFile : shpFiles) {
                results.addAll(parseShapefilePolygons(shpFile, targetSrid));
            }
            return results;
        } finally {
            deleteDirectory(tempDir.toFile());
        }
    }



    /**
     * 解压SHP压缩包
     */
    private static List<File> extractShapefile(String zipPath, String outputDir) throws IOException {
        List<File> shpFiles = new ArrayList<>();
        File zipFile = new File(zipPath);

        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile))) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                Path outputPath = Paths.get(outputDir, entry.getName());

                if (!entry.isDirectory()) {
                    Files.createDirectories(outputPath.getParent());
                    Files.copy(zis, outputPath, StandardCopyOption.REPLACE_EXISTING);

                    if (entry.getName().toLowerCase().endsWith(".shp")) {
                        shpFiles.add(outputPath.toFile());
                    }
                }
                zis.closeEntry();
            }
        }
        return shpFiles;
    }

    /**
     * 解析SHP文件中的多边形
     */
    private List<String> parseShapefilePolygons(File shpFile, int targetSrid) throws IOException {
        List<String> polygons = new ArrayList<>();
        FileDataStore store = null;

        try {
            store = FileDataStoreFinder.getDataStore(shpFile);
            SimpleFeatureCollection features = store.getFeatureSource().getFeatures();
            CoordinateReferenceSystem sourceCrs = store.getSchema().getCoordinateReferenceSystem();

            MathTransform transform = null;
            CoordinateReferenceSystem targetCrs = null;

            if (sourceCrs != null) {
                try {
                    targetCrs = CRS.decode("EPSG:" + targetSrid);

                    // 检查是否需要转换
                    if (!CRS.equalsIgnoreMetadata(sourceCrs, targetCrs)) {
                        transform = CRS.findMathTransform(sourceCrs, targetCrs, true);
                    }
                } catch (FactoryException e) {
                    throw new IOException("CRS处理失败: " + e.getMessage(), e);
                }
            } else {
                targetCrs = DefaultGeographicCRS.WGS84;
            }

            // 遍历所有要素
            try (SimpleFeatureIterator iterator = features.features()) {
                while (iterator.hasNext()) {
                    SimpleFeature feature = iterator.next();
                    Object geomObj = feature.getDefaultGeometry();

                    if (geomObj instanceof Geometry) {
                        Geometry geom = (Geometry) geomObj;

                        // 执行坐标转换
                        if (transform != null) {
                            try {
                                geom = org.geotools.geometry.jts.JTS.transform(geom, transform);
                            } catch (TransformException e) {
                                throw new IOException("几何体转换失败: " + e.getMessage(), e);
                            }
                        }

                        // 修正坐标顺序：交换经度和纬度
                        geom = swapCoordinates(geom);

                        // 转换为WKT
                        if (isPolygonType(geom)) {
                            polygons.add(geom.toText());
                        }
                    }
                }
            }
            return polygons;
        } finally {
            if (store != null) {
                store.dispose();
            }
        }
    }

    /**
     * 交换坐标顺序（经度和纬度互换）
     */
    private Geometry swapCoordinates(Geometry geom) {
        if (geom instanceof Point) {
            Point point = (Point) geom;
            Coordinate coord = point.getCoordinate();
            return geom.getFactory().createPoint(new Coordinate(coord.y, coord.x));
        } else if (geom instanceof Polygon) {
            Polygon polygon = (Polygon) geom;
            LinearRing exterior = swapLinearRing((LinearRing) polygon.getExteriorRing());
            LinearRing[] interiors = new LinearRing[polygon.getNumInteriorRing()];
            for (int i = 0; i < polygon.getNumInteriorRing(); i++) {
                interiors[i] = swapLinearRing((LinearRing) polygon.getInteriorRingN(i));
            }
            return geom.getFactory().createPolygon(exterior, interiors);
        } else if (geom instanceof MultiPolygon) {
            MultiPolygon multi = (MultiPolygon) geom;
            Polygon[] polys = new Polygon[multi.getNumGeometries()];
            for (int i = 0; i < multi.getNumGeometries(); i++) {
                polys[i] = (Polygon) swapCoordinates(multi.getGeometryN(i));
            }
            return geom.getFactory().createMultiPolygon(polys);
        } else if (geom instanceof LineString) {
            LineString line = (LineString) geom;
            Coordinate[] coords = line.getCoordinates();
            Coordinate[] swapped = new Coordinate[coords.length];
            for (int i = 0; i < coords.length; i++) {
                swapped[i] = new Coordinate(coords[i].y, coords[i].x);
            }
            return geom.getFactory().createLineString(swapped);
        } else if (geom instanceof LinearRing) {
            return swapLinearRing((LinearRing) geom);
        } else {
            // 对于其他类型，保持原样
            return geom;
        }
    }

    /**
     * 交换LinearRing的坐标顺序
     */
    private LinearRing swapLinearRing(LinearRing ring) {
        Coordinate[] coords = ring.getCoordinates();
        Coordinate[] swapped = new Coordinate[coords.length];
        for (int i = 0; i < coords.length; i++) {
            CoordinateBean coordinateBean = new CoordinateBean(coords[i].y, coords[i].x);
            CoordinateBean modifycoordinateBean = coordinateTransformUtil.wgs84togcj02(coordinateBean);
            swapped[i] = new Coordinate(modifycoordinateBean.getLon(), modifycoordinateBean.getLat());
        }
        return ring.getFactory().createLinearRing(swapped);
    }

    /**
     * 检查是否为多边形类型
     */
    private static boolean isPolygonType(Geometry geom) {
        return geom instanceof Polygon || geom instanceof MultiPolygon;
    }

    /**
     * 递归删除目录
     */
    private static void deleteDirectory(File directory) {
        if (directory.isDirectory()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    deleteDirectory(file);
                }
            }
        }
        if (directory.exists()) {
            directory.delete();
        }
    }

    /**
     * 生成PostGIS插入SQL
     */
    public static String generateInsertSql(String wkt, int srid, String tableName) {
        // 防止SQL注入
        String safeTable = tableName.replaceAll("[^a-zA-Z0-9_]", "");
        return String.format("INSERT INTO %s (geom) VALUES (ST_GeomFromText('%s', %d));",
                safeTable, wkt, srid);
    }


}
