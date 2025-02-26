package com.geology.common.utils;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class PaginationUtil {
    /**
     * 对列表进行分页处理
     * @param list 原始列表
     * @param currentPage 当前页码（从1开始）
     * @param pageSize 每页显示的记录数
     * @param <T> 列表中的元素类型
     * @return 分页后的列表
     */
    public static <T> List<T> paginate(List<T> list, int currentPage, int pageSize) {
        if (list == null || list.isEmpty()) {
            return new ArrayList<>();
        }

        int totalRecords = list.size();
        int startIndex = (currentPage - 1) * pageSize;
        int endIndex = Math.min(startIndex + pageSize, totalRecords);

        if (startIndex >= totalRecords) {
            return new ArrayList<>();
        }

        return list.subList(startIndex, endIndex);
    }

    /**
     * 计算总页数
     * @param totalRecords 总记录数
     * @param pageSize 每页显示的记录数
     * @return 总页数
     */
    public static int getTotalPages(int totalRecords, int pageSize) {
        return (int) Math.ceil((double) totalRecords / pageSize);
    }
}
