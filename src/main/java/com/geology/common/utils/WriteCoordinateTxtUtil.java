package com.geology.common.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

@Component
@Slf4j
public class WriteCoordinateTxtUtil {

    /**
     * 写出控制点文件，可直接在ArcGIS中加载-配准
     *
     * @param refCoordinates 控制点二维数组（前两列为WGS84, 后两列为GCJ02）
     * @param outputPath 控制点文件的输出路径
     * @return 保存文件的路径
     */
    public String writeCoordinate2Txt(double[][] refCoordinates, String outputPath) {
        // 指定输出文件的路径和文件名
        String filePath = outputPath;

        // 使用 try-with-resources 语句来自动关闭资源
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            // 遍历二维数组并写入文件
            for (double[] row : refCoordinates) {
                for (double value : row) {
                    writer.write(value + " "); // 写入值，并在值之间留有空格
                }
                writer.newLine(); // 每写完一行后换行
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return outputPath;
    }
}
