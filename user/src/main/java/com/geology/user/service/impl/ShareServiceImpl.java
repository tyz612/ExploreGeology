package com.geology.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.geology.user.dto.R;
import com.geology.user.pojo.Share;
import com.geology.user.service.ShareService;

import java.util.Collection;
import java.util.Map;
import java.util.function.Function;

public class ShareServiceImpl implements ShareService {
    @Override
    public R<String> shareData(String shareId, String shareType) {
        return null;
    }

    @Override
    public boolean saveBatch(Collection<Share> entityList, int batchSize) {
        return false;
    }

    @Override
    public boolean saveOrUpdateBatch(Collection<Share> entityList, int batchSize) {
        return false;
    }

    @Override
    public boolean updateBatchById(Collection<Share> entityList, int batchSize) {
        return false;
    }

    @Override
    public boolean saveOrUpdate(Share entity) {
        return false;
    }

    @Override
    public Share getOne(Wrapper<Share> queryWrapper, boolean throwEx) {
        return null;
    }

    @Override
    public Map<String, Object> getMap(Wrapper<Share> queryWrapper) {
        return null;
    }

    @Override
    public <V> V getObj(Wrapper<Share> queryWrapper, Function<? super Object, V> mapper) {
        return null;
    }

    @Override
    public BaseMapper<Share> getBaseMapper() {
        return null;
    }

    @Override
    public Class<Share> getEntityClass() {
        return null;
    }
}
