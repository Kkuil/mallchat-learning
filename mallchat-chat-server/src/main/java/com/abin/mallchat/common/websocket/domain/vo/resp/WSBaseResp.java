package com.abin.mallchat.common.websocket.domain.vo.resp;

/**
 * Description:
 * Author: <a href="https://github.com/zongzibinbin">abin</a>
 * Date: 2023-08-27
 */
public class WSBaseResp<T> {
    /**
     * @see com.abin.mallchat.common.websocket.domain.enums.WSRespTypeEnum
     */
    private Integer type;
    private T data;
}
