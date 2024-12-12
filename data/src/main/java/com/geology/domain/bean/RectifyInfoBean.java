package com.geology.domain.bean;

import lombok.Data;

@Data
public class RectifyInfoBean {
    private String outputPath;

    private Integer gcpNumbers;

    public RectifyInfoBean(String outputPath, Integer gcpNumbers) {
        this.outputPath = outputPath;
        this.gcpNumbers = gcpNumbers;
    }
}
