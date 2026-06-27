package com.example.ruoyi.util;

/**
 * 数据脱敏工具类
 */
public class DesensitizedUtil {

    /**
     * 手机号脱敏：隐藏中间4位
     */
    public static String mobile(String mobile) {
        if (mobile == null || mobile.length() < 11) {
            return mobile;
        }
        return mobile.substring(0, 3) + "****" + mobile.substring(7);
    }

    /**
     * 身份证脱敏：隐藏中间8位
     */
    public static String idCard(String idCard) {
        if (idCard == null || idCard.length() < 15) {
            return idCard;
        }
        return idCard.substring(0, 3) + "********" + idCard.substring(idCard.length() - 4);
    }

    /**
     * 邮箱脱敏：隐藏@前的部分
     */
    public static String email(String email) {
        if (email == null || !email.contains("@")) {
            return email;
        }
        int atIndex = email.indexOf("@");
        if (atIndex <= 2) {
            return email;
        }
        return email.substring(0, 2) + "****" + email.substring(atIndex);
    }

    /**
     * 银行卡脱敏：隐藏中间6位
     */
    public static String bankCard(String bankCard) {
        if (bankCard == null || bankCard.length() < 10) {
            return bankCard;
        }
        return bankCard.substring(0, 4) + "******" + bankCard.substring(bankCard.length() - 4);
    }

    /**
     * 姓名脱敏：隐藏中间字符
     */
    public static String name(String name) {
        if (name == null || name.length() <= 1) {
            return name;
        }
        if (name.length() == 2) {
            return name.substring(0, 1) + "*";
        }
        return name.substring(0, 1) + "*" + name.substring(name.length() - 1);
    }

    /**
     * 密码脱敏：全部隐藏
     */
    public static String password(String password) {
        if (password == null) {
            return null;
        }
        return "******";
    }

    /**
     * 地址脱敏：隐藏详细地址
     */
    public static String address(String address) {
        if (address == null || address.length() <= 6) {
            return address;
        }
        return address.substring(0, 6) + "****";
    }
}