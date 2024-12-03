package com.geology.service;

import com.geology.common.utils.CoordinateTransformUtil;
import com.geology.common.utils.GenerateGCPsUtil;
import com.geology.domain.bean.*;
import com.geology.common.utils.SingleCoordinateTransformUtil;
import com.geology.common.utils.WriteCoordinateTxtUtil;
import com.geology.domain.DTO.RectifyDTO;
import com.geology.domain.DTO.SingleCoordinateTransformDTO;
import lombok.extern.slf4j.Slf4j;
import org.gdal.gdal.*;
import org.gdal.ogr.ogr;
import org.gdal.osr.SpatialReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Vector;
import java.util.stream.IntStream;

@Slf4j
@Service
public class GeoToolsImpl implements GeoTools {

    @Autowired
    private CoordinateTransformUtil coordinateTransform;

    @Autowired
    private WriteCoordinateTxtUtil writeCoordinateTxtUtil;

    @Autowired
    private GenerateGCPsUtil generateGCPsUtil;

    @Autowired
    private SingleCoordinateTransformUtil singleCoordinateTransformUtil;

    @Override
    public TIFInfoBean getTIFInfo(String filename) {
        gdal.AllRegister(); //注册驱动，否则下边执行报错
        Dataset dataset = gdal.Open(filename);
        if (dataset == null) {
            log.info("无法打开文件：" + gdal.GetLastErrorNo());
        }

        // 输出TIF文件信息
        TIFInfoBean tifInfoBean = new TIFInfoBean();
        tifInfoBean.setProjection(dataset.GetProjection());
        tifInfoBean.setBands(dataset.getRasterCount());
        tifInfoBean.setDriver(dataset.GetDriver().getShortName());
        tifInfoBean.setXSize(dataset.getRasterXSize());
        tifInfoBean.setYSize(dataset.getRasterYSize());
        tifInfoBean.setDriverCount(gdal.GetDriverCount());
        tifInfoBean.setGeotransform(dataset.GetGeoTransform());
//        tifInfoBean.setNumberCPUs(gdal.GetNumCPUs());
        // 关闭数据集
        dataset.delete();

        return tifInfoBean;
    }

    @Override
    public String clipImage(String inputRaster, String inputShp, String outputRaster) {
        gdal.AllRegister();

        if (inputRaster == null || inputShp == null || outputRaster == null)
        {
            throw new RuntimeException("Params missing.");
        }
        Dataset source = gdal.Open(inputRaster); //tif文件路径
        Vector vector = new Vector();
        vector.add("-cutline");
        vector.add(inputShp);//shp文件路径
        vector.add("-crop_to_cutline");

        WarpOptions warpAppOptions = new WarpOptions(vector);
        Dataset[] datasets = new Dataset[]{source};
        Dataset output = gdal.Warp(outputRaster, datasets, warpAppOptions);

        output.delete();

        return outputRaster;

    }

    public void testDriver() {
        gdal.AllRegister();
        int count = ogr.GetDriverCount();
        for (int i = 0; i < count; i++) {
            String driverName = ogr.GetDriver(i).getName();
            System.out.print(driverName + "\t");
        }
        gdal.GDALDestroyDriverManager();
    }

