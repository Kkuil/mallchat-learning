package com.abin.mallchat.common.user.domain.vo.resp;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Description:
 * Author: <a href="https://github.com/zongzibinbin">abin</a>
 * Date: 2023-09-08
 */
@Data
public class BadgeResp {
    @ApiModelProperty(value = "徽章id")
    private Long id;
    @ApiModelProperty(value = "徽章图标")
    private String img;
    @ApiModelProperty(value = "徽章描述")
    private String describe;
    @ApiModelProperty(value = "是否拥有 0否 1是")
    private Integer obtain;
    @ApiModelProperty(value = "是否佩戴 0否 1是")
    private Integer wearing;
}
