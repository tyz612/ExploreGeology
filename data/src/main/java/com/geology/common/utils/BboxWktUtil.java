package com.geology.common.utils;

import org.springframework.stereotype.Component;
import java.util.Locale;

@Component
public class BboxWktUtil {

    /**
     * 将边界框转换为WKT格式的矩形多边形
     *
     * @param minLon 最小经度 (West)
     * @param maxLon 最大经度 (East)
     * @param minLat 最小纬度 (South)
     * @param maxLat 最大纬度 (North)
     * @return WKT格式的POLYGON字符串
     * @throws IllegalArgumentException 如果输入参数无效
     */
    public String convertToWktPolygon(double minLon, double maxLon, double minLat, double maxLat) {
        // 参数验证
        if (minLon >= maxLon) {
            throw new IllegalArgumentException("最小经度必须小于最大经度");
        }
        if (minLat >= maxLat) {
            throw new IllegalArgumentException("最小纬度必须小于最大纬度");
        }
        if (!isValidLongitude(minLon) || !isValidLongitude(maxLon)) {
            throw new IllegalArgumentException("经度值必须在[-180, 180]范围内");
        }
        if (!isValidLatitude(minLat) || !isValidLatitude(maxLat)) {
            throw new IllegalArgumentException("纬度值必须在[-90, 90]范围内");
        }

        // 构建WKT多边形（逆时针顺序）
        return String.format(Locale.US,
                "POLYGON ((%.6f %.6f, %.6f %.6f, %.6f %.6f, %.6f %.6f, %.6f %.6f))",
                minLon, minLat,  // 左下 (SW)
                maxLon, minLat,  // 右下 (SE)
                maxLon, maxLat,  // 右上 (NE)
                minLon, maxLat,  // 左上 (NW)
                minLon, minLat   // 闭合回起点
        );
    }

    /**
     * 验证经度有效性
     */
    private boolean isValidLongitude(double lon) {
        return lon >= -180.0 && lon <= 180.0;
    }

    /**
     * 验证纬度有效性
     */
    private boolean isValidLatitude(double lat) {
        return lat >= -90.0 && lat <= 90.0;
    }
}