    @Override
    public RectifyInfoBean generateGCPsFile(RectifyDTO rectifyDTO) {
        gdal.AllRegister();
        String inputRasterFilePath =  rectifyDTO.getInputRaster();
        Dataset source = gdal.Open(inputRasterFilePath);

        double[] geotransform = source.GetGeoTransform();
        int cols = source.getRasterXSize();
        int rows = source.getRasterYSize();

        int[] XSamples = IntStream.range(0, cols + 1)
                .filter(i -> (i - 0) % (cols/rectifyDTO.getGcpNumbers()) == 0)
                .toArray();

        int[] YSamples = IntStream.range(0, rows + 1)
                .filter(i -> (i - 0) % (rows/rectifyDTO.getGcpNumbers()) == 0)
                .toArray();

        ArrayList<Integer> ori_x_ls = new ArrayList<>();
        ArrayList<Integer> ori_y_ls = new ArrayList<>();
        ArrayList<Double> ori_lon_ls = new ArrayList<>();
        ArrayList<Double> ori_lat_ls = new ArrayList<>();
        ArrayList<Double> gcj_lon_ls = new ArrayList<>();
        ArrayList<Double> gcj_lat_ls = new ArrayList<>();

        for (int i : YSamples)
        {
            for (int j : XSamples)
            {
                double ori_lon = j * geotransform[1] + geotransform[0];
                double ori_lat = i * geotransform[5] + geotransform[3];
                int ori_x = j;
                int ori_y = i;
                ori_x_ls.add(ori_x);
                ori_y_ls.add(ori_y);
                ori_lon_ls.add(ori_lon);
                ori_lat_ls.add(ori_lat);

                CoordinateBean gcjcoordinateBean = coordinateTransform.wgs84togcj02(new CoordinateBean(ori_lon, ori_lat));
                gcj_lon_ls.add(gcjcoordinateBean.getLon());
                gcj_lat_ls.add(gcjcoordinateBean.getLat());
            }
        }

        double[][] resultArray = new double[gcj_lon_ls.size()][4];
        for (int i = 0; i < gcj_lon_ls.size(); i++) {
            resultArray[i][0] = i < ori_lon_ls.size() ? ori_lon_ls.get(i) : 0;
            resultArray[i][1] = i < ori_lat_ls.size() ? ori_lat_ls.get(i) : 0;
            resultArray[i][2] = i < gcj_lon_ls.size() ? gcj_lon_ls.get(i) : 0;
            resultArray[i][3] = i < gcj_lat_ls.size() ? gcj_lat_ls.get(i) : 0;
        }

        String outTxtPath = writeCoordinateTxtUtil.writeCoordinate2Txt(resultArray, rectifyDTO.getOutputCoordinateTxtPath());
        RectifyInfoBean rectifyInfoBean = new RectifyInfoBean(outTxtPath, rectifyDTO.getGcpNumbers());

        return rectifyInfoBean;
    }

    @Override
    public ArrayList<GCP> generateGCPs(RectifyTIFInfoBean rectifyTIFInfoBean) {
        return generateGCPsUtil.generateGCPs(rectifyTIFInfoBean);
    }

    @Override
    public String rectifyByGCP(RectifyTIFInfoBean rectifyTIFInfoBean) {
        gdal.AllRegister();

        String inputPath = rectifyTIFInfoBean.getInputFilePath();
        String outputPath = rectifyTIFInfoBean.getOutputFilePath();

        // 创建GCP列表
        ArrayList<GCP> gcpList = generateGCPsUtil.generateGCPs(rectifyTIFInfoBean);

        // 设置输出文件的投影(WGS84)
        SpatialReference spatialReference = new SpatialReference();
        spatialReference.ImportFromEPSG(4326);

        // 打开原始图像
        Dataset srcDataset = gdal.Open(inputPath);

        // 创建输出图像
        int width = srcDataset.GetRasterXSize();
        int height = srcDataset.GetRasterYSize();
        Dataset dstDataset = srcDataset.GetDriver().Create(outputPath, width, height, 3);

        // 设置输出图像的投影
        dstDataset.SetProjection(spatialReference.ExportToWkt());
        // 设置GCP
        dstDataset.SetGCPs(gcpList.toArray(new GCP[0]), spatialReference.ExportToWkt());
        log.info(String.valueOf(dstDataset.GetGCPCount()));

        // GCP控制点纠正，并写入波段数据
        for (int i = 1; i <= srcDataset.getRasterCount(); i++) {
            Band srcBand = srcDataset.GetRasterBand(i);
            Band dstBand = dstDataset.GetRasterBand(i);
            int[] data = new int[srcDataset.getRasterXSize() * srcDataset.getRasterYSize()];

            // buf_type对应的是gdal的int类型，此处必须和java数组的数据类型(int[])保持一致
            int src1 =  srcBand.ReadRaster(0, 0, width, height,5, data);
            int dst1 = dstBand.WriteRaster(0, 0, width, height,5, data);

        }

        dstDataset.delete();

        return outputPath;
    }

    @Override
    public SingleCoordinateTransformBean singleCoordinateTransform(SingleCoordinateTransformDTO singleCoordinateTransformDTO) {

        SingleCoordinateTransformBean resultSingleCoordinate =  singleCoordinateTransformUtil.singleCoordinateTransform(singleCoordinateTransformDTO);
        return resultSingleCoordinate;
    }
}
