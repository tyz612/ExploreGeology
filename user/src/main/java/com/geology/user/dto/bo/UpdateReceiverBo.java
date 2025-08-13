package com.geology.user.dto.bo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Date:2024/9/9
 * author:zmh
 * description: 更新Redis中接收者信息Bo对象
 **/

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateReceiverBo {

    // 接收者ID
    private String receiverId;
    // 接收者名称
    private String name;
    // 标志 add为新增，del为删除
    private String ops;
}
