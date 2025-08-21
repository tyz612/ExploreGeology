package com.geology.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.geology.user.dto.R;
import com.geology.user.pojo.Share;

public interface ShareService extends IService<Share> {
    R<String> shareData(String shareId, String shareType);
}
