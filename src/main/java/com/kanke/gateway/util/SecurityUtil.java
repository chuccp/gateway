package com.kanke.gateway.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SecurityUtil {

	public static byte[] SHA256(byte[] data) throws NoSuchAlgorithmException {
		MessageDigest md = MessageDigest.getInstance("SHA-256");
		md.update(data);
		return md.digest();
	}

	 /**
     * 密钥, 256位32个字节
     */

    private static final String AES = "AES";


    /**
     * 加密解密算法/加密模式/填充方式
     */
    private static final String CIPHER_ALGORITHM = "AES/CBC/PKCS5Padding";

    /**
     * AES加密
     */
    public static byte[] encodeAES(byte[] content,byte[] key,byte[] iv) {
        try {
            javax.crypto.SecretKey secretKey = new javax.crypto.spec.SecretKeySpec(key, AES);
            javax.crypto.Cipher cipher = javax.crypto.Cipher.getInstance(CIPHER_ALGORITHM);
            
            
            
            
            cipher.init(javax.crypto.Cipher.ENCRYPT_MODE, secretKey,new javax.crypto.spec.IvParameterSpec(iv));
            byte[] byteAES = cipher.doFinal(content);
            return byteAES;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * AES解密
     */
    public static byte[] decodeAES(byte[] content, byte[] key,byte[] iv) {
        try {
            javax.crypto.SecretKey secretKey = new javax.crypto.spec.SecretKeySpec(key, AES);
            javax.crypto.Cipher cipher = javax.crypto.Cipher.getInstance(CIPHER_ALGORITHM);
            cipher.init(javax.crypto.Cipher.DECRYPT_MODE, secretKey,new javax.crypto.spec.IvParameterSpec(iv));
            byte[] byteDecode = cipher.doFinal(content);
            return byteDecode;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
