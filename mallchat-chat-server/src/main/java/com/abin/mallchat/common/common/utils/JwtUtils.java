package com.abin.mallchat.common.common.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;
import java.util.Optional;

/**
 * @Author Kkuil
 * @Date 2023/9/18
 * @Description jwt的token生成与解析
 */
@Slf4j
@Component
public class JwtUtils {

    /**
     * token秘钥，请勿泄露，请勿随便修改
     */
    @Value("${mallchat.jwt.secret}")
    private String secret;

    private static final String UID_CLAIM = "uid";
    private static final String CREATE_TIME = "createTime";

    /**
     * 创建token
     *
     * @param uid 用户ID
     * @return token
     */
    public String createToken(Long uid) {
        // 构建token
        return JWT.create()
                .withClaim(UID_CLAIM, uid)
                .withClaim(CREATE_TIME, new Date())
                .sign(Algorithm.HMAC256(secret));
    }

    /**
     * 解密Token
     *
     * @param token token
     * @return 解密信息
     */
    public Map<String, Claim> verifyToken(String token) {
        if (StringUtils.isEmpty(token)) {
            return null;
        }
        try {
            JWTVerifier verifier = JWT.require(Algorithm.HMAC256(secret)).build();
            DecodedJWT jwt = verifier.verify(token);
            return jwt.getClaims();
        } catch (Exception e) {
            log.error("decode error,token:{}", token, e);
        }
        return null;
    }


    /**
     * 根据Token获取uid
     *
     * @param token token
     * @return 用户ID
     */
    public Long getUidOrNull(String token) {
        return Optional.ofNullable(verifyToken(token))
                .map(map -> map.get(UID_CLAIM))
                .map(Claim::asLong)
                .orElse(null);
    }

}
