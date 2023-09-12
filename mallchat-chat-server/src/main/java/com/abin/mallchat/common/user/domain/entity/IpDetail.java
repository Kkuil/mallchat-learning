package com.abin.mallchat.common.user.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.io.Serializable;

/**
 * Description:
 * Author: <a href="https://github.com/zongzibinbin">abin</a>
 * Date: 2023-09-10
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class IpDetail implements Serializable {
    private String ip;
    private String isp;
    private String isp_id;
    private String city;
    private String city_id;
    private String country;
    private String county_id;
    private String region;
    private String region_id;
}
