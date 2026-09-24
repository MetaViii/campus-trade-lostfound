package com.campus.util;

import java.security.MessageDigest;

/**
 * 密码工具类。
 * 使用 MD5 对用户密码进行单向加密后再存入数据库，避免明文存储。
 */
public class PasswordUtil {

    /** 将明文密码加密为 32 位小写十六进制字符串 */
    public static String md5(String plainText) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] bytes = md.digest(plainText.getBytes("UTF-8"));
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) {
                String hex = Integer.toHexString(b & 0xFF);
                if (hex.length() == 1) {
                    sb.append('0');
                }
                sb.append(hex);
            }
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException("密码加密失败", e);
        }
    }

    /** 校验明文密码与已加密密码是否一致 */
    public static boolean verify(String plainText, String encrypted) {
        return md5(plainText).equals(encrypted);
    }
}
