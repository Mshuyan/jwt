# jwt
> 登录认证（json web token）

## 介绍

1. `JWT`全称`json web token`，是用于身份认证的令牌

2. 完整`jwt`样式

   ```java
   eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJ1SWQiOiIxIiwiZXhwIjoxNTM1MDQ3MjQ5LCJqdGkiOiI1N2QzNWIxOS1kZWQ1LTQ4NTgtYjM4OS04M2Y3ZWU5OGZkMTcifQ.aStUf6SVeF4wqz0cIKIiHN8e2dWWcD8aMQioWcfSU5c
   ```

   可见`jwt`的形式如：`header.payload.signature`，由`jwt`的三部分使用`.`相连

## 结构

> + `jwt`由3部分组成：
>   + header（头部）
>   + payload（载体）
>   + Signature（签名）

### header

1. header编码前样式

   ```json
   {
     "typ": "JWT",
     "alg": "HS256"
   }
   ```

   + typ

     类型，这里固定就是JWT

   + alg

     算法，指的是生成签名时用的是什么算法，常用的就是`HS256`

2. 将该json字符串进行`base64url`编码后，就是`jwt`的头部

### payload

1. payload编码前样式

   ```java
   {
     "sub": "1234567890",
     "name": "John Doe",
     "admin": true
   }
   ```

2. 一般来说，payload部分应该有7个标准声名，但不必须

   + `iss`: （Issuer）签发者

   + `iat`: （Issued At）签发时间，用Unix时间戳表示

   + `exp`: （Expiration Time）过期时间，用Unix时间戳表示

   + `aud`: （Audience）接收该JWT的一方

   + `sub`: （Subject）该JWT的主题

   + `nbf`: （Not Before）不要早于这个时间

   + `jti`: （JWT ID）用于标识JWT的唯一ID

   除此之外，也可以添加一下其他自定义字段

3. 将该json字符串进行`base64url`编码后，就是`jwt`的载体部分

### signature

> 前面我们已经得到`base64url(header).base64url(payload)`，再准备1个`secret`字符串，将这两部分字符串用头部中`alg`指定的算法进行加密，得到signature

## 说明

1. `base64url`编码是可逆的，可以通过jwt中的字符串获得铭文信息，所以一些不太敏感的用户信息可以放在jwt中
2. 签名算法都是不可逆的，并且secret只有服务器知道，所以保证了jwt中数据的安全性

## 认证流程

+ 对称加密
  + 用户登录通过后，服务器按照上述规则生成1个jwt，返回给浏览器
  + 浏览器再次访问时，将jwt带回，浏览器拿出自己的`secret`和返回的`jwt`进行验证就能知道这是不是1个有效的token
  + 如果签名验证通过，就可以查看payload中的字段，检查是否过期等
+ 非对称加密
  + 服务端事先准备1个`privatekey`和1个`publickey`文件
  + 用户登录通过后，服务器按照上述规则，使用`privatekey`生成1个jwt，返回给浏览器
  + 浏览器再次访问时，将jwt带回，浏览器拿出自己的`publickey`和返回的`jwt`进行验证就能知道这是不是1个有效的token
  + 如果签名验证通过，就可以查看payload中的字段，检查是否过期等

## 优缺点

#### 优点

1. 结构简单，字节消耗小，便于传输
2. 因为json的通用性，jwt也可以跨语言
3. 自包含，jwt的载体内包含一些业务需要的非敏感数据，可以减少请求接口次数
4. 无状态，易于横向扩展（分布式）
5. 支持跨域
6. 手机端开发没有cookie处理，所以更适合移动端开发

#### 缺点

	因为jwt的无状态特性，注销、修改密码、续签等存在一些问题

## 存在哪

> jwt只是1串字符串，所以它可以以任意方式通过http协议传给前端
>
> 建议使用请求头传递`jwt`

1. cookies

   在后端将jwt设置到cookie中

2. localStorage

   浏览器的持久化存储

   后端以任意方式将jwt传给前端后，由前端代码负责存到localstorage

3. sessionStorage

   浏览器的会话存储

   后端以任意方式将jwt传给前端后，由前端代码负责存到sessionStorage

## 加密

> 上面提到jwt的加密算法，这里对比讲一下`HS256`和`RS256`

+ 对称加密算法

  `HS256`属于对称加密

  服务端存着1个secret，浏览器端只要不知道这个secret，就无法伪造jwt

  但是一旦这个secret泄露了，用户就可以自己伪造jwt了

+ 非对称加密算法

  >  `RS256`属于非对称加密
  >
  > 非对称加密使用`privateKey`和`publickey`
  >
  > `privatekey`仅用于签发token
  >
  > 验证token的时候只用`publickey`进行验证

  + 服务端浏览器模型

    浏览器签发token后将token发给浏览器，由于验证token的工作需要服务器完成，所以`publickey`和`privatekey`都留在服务器

  + git模型

    本地生成1个`privatekey`和1个`publickey`，因为git需要验证拉取请求是不是我这台电脑发送的，所以需要将`publickey`交给git，让git使用`publickey`进行验证

## java代码实现

### HS256

1. 依赖

   ```xml
   <dependency>
       <groupId>com.auth0</groupId>
       <artifactId>java-jwt</artifactId>
       <version>3.4.0</version>
   </dependency>
   ```

2. 加密

   ```java
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
   ```

3. 验证

   ```java
   public static void verify(String token){
       Algorithm algorithm = Algorithm.HMAC256("secret");
       JWTVerifier verifier = JWT.require(algorithm)
           // 验证 iss
           .withIssuer("viathink")
           .build();
       DecodedJWT jwt = verifier.verify(token);
       // 通过jwt获取token中的信息
       System.out.println(jwt.getToken());
   }
   ```



> Demo中还有利用`jjwt`依赖实现的`hs256`和`rs256`加密方式的jwt

