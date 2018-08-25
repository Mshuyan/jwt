package com.shuyan.jwt.util;

import com.sun.org.apache.bcel.internal.generic.NEW;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.apache.commons.codec.binary.Base64;
import org.junit.jupiter.api.Test;
import sun.misc.BASE64Decoder;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 使用 jjwt 完成 RS256 非对称加密
 * @author will
 */
public class JjwtRs256 {
    public static String str_pubK = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCqPvovSfXcwBbW8cKMCgwqNpsYuzF8RPAPFb7LGsnVo44JhM/xxzDyzoYtdfNmtbIuKVi9PzIsyp6rg+09gbuI6UGwBZ5DWBDBMqv5MPdOF5dCQkB2Bbr5yPfURPENypUz+pBFBg41d+BC+rwRiXELwKy7Y9caD/MtJyHydj8OUwIDAQAB";
    public static String str_priK = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAKo++i9J9dzAFtbxwowKDCo2mxi7MXxE8A8VvssaydWjjgmEz/HHMPLOhi1182a1si4pWL0/MizKnquD7T2Bu4jpQbAFnkNYEMEyq/kw904Xl0JCQHYFuvnI99RE8Q3KlTP6kEUGDjV34EL6vBGJcQvArLtj1xoP8y0nIfJ2Pw5TAgMBAAECgYAGGB8IllMwxceLhjf6n1l0IWRH7FuHIUieoZ6k0p6rASHSgWiYNRMxfecbtX8zDAoG0QAWNi7rn40ygpR5gS1fWDAKhmnhKgQIT6wW0VmD4hraaeyP78iy8BLhlvblri2nCPIhDH5+l96v7D47ZZi3ZSOzcj89s1eS/k7/N4peEQJBAPEtGGJY+lBoCxQMhGyzuzDmgcS1Un1ZE2pt+XNCVl2b+T8fxWJH3tRRR8wOY5uvtPiK1HM/IjT0T5qwQeH8Yk0CQQC0tcv3d/bDb7bOe9QzUFDQkUSpTdPWAgMX2OVPxjdq3Sls9oA5+fGNYEy0OgyqTjde0b4iRzlD1O0OhLqPSUMfAkEAh5FIvqezdRU2/PsYSR4yoAdCdLdT+h/jGRVefhqQ/6eYUJJkWp15tTFHQX3pIe9/s6IeT/XyHYAjaxmevxAmlQJBAKSdhvQjf9KAjZKDEsa7vyJ/coCXuQUWSCMNHbcR5aGfXgE4e45UtUoIE1eKGcd6AM6LWhx3rR6xdFDpb9je8BkCQB0SpevGfOQkMk5i8xkEt9eeYP0fi8nv6eOUcK96EXbzs4jV2SAoQJ9oJegPtPROHbhIvVUmNQTbuP10Yjg59+8=";

    @Test
    public void test() throws Exception {
        verify(getJWT());
    }

    /**
     * 功能：将公钥字符串转换为公钥对象
     * @param key 生成的公钥字符串
     * @return 转换后得到的公钥对象
     * @throws Exception
     */
    public static PublicKey getPublicKey(String key) throws Exception {
        byte[] keyBytes = (new BASE64Decoder()).decodeBuffer(key);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePublic(keySpec);
    }

    /**
     * 功能：将私钥字符串转换为私钥对象
     * @param key 生成的私钥字符串
     * @return 转换后得到的私钥对象
     * @throws Exception
     */
    public static PrivateKey getPrivateKey(String key) throws Exception {
        byte[] keyBytes = (new BASE64Decoder()).decodeBuffer(key);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePrivate(keySpec);
    }

    /**
     * 用于加密
     * header 字段自动生成，无须自己设置
     */
    public static String getJWT() throws Exception {

        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.RS256;
        Map<String, Object> map = new HashMap<>(1024);
        map.put("name", "alan");
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
                .signWith(signatureAlgorithm,getPrivateKey(str_priK));
        return jwtBuilder.compact();
    }

    public static void verify(String token) throws Exception {
        System.out.println(token);
        FileInputStream fileInputStream = new FileInputStream("/Users/will/Desktop/id_rsa.pub");
        byte bytes[] = new byte[fileInputStream.available()];
        fileInputStream.read(bytes);

        Claims claims = Jwts.parser()
                .setSigningKey(getPublicKey(str_pubK))
                .requireIssuer("viathink")
                .parseClaimsJws(token).getBody();
        System.out.println(claims.toString());
    }
}