package com.geology.common.utils;

import com.geology.domain.bean.CoordinateBean;
import com.geology.repository.db.entity.TrackEntity;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class KmlParseUtil {
    @Autowired
    private CoordinateTransformUtil coordinateTransformUtil;
    private final GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);

    public TrackEntity parseKml(String kmlContent) {
        Document doc = Jsoup.parse(kmlContent);
        TrackEntity trajectory = new TrackEntity();

        // 解析元数据
        trajectory.setName(doc.select("Document > name").text());

        Element description = null;
        Element extent = null;
        // 解析轨迹描述信息
        Elements descriptions = doc.select("Placemark > description");
        for (int i = 0; i < descriptions.size(); i++){
            String descriptionTemp = descriptions.get(i).text();
            if (descriptionTemp.contains("开始时间"))
            {
                description = descriptions.get(i);
            }
            else
            {
                continue;
            }
        }
        Elements extents = doc.select("ExtendedData");
        if (description != null) {
            // 获取description内所有div元素
            Elements divs = description.select("div");
            for (Element div : divs) {
                String text = div.text().trim();
                // 检查是否为最高海拔行
                if (text.startsWith("最高海拔")) {
                    // 提取冒号后的内容并移除单位"米"
                    String maxvaluePart = text.split(":")[1];  // 使用中文冒号分割
                    String maxaltitudeValue = maxvaluePart.replace("米", "").trim();
                    // 转换为double类型
                    try {
                        Float maxAltitude = Float.parseFloat(maxaltitudeValue);
                        trajectory.setMaxAltitude(maxAltitude);
                    } catch (NumberFormatException e) {
                        log.info("最高转换失败: " + maxaltitudeValue);
                    }
                    break;
                }
            }
            for (Element div : divs) {
                String text = div.text().trim();
                // 检查是否为最高海拔行
                if (text.startsWith("最低海拔")) {
                    // 提取冒号后的内容并移除单位"米"
                    String minvaluePart = text.split(":")[1];  // 使用中文冒号分割
                    String minaltitudeValue = minvaluePart.replace("米", "").trim();
                    // 转换为double类型
                    try {
                        Float minAltitude = Float.parseFloat(minaltitudeValue);
                        trajectory.setMinAltitude(minAltitude);
                    } catch (NumberFormatException e) {
                        log.info("最低转换失败: " + minaltitudeValue);
                    }
                }
            }

            for (Element div : divs) {
                String text = div.text().trim();
                // 检查是否为最高海拔行
                if (text.startsWith("本段里程")) {
                    // 提取冒号后的内容并移除单位"米"
                    String valuePart = text.split(":")[1];  // 使用中文冒号分割
                    String totalDistance = valuePart.replace("米", "").trim();
                    // 转换为double类型
                    try {
                        Float distance = Float.parseFloat(totalDistance);
                        trajectory.setDistance(distance);
                    } catch (NumberFormatException e) {
                        log.info("里程转换失败: " + totalDistance);
                    }
                }
            }

            for (Element div : divs) {
                String text = div.text().trim();
                // 检查是否为最高海拔行
                if (text.startsWith("开始时间")) {
                    String valuePart = text.split("间:")[1];  // 使用中文冒号分割
                    try {
                        trajectory.setStartTime(valuePart);
                    } catch (NumberFormatException e) {
                        log.info("起始时间转换失败: " + valuePart);
                    }
                }
            }

            for (Element div : divs) {
                String text = div.text().trim();
                // 检查是否为最高海拔行
                if (text.startsWith("结束时间")) {
                    String valuePart = text.split("间:")[1];  // 使用中文冒号分割
                    try {
                        trajectory.setEndTime(valuePart);
                    } catch (NumberFormatException e) {
                        log.info("结束时间转换失败: " + valuePart);
                    }
                }
            }
        }
        else {
            for (int i = 0; i < extents.size(); i++)
            {
                String extentElement = extents.get(i).toString();
                if (extentElement.contains("BeginTime")){
                    extent = extents.get(i);
                }
                else {
                    continue;
                }
            }
            Elements datas = extent.select("data");
            for (Element div : datas) {
                String text = div.toString().trim();
                // 检查是否为最高海拔行
                if (text.contains("Mileage")) {
                    // 提取冒号后的内容并移除单位"米"
                    String valuePart = text.split("\n")[1].trim();  // 使用中文冒号分割
//                    String totalDistance = valuePart.replace("米", "").trim();
                    // 转换为double类型
                    try {
                        Float distance = Float.parseFloat(valuePart);
                        trajectory.setDistance(distance);
                    } catch (NumberFormatException e) {
                        log.info("里程转换失败: " + valuePart);
                    }
                }
            }

            for (Element div : datas) {
                String text = div.toString().trim();
                // 检查是否为最高海拔行
                if (text.contains("BeginTime")) {
                    // 提取冒号后的内容并移除单位"米"
                    String valuePart = text.split("\n")[1].trim();  // 使用中文冒号分割
//                    String totalDistance = valuePart.replace("米", "").trim();
                    // 转换为double类型
                    try {
                        Instant instant = Instant.ofEpochMilli(Long.parseLong(valuePart));
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                                .withZone(ZoneId.systemDefault()); // 使用系统默认时区
                        String formattedDate = formatter.format(instant);
                        trajectory.setStartTime(formattedDate);
                    } catch (NumberFormatException e) {
                        log.info("里程转换失败: " + valuePart);
                    }
                }
            }

            for (Element div : datas) {
                String text = div.toString().trim();
                // 检查是否为最高海拔行
                if (text.contains("EndTime")) {
                    // 提取冒号后的内容并移除单位"米"
                    String valuePart = text.split("\n")[1].trim();  // 使用中文冒号分割
//                    String totalDistance = valuePart.replace("米", "").trim();
                    // 转换为double类型
                    try {
                        Instant instant = Instant.ofEpochMilli(Long.parseLong(valuePart));
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                                .withZone(ZoneId.systemDefault()); // 使用系统默认时区
                        String formattedDate = formatter.format(instant);
                        trajectory.setEndTime(formattedDate);
                    } catch (NumberFormatException e) {
                        log.info("里程转换失败: " + valuePart);
                    }
                }
            }

            // 解析轨迹点
            Elements coords = doc.select("gx|coord");
            List<Float> elevationList = new ArrayList<>();
            for (Element coord : coords) {
                String[] values = coord.text().split("\\s+");
                if (values.length >= 3) {
                    Float elevation = Float.parseFloat(values[2]);
                    elevationList.add(elevation);
                }
            }
            if (!elevationList.isEmpty()) {
                Float minElevation = Collections.min(elevationList);
                Float maxElevation = Collections.max(elevationList);
                trajectory.setMinAltitude(minElevation);
                trajectory.setMaxAltitude(maxElevation);
            } else {
                log.info("海拔为空");
            }

        }

        // 解析轨迹点
        Elements coords = doc.select("gx|coord");
        List<Coordinate> coordinates = new ArrayList<>();
        for (Element coord : coords) {
            String[] values = coord.text().split("\\s+");
            if (values.length >= 2) {
                double lon = Double.parseDouble(values[0]);
                double lat = Double.parseDouble(values[1]);

                CoordinateBean gcjcoordinateBean = coordinateTransformUtil.wgs84togcj02(new CoordinateBean(lon, lat));

                coordinates.add(new Coordinate(gcjcoordinateBean.getLon(), gcjcoordinateBean.getLat()));
            }
        }

        // 创建LineString
        Coordinate[] coordArray = coordinates.toArray(new Coordinate[0]);
        LineString lineString = geometryFactory.createLineString(coordArray);
        trajectory.setGeom(lineString.toString());

        return trajectory;
    }

//    private String parseTime(String timestamp) {
//        if (timestamp != null && !timestamp.isEmpty()) {
//            return Instant.ofEpochMilli(Long.parseLong(timestamp));
//        }
//        return null;
//    }

    private Double extractValue(String text, String prefix, String suffix) {
        try {
            int startIdx = text.indexOf(prefix);
            if (startIdx == -1) return null;

            startIdx += prefix.length();
            int endIdx = text.indexOf(suffix, startIdx);
            if (endIdx == -1) return null;

            String valueStr = text.substring(startIdx, endIdx).trim();
            return Double.parseDouble(valueStr);
        } catch (Exception e) {
            return null;
        }
    }
}
