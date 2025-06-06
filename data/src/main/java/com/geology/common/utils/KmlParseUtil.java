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

import java.time.Instant;
import java.util.ArrayList;
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

        // 解析轨迹描述信息
        Element description = doc.select("Placemark > description").first();
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
