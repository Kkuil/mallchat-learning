package com.abin.mallchat.common.websocket.domain.vo.req;

import lombok.Data;

/**
 * Description:
 * Author: <a href="https://github.com/zongzibinbin">abin</a>
 * Date: 2023-08-27
 */
@Data
public class WSBaseReq {
    /**
     * @see com.abin.mallchat.common.websocket.domain.enums.WSReqTypeEnum
     */
    private Integer type;
    private String data;
}
