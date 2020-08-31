package com.xuecheng.auth;

import com.alibaba.fastjson.JSON;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.jwt.Jwt;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.jwt.crypto.sign.RsaSigner;
import org.springframework.security.jwt.crypto.sign.RsaVerifier;
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory;
import org.springframework.test.context.junit4.SpringRunner;
import java.security.KeyPair;
import java.security.interfaces.RSAPrivateKey;
import java.util.HashMap;
import java.util.Map;

@SpringBootTest
@RunWith(SpringRunner.class)
public class testCreateJwt {
    //生成一个令牌
    @Test
    public void testCreateJwt(){
        //证书文件
        String key_location = "xc.keystore";
        //密钥库密码
        String keystore_password = "xuechengkeystore";
        //访问证书路径
        ClassPathResource classPathResource = new ClassPathResource(key_location);
        //密钥工厂
        KeyStoreKeyFactory keyStoreKeyFactory = new KeyStoreKeyFactory(classPathResource, keystore_password.toCharArray());
        //密钥密码
        String keypassword = "xuecheng";
        //密钥别名
        String alias = "xckey";
        //密钥对
        KeyPair keyPair = keyStoreKeyFactory.getKeyPair(alias, keypassword.toCharArray());
        //私钥
        RSAPrivateKey aPrivate = (RSAPrivateKey) keyPair.getPrivate();
        //定义负载信息
        Map<String,String> tokenMap = new HashMap<>();
        tokenMap.put("id","123");
        tokenMap.put("name","itcast");
        tokenMap.put("roles","r1,r2");
        tokenMap.put("v","1");
        //生成jwt令牌
        Jwt jwt = JwtHelper.encode(JSON.toJSONString(tokenMap), new RsaSigner(aPrivate));
        //出去jwt令牌
        String encoded = jwt.getEncoded();
        System.out.println(encoded);
    }

    //资源服务使用公钥验证jwt的完整性，并对jwt解码
    @Test
    public void testVerify(){
        //jwt令牌
        String token = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJ2IjoiMSIsInJvbGVzIjoicjEscjIiLCJuYW1lIjoiaXRjYXN0IiwiaWQiOiIxMjMifQ.Zw-L7mrb67DUb1TcFFyAqtx_GfEMs4NPVyoGOxq841PmrUQL62IDMG-4ZmblCmQbTlqVXgv4fXjoT3ZDjFvJ6dFne_j7zPNPE_hSCiOcj7jEa-igourVRCQ1FX1AKu_vF_6IQWAZCog5WSvo9p9YGlq2iIGY9fvbygNEIS5HGclHZkZ-0Mv55e8sW8EKJyjrytVoqQpKg37tShtUe8o9dshea16Rn_daXAO9kq5xPf-aiIO4Dw2ic2vWtgvydfHOgKyi4dSl2DNUfG4t4vt-LuL2IZL4icGmYH36gUDO0tNFlLWHT8I0Vmfd1nMn14gqpobrmxECfoCvmZkm23yTHg";
        //公钥
        String publicKey = "-----BEGIN PUBLIC KEY-----MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAnASXh9oSvLRLxk901HANYM6KcYMzX8vFPnH/To2R+SrUVw1O9rEX6m1+rIaMzrEKPm12qPjVq3HMXDbRdUaJEXsB7NgGrAhepYAdJnYMizdltLdGsbfyjITUCOvzZ/QgM1M4INPMD+Ce859xse06jnOkCUzinZmasxrmgNV3Db1GtpyHIiGVUY0lSO1Frr9m5dpemylaT0BV3UwTQWVW9ljm6yR3dBncOdDENumT5tGbaDVyClV0FEB1XdSKd7VjiDCDbUAUbDTG1fm3K9sx7kO1uMGElbXLgMfboJ963HEJcU01km7BmFntqI5liyKheX+HBUCD4zbYNPw236U+7QIDAQAB-----END PUBLIC KEY-----";
        //校验jwt
        Jwt jwt = JwtHelper.decodeAndVerify(token, new RsaVerifier(publicKey));
        //获取jwt原始内容
        String encoded = jwt.getEncoded();
        System.out.println(encoded);
    }
}
