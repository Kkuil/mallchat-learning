package com.abin.mallchat.common.user.domain.vo.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * Description:
 * Author: <a href="https://github.com/zongzibinbin">abin</a>
 * Date: 2023-09-09
 */
@Data
public class BlackReq {
    @ApiModelProperty("拉黑用户的uid")
    @NotNull
    private Long uid;
}
