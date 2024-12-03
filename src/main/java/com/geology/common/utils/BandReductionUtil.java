package com.geology.common.utils;

import lombok.extern.slf4j.Slf4j;
import org.gdal.gdal.Band;
import org.gdal.gdal.Dataset;
import org.gdal.gdal.Driver;
import org.gdal.gdal.gdal;
import org.gdal.gdalconst.gdalconstConstants;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class BandReductionUtil {

    public void generateRGBImage()
    {
        gdal.AllRegister();

        // 打开原始影像文件
        Dataset dataset = gdal.Open("D:/badanjilinTest/SV1-01_20220406_L1B0001459266_6042300242240010_01-MUX_FUSION.tiff");
        if (dataset == null) {
            System.out.println("无法打开输入图像");
            return;
        }

        // 创建新的三波段影像
        Driver driver = gdal.GetDriverByName("GTiff");
        Dataset outputDataset = driver.Create("D:/badanjilinTest/SV01.tif", dataset.GetRasterXSize(), dataset.GetRasterYSize(), 3, 14);
        outputDataset.SetGeoTransform(dataset.GetGeoTransform());
        outputDataset.SetProjection(dataset.GetProjection());

        // 读取原始影像的前三个波段数据
        for (int band = 1; band <= 3; band++) {
            Band srcBand = dataset.GetRasterBand(band);
            Band dstBand = outputDataset.GetRasterBand(band);

            // 读取数据到数组
            int width = dataset.GetRasterXSize();
            int height = dataset.GetRasterYSize();
            int[] data = new int[width * height];
            srcBand.ReadRaster(0, 0, width, height, data);

            // 将数组数据写入新影像的对应波段
            dstBand.WriteRaster(0, 0, width, height, data);
        }

        // 清理资源
        dataset.delete();
        outputDataset.delete();
    }
}
