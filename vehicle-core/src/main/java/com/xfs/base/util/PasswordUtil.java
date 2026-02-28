package com.xfs.base.util;

import java.security.SecureRandom;

/* 密码工具类 用于生成随机密码 */
public class PasswordUtil {
    //定义一个允许出现在密码串中的字符集合,包含大写字母 小写字母 数字 特殊字符
    private static final String ALLOWED_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()_+";
    //创建一个安全的随机数生成器,用于生成随机数
    private static final SecureRandom random = new SecureRandom();
    /* 创建生成指定位数随机密码串的静态方法 */
    public static String generateRandomPassword(int length) {
       if(length < 5) throw new RuntimeException("密码长度不能小于5!");
       //创建一个字符串拼接工具,用于拼接生成的密码串
       StringBuilder password = new StringBuilder();
       for(int i = 0; i < length; i++){
           //生成一个随机数作为随机下标
           int index = random.nextInt(ALLOWED_CHARS.length());
           char randomChar = ALLOWED_CHARS.charAt(index);
           //将本次获取到的随机字符拼接到密码中
           password.append(randomChar);
       }
       //返回生成的密码
       return password.toString();
    }
}
