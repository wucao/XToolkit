package com.xxg.xtoolkit;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.spec.AlgorithmParameterSpec;

public class AesUtil {

    // 支持的AES实现：https://docs.oracle.com/javase/8/docs/api/javax/crypto/Cipher.html
    public static final String AES_CBC_NOPADDING = "AES/CBC/NoPadding";
    public static final String AES_CBC_PKCS5PADDING = "AES/CBC/PKCS5Padding";
    public static final String AES_ECB_NOPADDING = "AES/ECB/NoPadding";
    public static final String AES_ECB_PKCS5PADDING = "AES/ECB/PKCS5Padding";
    public static final String AES_GCM_NOPADDING = "AES/GCM/NoPadding";

    public static byte[] encrypt(String algorithm, byte[] data, Key key) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance(algorithm);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] result = cipher.doFinal(data);
        return result;
    }

    public static byte[] decrypt(String algorithm, byte[] data, Key key) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance(algorithm);
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] result = cipher.doFinal(data);
        return result;
    }

    public static byte[] encrypt(String algorithm, byte[] data, byte[] key) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        return encrypt(algorithm, data, getSecretKey(key));
    }

    public static byte[] decrypt(String algorithm, byte[] data, byte[] key) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        return decrypt(algorithm, data, getSecretKey(key));
    }

    public static byte[] encrypt(String algorithm, byte[] data, Key key, AlgorithmParameterSpec parameter) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, InvalidAlgorithmParameterException {
        Cipher cipher = Cipher.getInstance(algorithm);
        cipher.init(Cipher.ENCRYPT_MODE, key, parameter);
        byte[] result = cipher.doFinal(data);
        return result;
    }

    public static byte[] decrypt(String algorithm, byte[] data, Key key, AlgorithmParameterSpec parameter) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, InvalidAlgorithmParameterException {
        Cipher cipher = Cipher.getInstance(algorithm);
        cipher.init(Cipher.DECRYPT_MODE, key, parameter);
        byte[] result = cipher.doFinal(data);
        return result;
    }

    public static byte[] encrypt(String algorithm, byte[] data, byte[] key, byte[] iv) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, InvalidAlgorithmParameterException {
        return encrypt(algorithm, data, getSecretKey(key), getIvParameter(iv));
    }

    public static byte[] decrypt(String algorithm, byte[] data, byte[] key, byte[] iv) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, InvalidAlgorithmParameterException {
        return decrypt(algorithm, data, getSecretKey(key), getIvParameter(iv));
    }


    public static byte[] cbcEncrypt(byte[] data, Key key, IvParameterSpec parameter) throws NoSuchPaddingException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException {
        return encrypt(AES_CBC_PKCS5PADDING, data, key, parameter);
    }

    public static byte[] cbcDecrypt(byte[] data, Key key, IvParameterSpec parameter) throws NoSuchPaddingException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException {
        return decrypt(AES_CBC_PKCS5PADDING, data, key, parameter);
    }

    public static byte[] cbcEncrypt(byte[] data, byte[] key, byte[] iv) throws NoSuchPaddingException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException {
        return encrypt(AES_CBC_PKCS5PADDING, data, key, iv);
    }

    public static byte[] cbcDecrypt(byte[] data, byte[] key, byte[] iv) throws NoSuchPaddingException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException {
        return decrypt(AES_CBC_PKCS5PADDING, data, key, iv);
    }

    public static byte[] ecbEncrypt(byte[] data, Key key) throws NoSuchPaddingException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException {
        return encrypt(AES_ECB_PKCS5PADDING, data, key);
    }

    public static byte[] ecbDecrypt(byte[] data, Key key) throws NoSuchPaddingException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException {
        return decrypt(AES_ECB_PKCS5PADDING, data, key);
    }

    public static byte[] ecbEncrypt(byte[] data, byte[] key) throws NoSuchPaddingException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException {
        return encrypt(AES_ECB_PKCS5PADDING, data, key);
    }

    public static byte[] ecbDecrypt(byte[] data, byte[] key) throws NoSuchPaddingException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException {
        return decrypt(AES_ECB_PKCS5PADDING, data, key);
    }

    /**
     * 生成 AES 密钥，字节数必须为 16 字节、24 字节或 32 字节
     */
    public static Key getSecretKey(byte[] key) {
        return new SecretKeySpec(key, "AES");
    }

    public static IvParameterSpec getIvParameter(byte[] iv) {
        return new IvParameterSpec(iv);
    }
}
