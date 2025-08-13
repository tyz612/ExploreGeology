package com.geology.user.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.geology.user.dto.R;
import com.geology.user.dto.vo.ApplyUpdateAgrsVo;
import com.geology.user.dto.vo.FriendsInfoDto;
import com.geology.user.model.domain.User;
import com.geology.user.pojo.Friends;
import com.geology.user.pojo.UserInfo;

import java.util.List;

/**
 * (Friends)表服务接口
 *
 * @author zmh
 * @since 2024-09-10 16:51:10
 */
public interface FriendsService extends IService<Friends> {

    /**
     * 分页查询
     *
     * @param page 查询页数
     * @param size 一页显示条数
     * @return ·
     */
    R<Page<Friends>> getAllByPage(int page, int size);

    // 好友表添加
    R<String> addFriend(String beAddId);

    R<UserInfo> searchFriend(String keyword);

    // 获取好友“申请”列表
    R<List<FriendsInfoDto>> getApplyFriendsInfo();

    // 好友对于申请的操作 - 对于【好友申请】，同意或拒绝
    R<String> updateFriendStatus(ApplyUpdateAgrsVo applyUpdateAgrsVo);

    R<List<UserInfo>> getFriendsInfoByUserId();

}

