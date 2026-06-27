package com.example.ruoyi.util;

import jakarta.servlet.http.HttpServletRequest;
import org.lionsoul.ip2region.xdb.Searcher;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.FileCopyUtils;

import java.io.InputStream;

/**
 * IP地址工具类
 */
public class IpUtils {

    private static byte[] dbBinArr;

    private static final String UNKNOWN = "unknown";
    private static final String LOCALHOST_IP = "127.0.0.1";
    private static final String LOCALHOST_IPV6 = "::1";

    static {
        try {
            ClassPathResource resource = new ClassPathResource("ip2region/ip2region.xdb");
            InputStream inputStream = resource.getInputStream();
            dbBinArr = FileCopyUtils.copyToByteArray(inputStream);
        } catch (Exception e) {
            // 如果找不到ip2region.xdb文件，使用空实现
            dbBinArr = null;
        }
    }

    /**
     * 获取客户端IP地址
     */
    public static String getIpAddr(HttpServletRequest request) {
        if (request == null) {
            return UNKNOWN;
        }
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.isEmpty() || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Forwarded-For");
        }
        if (ip == null || ip.isEmpty() || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }

        // 对于通过多个代理的情况，第一个IP才是客户端的真实IP
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }

        // 本地地址处理
        if (LOCALHOST_IP.equals(ip) || LOCALHOST_IPV6.equals(ip)) {
            ip = LOCALHOST_IP;
        }

        return "0:0:0:0:0:0:0:1".equals(ip) ? LOCALHOST_IP : ip;
    }

    /**
     * 根据IP获取地理位置
     */
    public static String getLocation(String ip) {
        if (dbBinArr == null) {
            return "未知位置";
        }
        try {
            Searcher searcher = Searcher.newWithBuffer(dbBinArr);
            String region = searcher.search(ip);
            searcher.close();
            // 格式: 中国|0|省份|城市|ISP
            String[] parts = region.split("\\|");
            StringBuilder location = new StringBuilder();
            if (parts.length >= 3 && !"0".equals(parts[2])) {
                location.append(parts[2]);
            }
            if (parts.length >= 4 && !"0".equals(parts[3])) {
                if (location.length() > 0) {
                    location.append("-");
                }
                location.append(parts[3]);
            }
            return location.length() > 0 ? location.toString() : "未知位置";
        } catch (Exception e) {
            return "未知位置";
        }
    }

    /**
     * 获取浏览器信息
     */
    public static String getBrowser(HttpServletRequest request) {
        String userAgent = request.getHeader("User-Agent");
        if (userAgent == null) {
            return "未知";
        }
        if (userAgent.contains("Chrome")) {
            return "Chrome";
        } else if (userAgent.contains("Firefox")) {
            return "Firefox";
        } else if (userAgent.contains("Safari")) {
            return "Safari";
        } else if (userAgent.contains("Edge")) {
            return "Edge";
        } else if (userAgent.contains("MSIE") || userAgent.contains("Trident")) {
            return "IE";
        }
        return "其他";
    }

    /**
     * 获取操作系统信息
     */
    public static String getOs(HttpServletRequest request) {
        String userAgent = request.getHeader("User-Agent");
        if (userAgent == null) {
            return "未知";
        }
        if (userAgent.contains("Windows")) {
            if (userAgent.contains("Windows 10")) {
                return "Windows 10";
            } else if (userAgent.contains("Windows 8")) {
                return "Windows 8";
            } else if (userAgent.contains("Windows 7")) {
                return "Windows 7";
            }
            return "Windows";
        } else if (userAgent.contains("Mac OS X")) {
            return "Mac OS X";
        } else if (userAgent.contains("Linux")) {
            return "Linux";
        } else if (userAgent.contains("Android")) {
            return "Android";
        } else if (userAgent.contains("iPhone") || userAgent.contains("iPad")) {
            return "iOS";
        }
        return "其他";
    }
}