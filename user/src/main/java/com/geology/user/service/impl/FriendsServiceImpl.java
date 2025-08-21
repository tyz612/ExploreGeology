package com.geology.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
//import com.mh.dao.FriendsDao;
import com.geology.user.common.ErrorCode;
import com.geology.user.dao.FriendsDao;
import com.geology.user.dao.ShareDao;
import com.geology.user.dao.UserInfoDao;
import com.geology.user.dto.R;
import com.geology.user.dto.vo.ApplyUpdateAgrsVo;
import com.geology.user.dto.vo.FriendsInfoDto;
import com.geology.user.dto.vo.ShareDataInfoDTO;
import com.geology.user.dto.vo.ShareDataUpdateVO;
import com.geology.user.exception.BusinessException;
import com.geology.user.jwt.AuthStorage;
import com.geology.user.jwt.JwtUser;
import com.geology.user.pojo.Friends;
import com.geology.user.pojo.Share;
import com.geology.user.pojo.UserInfo;
import com.geology.user.service.FriendsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * (Friends)表服务实现类
 *
 * @author zmh
 * @since 2024-09-10 16:51:11
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class FriendsServiceImpl extends ServiceImpl<FriendsDao, Friends> implements FriendsService {

    private final FriendsDao friendsDao;

    @Autowired
    private final UserInfoDao userInfoDao;

    @Autowired
    private final ShareDao shareDao;

    /**
     * 分页查询
     *
     * @param page 查询页数
     * @param size 一页显示条数
     * @return ·
     */
    public R<Page<Friends>> getAllByPage(int page, int size) {
        Page<Friends> friendsPage = new Page<>(page, size);
        LambdaQueryWrapper<Friends> queryWrapper = new LambdaQueryWrapper<>();
        //TODO 查询条件定制
        return R.success(friendsDao.selectPage(friendsPage, queryWrapper));
    }

    // 好友表添加
    @Override
    public R<String> addFriend(String beAddId) {
        JwtUser user = AuthStorage.getUser();
        String addId = user.getUserId();


        // 检查id是否存在
        LambdaQueryWrapper<UserInfo> userInfoLambdaQueryWrapper = new LambdaQueryWrapper<>();
        userInfoLambdaQueryWrapper.eq(UserInfo::getId, Long.parseLong(beAddId));

        List<UserInfo> userInfos = userInfoDao.selectList(userInfoLambdaQueryWrapper);

        if (userInfos.size() == 0)
        {
            return R.error("该用户不存在");
        }

        // 检查添加被添加用户是否已经添加了自己
        LambdaQueryWrapper<Friends> qw = new LambdaQueryWrapper<>();
        qw.eq(Friends::getUserId, beAddId).eq(Friends::getContactId, addId)
                .eq(Friends::getState, 0);
        Friends preFriendInfo = friendsDao.selectOne(qw);

        // 如果对方已经添加了我们，则仅需要去修改双方两条记录状态变为1即可。
        if(preFriendInfo != null){
            // 修改双方两条记录状态
            friendsDao.updateState(addId, beAddId,1);
            friendsDao.updateState(beAddId, addId, 1);
            return R.success("消息发送成功");
        }

        // === 如果对象没有添加我们，则需要创建出添加和被添加记录 ===

        // 根据主动添加ID和被动添加ID，向数据库中为双方添加一个好友记录，使用state区分主被动关系
        // 检查是否存在 “重复添加”的操作
        LambdaQueryWrapper<Friends> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Friends::getUserId, addId).eq(Friends::getContactId, beAddId)
                .in(Friends::getState, 0,1);
        Friends friendInfo = friendsDao.selectOne(queryWrapper);

        if(friendInfo != null){
            return R.error("请勿重复添加该用户");
        }

        // 构造对象1：主动添加信息
        Friends addUer = new Friends(null, addId, beAddId, 0);
        Friends beAddUser = new Friends(null, beAddId, addId, 2);
        friendsDao.insert(addUer);
        friendsDao.insert(beAddUser);
        return R.success("消息发送成功");
    }

    @Override
    public R<UserInfo> searchFriend(String keyword) {
        // 验证输入
        if (StringUtils.isBlank(keyword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "搜索关键词不能为空");
        }

        // 构建查询条件
        LambdaQueryWrapper<UserInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserInfo::getEmail, keyword)
                .or()
                .eq(UserInfo::getPhone, keyword)
                .or()
                .eq(UserInfo::getUserName, keyword);

        // 执行查询
        UserInfo userInfo = userInfoDao.selectOne(wrapper);

        // 处理结果
        if (userInfo == null) {
            return R.error("未找到该用户");
        }

        return R.success(userInfo);
    }

    /**
     * 获取申请的好友列表 - 在好友列表根据当前用户ID进行过滤，及状态为2的数据列表
     * @return 申请的好友列表DTO对象
     */
    @Override
    public R<List<FriendsInfoDto>> getApplyFriendsInfo() {
        JwtUser user = AuthStorage.getUser();
        String userId = user.getUserId();


        // 构造条件进行查询
        LambdaQueryWrapper<Friends> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Friends::getUserId, userId).eq(Friends::getState, 2);
        List<Friends> applyFriendsInfo = friendsDao.selectList(queryWrapper);

        // 鉴空
        if(applyFriendsInfo.size() == 0){
            return R.error("暂无好友申请!");
        }

        // 取出所有申请的好友ID，构造ID列表进行批量查询申请的用户ID
        HashSet<Long> ids = new HashSet<>();
        for (Friends friends : applyFriendsInfo) {
//            ids.add(Long.parseLong(friends.getContactId()));
            ids.add(Long.parseLong(friends.getContactId()));
        }

        // 批量获取申请的用户信息
        LambdaQueryWrapper<UserInfo> userInfoLambdaQueryWrapper = new LambdaQueryWrapper<>();
        userInfoLambdaQueryWrapper.in(UserInfo::getId, ids);

        List<UserInfo> userInfos = userInfoDao.selectList(userInfoLambdaQueryWrapper);

        // 构造返回的DTO列表
        ArrayList<FriendsInfoDto> returnApplyUserInfos = new ArrayList<>();
        for (UserInfo userInfo : userInfos) {
            returnApplyUserInfos.add(new FriendsInfoDto(userInfo.getId().toString(), userInfo.getUserName(), userInfo.getEmail(), userInfo.getAvatarUrl()));
        }

        return R.success(returnApplyUserInfos);
    }

    /**
     * 处理器-申请处理结果-同意或拒绝
     * @param applyUpdateAgrsVo 处理好友申请的参数对象
     * @return ·
     */
    @Override
    public R<String> updateFriendStatus(ApplyUpdateAgrsVo applyUpdateAgrsVo) {
        JwtUser user = AuthStorage.getUser();
        String userId = user.getUserId();

//        applyUpdateAgrsVo.setUserId(userId);


        // 根据操作类型（1或3），同时修改两条记录
        friendsDao.updateState(userId, applyUpdateAgrsVo.getContactId(), applyUpdateAgrsVo.getStatus());
        friendsDao.updateState(applyUpdateAgrsVo.getContactId(), userId, applyUpdateAgrsVo.getStatus());

        return R.success("修改成功");
    }

    /**
     * 获取好友列表
     * @return 好友信息Dto列表（包含用户ID， 用户名称，用户邮箱）
     */
    @Override
    public R<List<UserInfo>> getFriendsInfoByUserId() {
        JwtUser user = AuthStorage.getUser();
        String userId = user.getUserId();

        // 过滤出好友状态为1（已通过）的好友列表
        LambdaQueryWrapper<Friends> eq = new LambdaQueryWrapper<Friends>().eq(Friends::getUserId, userId).eq(Friends::getState, 1);
        List<Friends> friends = friendsDao.selectList(eq);

        if(friends.size() == 0){return R.error("该用户没有添加任何好友");}

        // 构造申请者ID
        HashSet<Long> ids = new HashSet<>();
        for (Friends friend : friends) {
            ids.add(Long.parseLong(friend.getContactId()));
        }

        // 遍历构造返回DTO信息对象列表
        LambdaQueryWrapper<UserInfo> in = new LambdaQueryWrapper<UserInfo>().in(UserInfo::getId, ids);
        List<UserInfo> userInfos = userInfoDao.selectList(in);
        return userInfos.size()>0?R.success(userInfos):R.error("该用户没有好友");
    }

    @Override
    public R<List<ShareDataInfoDTO>> getSharedDataInfo() {
        JwtUser user = AuthStorage.getUser();
        String userId = user.getUserId();


        // 构造条件进行查询
        LambdaQueryWrapper<Share> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Share::getContactId, userId).eq(Share::getStatus, 2);
        List<Share> shareDataInfos = shareDao.selectList(queryWrapper);

        // 鉴空
        if(shareDataInfos.size() == 0){
            return R.error("暂无好友分享数据!");
        }

        // 构造返回的DTO列表
        ArrayList<ShareDataInfoDTO> returnShareDataInfos = new ArrayList<>();
        for (Share share : shareDataInfos) {
            returnShareDataInfos.add(new ShareDataInfoDTO(share.getUserId().toString(), share.getUserName(), share.getAvatar(), share.getDataType(), share.getDataName(), share.getContactId().toString(), share.getDataId()));
        }

        return R.success(returnShareDataInfos);
    }

    @Override
    public R<String> shareData(String friendId, String dataId, Integer dataType, String dataName) {
        JwtUser user = AuthStorage.getUser();
        String userId = user.getUserId();
        // 检查id是否存在
        LambdaQueryWrapper<UserInfo> currentUserInfoLambdaQueryWrapper = new LambdaQueryWrapper<>();
        currentUserInfoLambdaQueryWrapper.eq(UserInfo::getId, Long.parseLong(userId));
        UserInfo userInfo = userInfoDao.selectOne(currentUserInfoLambdaQueryWrapper);

        String userName = userInfo.getUserName();
        String avatar = userInfo.getAvatarUrl();


        // 检查id是否存在
        LambdaQueryWrapper<UserInfo> userInfoLambdaQueryWrapper = new LambdaQueryWrapper<>();
        userInfoLambdaQueryWrapper.eq(UserInfo::getId, Long.parseLong(friendId));

        List<UserInfo> userInfos = userInfoDao.selectList(userInfoLambdaQueryWrapper);

        if (userInfos.size() == 0)
        {
            return R.error("该用户不存在");
        }

        // 构造对象1：主动添加信息
        Share share = new Share(null, userId, friendId, dataId, dataType, 0, dataName, userName, 2, avatar);
        shareDao.insert(share);
        log.info(userId.concat(" 分享数据 ").concat(dataId).concat(" 到 ").concat(friendId));
        return R.success("数据分享成功");
    }

    @Override
    public R<String> updateShareDataStatus(ShareDataUpdateVO shareDataUpdateVO) {
        JwtUser user = AuthStorage.getUser();
        String userId = user.getUserId();

        // 根据操作类型（1或3），同时修改两条记录
        shareDao.updateStatus(shareDataUpdateVO.getStatus(), shareDataUpdateVO.getDataId());

        return R.success("修改成功");
    }


}

