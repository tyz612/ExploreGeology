package com.geology.common.utils;

import com.geology.domain.bean.CoordinateBean;
import com.geology.domain.bean.RectifyTIFInfoBean;
import lombok.extern.slf4j.Slf4j;
import org.gdal.gdal.Dataset;
import org.gdal.gdal.GCP;
import org.gdal.gdal.gdal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.stream.IntStream;

@Component
@Slf4j
public class GenerateGCPsUtil {

    @Autowired
    private CoordinateTransformUtil coordinateTransformUtil;

    public ArrayList<GCP> generateGCPs(RectifyTIFInfoBean rectifyTIFInfoBean) {
        gdal.AllRegister();
        Dataset source = gdal.Open(rectifyTIFInfoBean.getInputFilePath());
        ArrayList<GCP> gcps = new ArrayList<>();
//        GCP[] gcps = new GCP[rectifyTIFInfoBean.getGcpNumbers()];

        double[] geotransform = source.GetGeoTransform();
        int cols = source.getRasterXSize();
        int rows = source.getRasterYSize();

        int[] XSamples = IntStream.range(0, cols + 1)
                .filter(i -> (i - 0) % (cols / rectifyTIFInfoBean.getGcpNumbers()) == 0)
                .toArray();

        int[] YSamples = IntStream.range(0, rows + 1)
                .filter(i -> (i - 0) % (rows / rectifyTIFInfoBean.getGcpNumbers()) == 0)
                .toArray();

        ArrayList<Integer> ori_x_ls = new ArrayList<>();
        ArrayList<Integer> ori_y_ls = new ArrayList<>();
        ArrayList<Double> ori_lon_ls = new ArrayList<>();
        ArrayList<Double> ori_lat_ls = new ArrayList<>();
        ArrayList<Double> gcj_lon_ls = new ArrayList<>();
        ArrayList<Double> gcj_lat_ls = new ArrayList<>();

        for (int i : YSamples) {
            for (int j : XSamples) {
                double ori_lon = j * geotransform[1] + geotransform[0];
                double ori_lat = i * geotransform[5] + geotransform[3];
                int ori_x = j;
                int ori_y = i;
                ori_x_ls.add(ori_x);
                ori_y_ls.add(ori_y);
                ori_lon_ls.add(ori_lon);
                ori_lat_ls.add(ori_lat);

                CoordinateBean gcjcoordinateBean = coordinateTransformUtil.wgs84togcj02(new CoordinateBean(ori_lon, ori_lat));

                GCP gcp = new GCP(gcjcoordinateBean.getLon(), gcjcoordinateBean.getLat(), 0, j, i, "GCP".concat(String.valueOf(i)), "ID".concat(String.valueOf(i)));
                gcps.add(gcp);
            }
        }

        return gcps;
    }
}
