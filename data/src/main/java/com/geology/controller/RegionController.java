package com.geology.controller;

import com.geology.common.ApiResponse;
import com.geology.domain.VO.RegionNode;
import com.geology.domain.bean.CountyBean;
import com.geology.domain.bean.SingleFileGeologyType;
import com.geology.domain.bean.TrackGeomBean;
import com.geology.service.RegionService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/region")
@RequiredArgsConstructor
public class RegionController {

//    private final RegionService regionService;

    @Autowired
    private RegionService regionService;

    @GetMapping("/province")
    public List<RegionNode> province(){
        return regionService.listProvince();
    }

    @GetMapping("/city")
    public List<RegionNode> city(@RequestParam Integer provinceId){
        return regionService.listCity(provinceId);
    }

    @GetMapping("/county")
    public List<RegionNode> county(@RequestParam Integer cityId){
        return regionService.listCounty(cityId);
    }


    @CrossOrigin(origins = "https://geologymine.fun", allowCredentials = "true")
    @GetMapping("/getCountyByCode")
    public ApiResponse<CountyBean> getCountyByCode(@RequestParam("gb") String gb) {
        CountyBean countyBean = regionService.getCountyGeomByCode(gb);

        return ApiResponse.success(countyBean);
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/getMineGeologyFileByCountyCode")
    public ApiResponse<SingleFileGeologyType> getMineGeologyFileByCountyCode(@RequestParam("gb") String gb) {
        SingleFileGeologyType singleFileGeologyType = regionService.getMineGeologyFileByCountyCode(gb);

        return ApiResponse.success(singleFileGeologyType);
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/getFaultGeologyFileByCountyCode")
    public ApiResponse<SingleFileGeologyType> getFaultGeologyFileByCountyCode(@RequestParam("gb") String gb) {
        SingleFileGeologyType singleFileGeologyType = regionService.getFaultGeologyFileByCountyCode(gb);

        return ApiResponse.success(singleFileGeologyType);
    }
}
