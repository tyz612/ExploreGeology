package com.geology.common.utils;

import com.geology.domain.bean.CoordinateBean;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CoordinateTransformUtil {

    private static double ee = 0.00669342162296594323;   //扁率
    private static double a = 6378245.0;                  //长半轴

    public static double transformLat(double lon, double lat) {
        double dlat = -100.0 + 2.0 * lon + 3.0 * lat + 0.2 * lat * lat + 0.1 * lon * lat + 0.2 * Math.sqrt(Math.abs(lon));
        dlat += (20.0 * Math.sin(6.0 * lon * Math.PI) + 20.0 * Math.sin(2.0 * lon * Math.PI)) * 2.0 / 3.0;
        dlat += (20.0 * Math.sin(lat * Math.PI) + 40.0 * Math.sin(lat / 3.0 * Math.PI)) * 2.0 / 3.0;
        dlat += (160.0 * Math.sin(lat / 12.0 * Math.PI) + 320 * Math.sin(lat * Math.PI / 30.0)) * 2.0 / 3.0;

        return dlat;
    }

    public static double transformlon(double lon, double lat) {
        double dlon = 300.0 + lon + 2.0 * lat + 0.1 * lon * lon + 0.1 * lon * lat + 0.1 * Math.sqrt(Math.abs(lon));
        dlon += (20.0 * Math.sin(6.0 * lon * Math.PI) + 20.0 * Math.sin(2.0 * lon * Math.PI)) * 2.0 / 3.0;
        dlon += (20.0 * Math.sin(lon * Math.PI) + 40.0 * Math.sin(lon / 3.0 * Math.PI)) * 2.0 / 3.0;
        dlon += (150.0 * Math.sin(lon / 12.0 * Math.PI) + 300.0 * Math.sin(lon / 30.0 * Math.PI)) * 2.0 / 3.0;


        return dlon;
    }


    public CoordinateBean wgs84togcj02(CoordinateBean coordinateBean) {
        double dlat = transformLat(coordinateBean.getLon() - 105.0, coordinateBean.getLat() - 35.0);
        double dlon = transformlon(coordinateBean.getLon() - 105.0, coordinateBean.getLat() - 35.0);
        double radlat = coordinateBean.getLat() / 180.0 * Math.PI;
        double magicTemp = Math.sin(radlat);
        double magic = 1 - ee * magicTemp * magicTemp;
        double sqrtmagic = Math.sqrt(magic);
        double dlatFinal = (dlat * 180.0) / ((a * (1 - ee)) / (magic * sqrtmagic) * Math.PI);
        double dlonFinal = (dlon * 180.0) / (a / sqrtmagic * Math.cos(radlat) * Math.PI);
        double mglat = coordinateBean.getLat() + dlatFinal;
        double mglon = coordinateBean.getLon() + dlonFinal;

        return new CoordinateBean(mglon, mglat);
    }

}
