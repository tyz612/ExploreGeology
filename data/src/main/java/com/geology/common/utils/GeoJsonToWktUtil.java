package com.geology.common.utils;

import com.geology.domain.bean.CoordinateBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.json.JSONArray;
import org.json.JSONObject;
import org.locationtech.jts.geom.*;
import org.locationtech.jts.io.WKTWriter;

@Component
public class GeoJsonToWktUtil {

    @Autowired
    private CoordinateTransformUtil coordinateTransformUtil;
    private final GeometryFactory GEOMETRY_FACTORY = new GeometryFactory();

    /**
     * 将 GeoJSON 字符串转换为 WKT 字符串
     * @param geoJson 有效的 GeoJSON 字符串
     * @return WKT 字符串
     * @throws IllegalArgumentException 如果输入不是有效的 GeoJSON
     */

    public String convert(String geoJson) {
        JSONObject json = new JSONObject(geoJson);
        Geometry geometry = parseGeometry(json);
        return new WKTWriter().write(geometry);
    }

    private Geometry parseGeometry(JSONObject json) {
        String type = json.getString("type");
        JSONArray coordinates = json.getJSONArray("coordinates");

        switch (type) {
            case "Point":
                return createPoint(coordinates);
            case "LineString":
                return createLineString(coordinates);
            case "Polygon":
                return createPolygon(coordinates);
            case "MultiPoint":
                return createMultiPoint(coordinates);
            case "MultiLineString":
                return createMultiLineString(coordinates);
            case "MultiPolygon":
                return createMultiPolygon(coordinates);
            default:
                throw new IllegalArgumentException("Unsupported GeoJSON type: " + type);
        }
    }

    private Point createPoint(JSONArray coords) {
        Coordinate coord = parseCoordinate(coords);
        return GEOMETRY_FACTORY.createPoint(coord);
    }

    private LineString createLineString(JSONArray coords) {
        return GEOMETRY_FACTORY.createLineString(parseCoordinates(coords));
    }

    private Polygon createPolygon(JSONArray rings) {
        // 外环
        LinearRing outerRing = GEOMETRY_FACTORY.createLinearRing(parseCoordinates(rings.getJSONArray(0)));

        // 内环（孔洞）
        LinearRing[] innerRings = new LinearRing[rings.length() - 1];
        for (int i = 1; i < rings.length(); i++) {
            innerRings[i - 1] = GEOMETRY_FACTORY.createLinearRing(parseCoordinates(rings.getJSONArray(i)));
        }

        return GEOMETRY_FACTORY.createPolygon(outerRing, innerRings);
    }

    private MultiPoint createMultiPoint(JSONArray points) {
        Point[] pointArray = new Point[points.length()];
        for (int i = 0; i < points.length(); i++) {
            pointArray[i] = createPoint(points.getJSONArray(i));
        }
        return GEOMETRY_FACTORY.createMultiPoint(pointArray);
    }

    private MultiLineString createMultiLineString(JSONArray lines) {
        LineString[] lineStrings = new LineString[lines.length()];
        for (int i = 0; i < lines.length(); i++) {
            lineStrings[i] = createLineString(lines.getJSONArray(i));
        }
        return GEOMETRY_FACTORY.createMultiLineString(lineStrings);
    }

    private MultiPolygon createMultiPolygon(JSONArray polygons) {
        Polygon[] polygonArray = new Polygon[polygons.length()];
        for (int i = 0; i < polygons.length(); i++) {
            polygonArray[i] = createPolygon(polygons.getJSONArray(i));
        }
        return GEOMETRY_FACTORY.createMultiPolygon(polygonArray);
    }

    private Coordinate parseCoordinate(JSONArray coordArray) {
        double x = coordArray.getDouble(0);
        double y = coordArray.getDouble(1);

//        CoordinateBean coordinateBean = new CoordinateBean(x, y);
//        CoordinateBean modifycoordinateBean = coordinateTransformUtil.wgs84togcj02(coordinateBean);
        // 处理3D坐标（Z值）
        if (coordArray.length() > 2) {
            double z = coordArray.getDouble(2);
//            return new Coordinate(modifycoordinateBean.getLon(), modifycoordinateBean.getLat(), z);
            return new Coordinate(x, y, z);
        }
        return new Coordinate(x,y);
    }

    private Coordinate[] parseCoordinates(JSONArray coordArrays) {
        Coordinate[] coords = new Coordinate[coordArrays.length()];
        for (int i = 0; i < coordArrays.length(); i++) {
            coords[i] = parseCoordinate(coordArrays.getJSONArray(i));
        }
        return coords;
    }
}
