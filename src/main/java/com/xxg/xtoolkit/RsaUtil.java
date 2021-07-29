package com.xxg.xtoolkit;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Enumeration;

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
     * 公钥证书加密
     */
    public static byte[] encrypt(byte[] data, Certificate certificate) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, certificate);
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
     * 公钥证书验证签名
     */
    public static boolean verify(byte[] data, byte[] sign, Certificate certificate) throws NoSuchAlgorithmException, SignatureException, InvalidKeyException {
        return verify(data, sign, certificate, DEFAULT_SIGN_ALGORITHM);
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
     * 公钥证书验证签名
     */
    public static boolean verify(byte[] data, byte[] sign, Certificate certificate, String algorithm) throws NoSuchAlgorithmException, SignatureException, InvalidKeyException {
        Signature signature = Signature.getInstance(algorithm);
        signature.initVerify(certificate);
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
        Certificate certificate = getCertificate(inputStream);
        return certificate.getPublicKey();
    }

    /**
     * 从证书文件中获取证书
     * 第一行一般是 -----BEGIN CERTIFICATE-----
     */
    public static Certificate getCertificate(InputStream inputStream) throws CertificateException {
        CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
        return certificateFactory.generateCertificate(inputStream);
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

    /**
     * 从证书文件中获取证书
     * 第一行一般是 -----BEGIN CERTIFICATE-----
     */
    public static Certificate getCertificate(File file) throws CertificateException, IOException {
        try (FileInputStream inputStream = new FileInputStream(file)) {
            return getCertificate(inputStream);
        }
    }

    /**
     * 从 .p12/.pfx 证书中获取公钥证书
     */
    public static Certificate getCertificateFromPkcs12(InputStream inputStream, String password) throws KeyStoreException, CertificateException, NoSuchAlgorithmException, IOException {
        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        keyStore.load(inputStream, password.toCharArray());
        Enumeration<String> aliases = keyStore.aliases();
        while (aliases.hasMoreElements()) {
            String alias = aliases.nextElement();
            return keyStore.getCertificate(alias);
        }
        throw new IOException("文件中不包含证书");
    }

    /**
     * 从 .p12/.pfx 证书中获取公钥证书
     */
    public static Certificate getCertificateFromPkcs12(File file, String password) throws KeyStoreException, CertificateException, NoSuchAlgorithmException, IOException {
        try (FileInputStream inputStream = new FileInputStream(file)) {
            return getCertificateFromPkcs12(inputStream, password);
        }
    }

    /**
     * 从 .p12/.pfx 证书中获取公钥
     */
    public static PublicKey getPublicKeyFromPkcs12(InputStream inputStream, String password) throws KeyStoreException, CertificateException, NoSuchAlgorithmException, IOException {
        return getCertificateFromPkcs12(inputStream, password).getPublicKey();
    }

    /**
     * 从 .p12/.pfx 证书中获取公钥
     */
    public static PublicKey getPublicKeyFromPkcs12(File file, String password) throws KeyStoreException, CertificateException, NoSuchAlgorithmException, IOException {
        try (FileInputStream inputStream = new FileInputStream(file)) {
            return getPublicKeyFromPkcs12(inputStream, password);
        }
    }

    /**
     * 从 .p12/.pfx 证书中获取私钥
     */
    public static PrivateKey getPrivateKeyFromPkcs12(InputStream inputStream, String password) throws KeyStoreException, CertificateException, NoSuchAlgorithmException, IOException, UnrecoverableKeyException {
        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        keyStore.load(inputStream, password.toCharArray());
        Enumeration<String> aliases = keyStore.aliases();
        while (aliases.hasMoreElements()) {
            String alias = aliases.nextElement();
            return (PrivateKey) keyStore.getKey(alias, password.toCharArray());
        }
        throw new IOException("文件中不包含证书");
    }

    /**
     * 从 .p12/.pfx 证书中获取私钥
     */
    public static PrivateKey getPrivateKeyFromPkcs12(File file, String password) throws KeyStoreException, CertificateException, NoSuchAlgorithmException, IOException, UnrecoverableKeyException {
        try (FileInputStream inputStream = new FileInputStream(file)) {
            return getPrivateKeyFromPkcs12(inputStream, password);
        }
    }
}
