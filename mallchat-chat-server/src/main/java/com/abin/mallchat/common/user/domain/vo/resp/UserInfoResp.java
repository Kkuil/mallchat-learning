package com.abin.mallchat.common.user.domain.vo.resp;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Description:
 * Author: <a href="https://github.com/zongzibinbin">abin</a>
 * Date: 2023-09-08
 */
@Data
public class UserInfoResp {
    @ApiModelProperty(value = "uid")
    private Long id;
    @ApiModelProperty(value = "用户头像")
    private String avatar;
    @ApiModelProperty(value = "用户名称")
    private String name;
    @ApiModelProperty(value = "用户性别 1为男性，2为女性")
    private Integer sex;
    @ApiModelProperty(value = "剩余的改名次数")
    private Integer modifyNameChance;
}
