package com.geology.common.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.FileWriter;
import java.io.IOException;

@Component
@Slf4j
public class DownloadFileUtil {
    public void writeGeoJsonToFile(String geoJsonData, String filePath) {
        try (FileWriter file = new FileWriter(filePath)) {
            file.write(geoJsonData);
            file.flush();
            log.info("Geojson has been saved to " + filePath + ".");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
