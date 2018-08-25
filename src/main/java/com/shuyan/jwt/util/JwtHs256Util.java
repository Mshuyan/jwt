package com.shuyan.jwt.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 使用 java-jwt 完成 HS256 对称加密
 * @author will
 */
public class JwtHs256Util {

    @Test
    public void test(){
        verify(getJWT());
    }

    /**
     * 用于加密
     * header 字段自动生成，无须自己设置
     */
    public static String getJWT(){

        Algorithm algorithm = Algorithm.HMAC256("secret");
        return JWT.create()
                // 在header中加1个kid字段，不知道干嘛的，可以忽略
                .withKeyId("456")
                // 设置7个标准声名
                .withIssuer("viathink")
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + 3600000))
                .withJWTId("123")
                .withNotBefore(new Date(System.currentTimeMillis() - 60000))
                .withSubject("auth")
                .withAudience("chrome")
                // 设置自定义声名
                .withClaim("username","shuyan")
                // 签名
                .sign(algorithm);
    }

    public static void verify(String token){
        Algorithm algorithm = Algorithm.HMAC256("secret");
        JWTVerifier verifier = JWT.require(algorithm)
                // 验证 iss
                .withIssuer("viathink")
                .build();
        DecodedJWT jwt = verifier.verify(token);
        System.out.println(jwt.getToken());
    }
}
