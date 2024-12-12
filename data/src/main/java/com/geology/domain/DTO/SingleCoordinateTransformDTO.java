package com.geology.domain.DTO;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
public class SingleCoordinateTransformDTO {
    private double oriLon;

    private double oriLat;

    private Integer srcSRS;

    private Integer dstSRS;
}
