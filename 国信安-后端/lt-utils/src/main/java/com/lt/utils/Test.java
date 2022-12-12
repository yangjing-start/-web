package com.lt.utils;


import javax.crypto.Cipher;
import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.Base64;

public class Test {
    /**
     * RSA最大加密明文大小
     */
    /**
     * RSA公钥加密
     *
     * @param str       加密字符串
     * @param publicKey 公钥
     * @return 密文
     * @throws Exception 加密过程中的异常信息
     */
    public static String encrypt(String str, String publicKey) throws Exception {
        //base64编码的公钥
        byte[] decoded = Base64.getDecoder().decode(publicKey);
        RSAPublicKey pubKey = (RSAPublicKey) KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(decoded));
        //RSA加密
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, pubKey);
        String outStr = Base64.getEncoder().encodeToString(cipher.doFinal(str.getBytes("UTF-8")));
        return outStr;
    }

    /**
     * RSA私钥解密
     *
     * @param str        加密字符串
     * @param privateKey 私钥
     * @return 明文
     * @throws Exception 解密过程中的异常信息
     */
    public static String decrypt(String str, String privateKey) throws Exception {
        //64位解码加密后的字符串
        byte[] inputByte = Base64.getDecoder().decode(str);
        //base64编码的私钥
        byte[] decoded = Base64.getDecoder().decode(privateKey);
        RSAPrivateKey priKey = (RSAPrivateKey) KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(decoded));
        //RSA解密
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, priKey);
        String outStr = new String(cipher.doFinal(inputByte));
        return outStr;
    }


    public static void main(String[] args) throws Exception {
        String pubKeyFile = "D:\\lt-02\\lt-02\\lt-utils\\src\\main\\resources\\authKey\\id_key_rsa.pub";
        String privateFile  = "D:\\lt-02\\lt-02\\lt-utils\\src\\main\\resources\\authKey\\id_key_rsa";


        RSAPublicKey publicKey;
        publicKey = (RSAPublicKey) RsaUtils.getPublicKey(pubKeyFile);
        System.out.println(publicKey.getPublicExponent());
        System.out.println(publicKey.getModulus());
        //公钥字符串
        String publicKeyString = Base64.getEncoder().encodeToString(publicKey.getEncoded());
        String encrypt = encrypt("123", publicKeyString);

        System.out.println(encrypt);

        RSAPrivateKey privateKey;
        String s = "zvADHr8vdHolCGAo9HuHBOgq+vrbzZDLpqm7LmYke74YwJH97Cna3d6CDu+8V+8g6KKdfq7K0WrbpIGvmFUHwVtETvsGpnGy7SRaosP+MkQF9udfArQ+i31IPuLeiXgITd3vM+j7YhMvTohdyhl5TLHrEIh0uPRC8ApBgucnkLw++ABUC86cJf658HyAfVQdpEOAAp5uC94Odw==";
        privateKey = (RSAPrivateKey) RsaUtils.getPrivateKey(privateFile);
        // 得到私钥字符串
        String privateKeyString = Base64.getEncoder().encodeToString(privateKey.getEncoded());
        String  decrypt = decrypt(encrypt, privateKeyString);
        System.out.println(decrypt);
    }
}
