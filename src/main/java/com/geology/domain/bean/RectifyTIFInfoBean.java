package com.geology.domain.bean;

import lombok.Data;

@Data
public class RectifyTIFInfoBean {
    private String inputFilePath;

    private Integer gcpNumbers;

    private String outputFilePath;

//    public RectifyTIFInfoBean(String inputFilePath, Integer gcpNumbers, String outputFilePath) {
//        this.inputFilePath = inputFilePath;
//        this.gcpNumbers = gcpNumbers;
//        this.outputFilePath = outputFilePath;
//    }
}
