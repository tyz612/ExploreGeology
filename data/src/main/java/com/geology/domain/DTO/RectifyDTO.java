package com.geology.domain.DTO;

import lombok.Data;

@Data
public class RectifyDTO {
    private String inputRaster;

    private String outputPath;

    private Integer srcCRS;

    private Integer destCRS;

    private Integer gcpNumbers;

    private String outputCoordinateTxtPath;
}
