package com.xxg.xtoolkit;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class RsaUtil {

    private final static String ALGORITHM = "RSA";

    /**
     * 默认签名算法
     */
    private final static String DEFAULT_SIGN_ALGORITHM = "SHA256withRSA";

    /**
     * 公钥加密
     */
    public static byte[] encrypt(byte[] data, PublicKey publicKey) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] result = cipher.doFinal(data);
        return result;
    }

    /**
     * 私钥解密
     */
    public static byte[] decrypt(byte[] data, PrivateKey privateKey) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] result = cipher.doFinal(data);
        return result;
    }

    /**
     * 私钥签名
     */
    public static byte[] sign(byte[] data, PrivateKey privateKey) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        return sign(data, privateKey, DEFAULT_SIGN_ALGORITHM);
    }

    /**
     * 公钥验证签名
     */
    public static boolean verify(byte[] data, byte[] sign, PublicKey publicKey) throws NoSuchAlgorithmException, SignatureException, InvalidKeyException {
        return verify(data, sign, publicKey, DEFAULT_SIGN_ALGORITHM);
    }

    /**
     * 私钥签名
     */
    public static byte[] sign(byte[] data, PrivateKey privateKey, String algorithm) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        Signature signature = Signature.getInstance(algorithm);
        signature.initSign(privateKey);
        signature.update(data);
        byte[] result = signature.sign();
        return result;
    }

    /**
     * 公钥验证签名
     */
    public static boolean verify(byte[] data, byte[] sign, PublicKey publicKey, String algorithm) throws NoSuchAlgorithmException, SignatureException, InvalidKeyException {
        Signature signature = Signature.getInstance(algorithm);
        signature.initVerify(publicKey);
        signature.update(data);
        return signature.verify(sign);
    }

    /**
     * 读取公钥
     */
    public static PublicKey getPublicKey(byte[] data) throws NoSuchAlgorithmException, InvalidKeySpecException {
        return KeyFactory.getInstance(ALGORITHM).generatePublic(new X509EncodedKeySpec(data));
    }

    /**
     * 读取私钥
     */
    public static PrivateKey getPrivateKey(byte[] data) throws NoSuchAlgorithmException, InvalidKeySpecException {
        return KeyFactory.getInstance(ALGORITHM).generatePrivate(new PKCS8EncodedKeySpec(data));
    }

    /**
     * 读取公钥
     * 第一行一般是 -----BEGIN PUBLIC KEY-----
     */
    public static PublicKey getPublicKey(String data) throws NoSuchAlgorithmException, InvalidKeySpecException {
        String base64 = data.replace("-----BEGIN PUBLIC KEY-----\n", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replace("\n", "");
        byte[] key = Base64.getDecoder().decode(base64);
        return getPublicKey(key);
    }

    /**
     * 读取私钥
     * 第一行一般是 -----BEGIN PRIVATE KEY-----
     */
    public static PrivateKey getPrivateKey(String data) throws NoSuchAlgorithmException, InvalidKeySpecException {
        String base64 = data.replace("-----BEGIN PRIVATE KEY-----\n", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replace("\n", "");
        byte[] key = Base64.getDecoder().decode(base64);
        return getPrivateKey(key);
    }

    /**
     * 读取公钥
     * 第一行一般是 -----BEGIN PUBLIC KEY-----
     */
    public static PublicKey getPublicKey(File file) throws NoSuchAlgorithmException, InvalidKeySpecException, IOException {
        return getPublicKey(new String(Files.readAllBytes(Paths.get(file.getPath()))));
    }

    /**
     * 读取私钥
     * 第一行一般是 -----BEGIN PRIVATE KEY-----
     */
    public static PrivateKey getPrivateKey(File file) throws NoSuchAlgorithmException, InvalidKeySpecException, IOException {
        return getPrivateKey(new String(Files.readAllBytes(Paths.get(file.getPath()))));
    }

    /**
     * 从证书文件中获取公钥
     * 第一行一般是 -----BEGIN CERTIFICATE-----
     */
    public static PublicKey getPublicKeyFromCertificate(InputStream inputStream) throws CertificateException {
        CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
        X509Certificate certificate = (X509Certificate) certificateFactory.generateCertificate(inputStream);
        return certificate.getPublicKey();
    }

    /**
     * 从证书文件中获取公钥
     * 第一行一般是 -----BEGIN CERTIFICATE-----
     */
    public static PublicKey getPublicKeyFromCertificate(File file) throws IOException, CertificateException {
        try (FileInputStream inputStream = new FileInputStream(file)) {
            return getPublicKeyFromCertificate(inputStream);
        }
    }
}
