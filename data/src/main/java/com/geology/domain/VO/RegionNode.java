package com.geology.domain.VO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegionNode {
    private Integer id;
    private String  name;
    private Boolean hasChildren;   // 前端好判断要不要继续点
}