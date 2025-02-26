package com.geology.service;

import cn.hutool.json.JSONObject;

import java.util.List;

public interface PoiSearch {
    List<JSONObject> searchPoi(String keywords, String key, String address, int currentPage, int pageSize);
}
