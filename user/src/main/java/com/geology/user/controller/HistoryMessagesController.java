package com.geology.user.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.geology.user.dto.MessageDTO;
import com.geology.user.dto.R;
import com.geology.user.pojo.HistoryMessages;
import com.geology.user.service.HistoryMessagesService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;
import java.util.List;


/**
 * 历史消息表(HistoryMessages)表控制层
 *
 * @author zmh
 * @since 2024-09-02 17:22:05
 */
@RestController
@Slf4j
@CrossOrigin
@RequestMapping("/historyMessagess")
public class HistoryMessagesController {
    /**
     * 服务对象
     */
    @Autowired
    private HistoryMessagesService historyMessagesService;

    /**
     * 传入用户id和最后一条本地聊天记录的时间戳，对比获取未同步的增量数据
     * @param userId 用户id
     * @param lastTimestamp 最后一条本地本天记录的时间戳
     * @return 用户有关的聊天记录
     */
    @GetMapping("/increment/{userId}/{lastTimestamp}")
    R<List<HistoryMessages>> getIncrementByTimestamp(@PathVariable String userId,@PathVariable String lastTimestamp){
        return historyMessagesService.getIncrementByTimestamp(userId, lastTimestamp);
    }

    /**
     * 分页查询
     *
     * @param page 查询页数
     * @param size 一页显示条数
     * @return ·
     */
    @GetMapping("/page")
    public R<Page<HistoryMessages>> getAllByPage(int page, int size) {
        //执行查询
        return historyMessagesService.getAllByPage(page, size);
    }

    /**
     * 通过主键查询单条数据
     *
     * @param id 主键
     * @return 单条数据
     */
    @GetMapping("{id}")
    public R<HistoryMessages> selectOne(@PathVariable Serializable id) {
        return R.success(this.historyMessagesService.getById(id));
    }

    /**
     * 保存聊天信息到数据库
     *
     * @return 单条消息
     */
    @PostMapping("/saveMessage")
    public R<String> saveMessage(@RequestBody MessageDTO messageDTO) {
        String savedMessage = historyMessagesService.saveMessage(messageDTO.getReceiver(), messageDTO.getMessage());

        return R.success(savedMessage);
    };


    /**
     * 保存聊天信息到数据库
     *
     * @return 单条消息
     */
    @PostMapping("/saveShareMessage")
    public R<String> saveShareMessage(@RequestBody MessageDTO messageDTO) {
        String savedMessage = historyMessagesService.saveMessage(messageDTO.getReceiver(), messageDTO.getMessage());

        return R.success(savedMessage);
    }


    @GetMapping("/getHistoryMessages")
    public R<List<HistoryMessages>> getHistoryMessages(@RequestParam("receiver") String receiver) {
        //执行查询
        List<HistoryMessages> historyMessages =  historyMessagesService.getHistoryMessagesById(receiver);

        return R.success(historyMessages);
    }

    /**
     * 新增数据
     *
     * @param historyMessages 实体对象
     * @return 新增结果
     */
    @PostMapping
    public R<String> insert(@RequestBody HistoryMessages historyMessages) {
        return R.success(this.historyMessagesService.save(historyMessages) + "");
    }

    /**
     * 修改数据
     *
     * @param historyMessages 实体对象
     * @return 修改结果
     */
    @PutMapping
    public R<String> update(@RequestBody HistoryMessages historyMessages) {
        return R.success(this.historyMessagesService.updateById(historyMessages) + "");
    }

    /**
     *
     * 删除数据
     *
     * @param id 主键结合
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public R<String> delete(@PathVariable("id") String id) {
        return R.success(this.historyMessagesService.removeById(id) + "");
    }
}

