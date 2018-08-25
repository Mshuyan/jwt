package com.shuyan.jwt.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 使用 jjwt 完成 HS256 对称加密
 * @author will
 */
public class JjwtHs256 {
    @Test
    public void test(){
        verify(getJWT());
    }

    /**
     * 用于加密
     * header 字段自动生成，无须自己设置
     */
    public static String getJWT(){

        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
        Map<String,Object> map = new HashMap<>(1024);
        map.put("name","alan");
        JwtBuilder jwtBuilder = Jwts.builder()
                // 设置自定义声名（这份方法必须放前面，否则会将前面设置的内容覆盖掉）
                .setClaims(map)
                // 设置7个标准声名
                .setIssuer("viathink")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 3600000))
                .setId("123")
                .setNotBefore(new Date(System.currentTimeMillis() - 60000))
                .setSubject("auth")
                .setAudience("chrome")
                // 签名
                .signWith(signatureAlgorithm, "secret");
        return jwtBuilder.compact();
    }

    public static void verify(String token){
        System.out.println(token);
        Claims claims = Jwts.parser()
                .setSigningKey("secret")
                .requireIssuer("viathink")
                .parseClaimsJws(token).getBody();
        System.out.println(claims.toString());
    }
}