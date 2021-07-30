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
