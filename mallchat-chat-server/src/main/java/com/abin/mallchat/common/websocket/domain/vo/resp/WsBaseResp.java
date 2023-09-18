package com.abin.mallchat.common.websocket.domain.vo.resp;

import com.abin.mallchat.common.websocket.domain.enums.WsRespTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author Kkuil
 * @Date 2023/09/17 17:00
 * @Description websocket基础返回json对象结构
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class WsBaseResp<T> {
    /**
     * 返回类型
     *
     * @see WsRespTypeEnum
     */
    private Integer type;

    /**
     * 返回的数据
     */
    private T data;
}
