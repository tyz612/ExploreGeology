package com.geology.user.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.geology.user.dto.R;
import com.geology.user.dto.vo.ApplyUpdateAgrsVo;
import com.geology.user.dto.vo.FriendsInfoDto;
import com.geology.user.dto.vo.ShareDataInfoDTO;
import com.geology.user.dto.vo.ShareDataUpdateVO;
import com.geology.user.pojo.Friends;
import com.geology.user.pojo.UserInfo;
import com.geology.user.service.FriendsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;
import java.util.List;

/**
 * (Friends)表控制层
 *
 * @author zmh
 * @since 2024-09-10 16:51:10
 */
@RestController
@Slf4j
@CrossOrigin
@RequestMapping("/friendss")
public class FriendsController {
    /**
     * 服务对象
     */
    @Autowired
    private FriendsService friendsService;

    /**
     * 分页查询
     *
     * @param page 查询页数
     * @param size 一页显示条数
     * @return ·
     */
    @GetMapping("/page")
    public R<Page<Friends>> getAllByPage(int page, int size) {
        //执行查询
        return friendsService.getAllByPage(page, size);
    }

    /**
     * 通过主键查询单条数据
     *
     * @param id 主键
     * @return 单条数据
     */
    @GetMapping("{id}")
    public R<Friends> selectOne(@PathVariable Serializable id) {
        return R.success(this.friendsService.getById(id));
    }

    /**
     * 新增数据
     *
     * @param friends 实体对象
     * @return 新增结果
     */
    @PostMapping
    public R<String> insert(@RequestBody Friends friends) {
        return R.success(this.friendsService.save(friends) + "");
    }

    /**
     * 添加好友
     * @param beAddId 好友ID
     * @return 执行状态
     */
    @GetMapping("/addFriend")
    public R<String> addFriend(@RequestParam("beAddId") String beAddId){
        return friendsService.addFriend(beAddId);
    }


    /**
     * 分享数据
     * @return 执行状态
     */
    @GetMapping("/shareData")
    public R<String> shareData(@RequestParam("friendId") String friendId,
                               @RequestParam("dataId") String dataId,
                               @RequestParam("dataType") Integer dataType,
                               @RequestParam("dataName") String dataName){
        return friendsService.shareData(friendId,dataId,dataType,dataName);
    }



    /**
     * 获取申请的好友列表 - 在好友列表根据当前用户ID进行过滤，及状态为2的数据列表
     * @return 申请的好友列表DTO对象
     */
    @GetMapping("/getApplyFriendsInfo")
    public R<List<FriendsInfoDto>> getApplyFriendsInfo(){
        return friendsService.getApplyFriendsInfo();
    }


    /**
     * 获取申请的好友列表 - 在好友列表根据当前用户ID进行过滤，及状态为2的数据列表
     * @return 申请的好友列表DTO对象
     */
    @GetMapping("/getSharedDataInfo")
    public R<List<ShareDataInfoDTO>> getSharedDataInfo(){
        return friendsService.getSharedDataInfo();
    }

    @GetMapping("/searchUser")
    public R<UserInfo> searchUser(@RequestParam("keyword") String keyword){
        return friendsService.searchFriend(keyword);
    }

    /**
     * 修改数据
     *
     * @param friends 实体对象
     * @return 修改结果
     */
    @PutMapping
    public R<String> update(@RequestBody Friends friends) {
        return R.success(this.friendsService.updateById(friends) + "");
    }

    /**
     * 处理器-申请处理结果-同意或拒绝
     * @param applyArgs 处理好友申请参数
     * @return ·
     */
    @PostMapping("/updateFriendStatus")
    public R<String> updateFriendStatus(@RequestBody ApplyUpdateAgrsVo applyArgs) {
        return friendsService.updateFriendStatus(applyArgs);
    }

    /**
     * 处理器-申请处理结果-同意或拒绝
     * @param shareDataUpdateVO 处理好友申请参数
     * @return ·
     */
    @PostMapping("/updateShareDataStatus")
    public R<String> updateShareDataStatus(@RequestBody ShareDataUpdateVO shareDataUpdateVO) {
        return friendsService.updateShareDataStatus(shareDataUpdateVO);
    }

    /**
     * 获取好友列表
     * @return 好友信息Dto列表（包含用户ID， 用户名称，用户邮箱）
     */
    @GetMapping("/getFriendsInfoByUserId")
    public R<List<UserInfo>> getFriendsInfoByUserId() {
        return friendsService.getFriendsInfoByUserId();
    }

    /**
     * 删除数据
     *
     * @param id 主键结合
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public R<String> delete(@PathVariable("id") String id) {
        return R.success(this.friendsService.removeById(id) + "");
    }
}

