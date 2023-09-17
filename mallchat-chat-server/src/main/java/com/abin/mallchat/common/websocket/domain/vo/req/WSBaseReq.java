package com.abin.mallchat.common.websocket.domain.vo.req;

import lombok.Data;

/**
 * @Author Kkuil
 * @Date 2023/09/17 17:00
 * @Description 
 */
@Data
public class WSBaseReq {
    /**
     * @see com.abin.mallchat.common.websocket.domain.enums.WSReqTypeEnum
     */
    private Integer type;
    private String data;
}
