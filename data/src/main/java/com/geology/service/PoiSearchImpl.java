package com.geology.service;

import cn.hutool.json.JSONArray;
import com.geology.common.utils.PaginationUtil;
import lombok.extern.slf4j.Slf4j;
import cn.hutool.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.apache.commons.lang3.StringUtils;
import cn.hutool.json.JSONUtil;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class PoiSearchImpl implements PoiSearch{
    @Autowired
    private RestTemplate restTemplate;

    private static final String POI_URL = "https://restapi.amap.com/v3/place/text?keywords={keywords}&key={key}";

    @Override
    public List<JSONObject> searchPoi(String keywords, String key, String address, int currentPage, int pageSize) {
        if (StringUtils.isBlank(keywords)) {
            return new ArrayList<>();
        }

        String url = POI_URL.replace("{keywords}", keywords).replace("{key}", "eb429bfb7d563d469bc74f51c48cd6d9");
        log.info(url);
        // 发送GET请求并接收响应
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        // 检查响应状态码是否为200（OK）
        if (!response.getStatusCode().is2xxSuccessful()) {
            // 打印响应体
            log.error("Response Body: " + response.getBody());
            return new ArrayList<>();
        }
        // 解析response中字段状态
        String responseBody = response.getBody();
        responseBody = responseBody.trim(); // 去掉首尾空白字符
        responseBody = responseBody.replaceAll("\\p{C}", ""); // 去掉不可见字符
        // 将字符串转换为JSONObject
        log.info(String.valueOf(JSONUtil.isJson(responseBody)));
        JSONObject jsonObject = JSONUtil.parseObj(responseBody);

        // 解析status对象
        String statusObj = jsonObject.getStr("status");
        if (!StringUtils.equals(statusObj, "1")) {
            return new ArrayList<>();
        }

        String dataItems = jsonObject.getStr("pois");
        JSONArray jsonArray = JSONUtil.parseArray(dataItems);
        List<JSONObject> poisList = new ArrayList<>();
        if (!(address == null))
        {
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject jsonObjectItem = jsonArray.getJSONObject(i);
                if(jsonObjectItem.getStr("adname").contains(address))
                {
                    poisList.add(jsonArray.getJSONObject(i));
                }
            }
        }
        else
        {
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject jsonObjectItem = jsonArray.getJSONObject(i);
                poisList.add(jsonArray.getJSONObject(i));
            }
        }

        // 获取分页数据
        List<JSONObject> paginatedList = PaginationUtil.paginate(poisList, currentPage, pageSize);

        return paginatedList;
    }
}
