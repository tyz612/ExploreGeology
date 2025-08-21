package com.geology.user.dto.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.criteria.CriteriaBuilder;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShareDataUpdateVO {

    private String contactId;

    private String dataId;

    private Integer dataType;

    private Integer status;
}
