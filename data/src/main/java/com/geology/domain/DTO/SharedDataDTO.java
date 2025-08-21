package com.geology.domain.DTO;

import lombok.Data;

@Data
public class SharedDataDTO {
    private String fromUserId;

    private String receiveUserId;

    private String dataId;

    private Integer dataType;
}
