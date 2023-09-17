package com.abin.mallchat.common.user.domain.vo.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @Author Kkuil
 * @Date 2023/09/17 17:00
 * @Description 
 */
@Data
public class BlackReq {
    @ApiModelProperty("拉黑用户的uid")
    @NotNull
    private Long uid;
}
