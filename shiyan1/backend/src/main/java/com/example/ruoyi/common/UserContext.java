package com.example.ruoyi.common;

/**
 * 用户上下文信息（用于存储当前请求的用户信息）
 */
public class UserContext {

    private static final ThreadLocal<Long> USER_ID = new ThreadLocal<>();
    private static final ThreadLocal<String> USER_NAME = new ThreadLocal<>();
    private static final ThreadLocal<String> TOKEN = new ThreadLocal<>();
    private static final ThreadLocal<String> IP = new ThreadLocal<>();
    private static final ThreadLocal<Long> DEPT_ID = new ThreadLocal<>();
    private static final ThreadLocal<String> DATA_SCOPE = new ThreadLocal<>();

    public static void setUserId(Long userId) {
        USER_ID.set(userId);
    }

    public static Long getUserId() {
        return USER_ID.get();
    }

    public static void setUserName(String userName) {
        USER_NAME.set(userName);
    }

    public static String getUserName() {
        return USER_NAME.get();
    }

    public static void setToken(String token) {
        TOKEN.set(token);
    }

    public static String getToken() {
        return TOKEN.get();
    }

    public static void setIp(String ip) {
        IP.set(ip);
    }

    public static String getIp() {
        return IP.get();
    }

    public static void setDeptId(Long deptId) {
        DEPT_ID.set(deptId);
    }

    public static Long getDeptId() {
        return DEPT_ID.get();
    }

    public static void setDataScope(String dataScope) {
        DATA_SCOPE.set(dataScope);
    }

    public static String getDataScope() {
        return DATA_SCOPE.get();
    }

    public static void clear() {
        USER_ID.remove();
        USER_NAME.remove();
        TOKEN.remove();
        IP.remove();
        DEPT_ID.remove();
        DATA_SCOPE.remove();
    }
}