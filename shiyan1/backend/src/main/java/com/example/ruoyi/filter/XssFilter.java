package com.example.ruoyi.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * XSS攻击过滤器
 */
@Component
public class XssFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        XssHttpServletRequestWrapper wrapper = new XssHttpServletRequestWrapper(httpRequest);
        chain.doFilter(wrapper, response);
    }

    /**
     * XSS请求包装类
     */
    static class XssHttpServletRequestWrapper extends HttpServletRequestWrapper {

        public XssHttpServletRequestWrapper(HttpServletRequest request) {
            super(request);
        }

        @Override
        public String getParameter(String name) {
            String value = super.getParameter(name);
            return cleanXSS(value);
        }

        @Override
        public String[] getParameterValues(String name) {
            String[] values = super.getParameterValues(name);
            if (values == null) {
                return null;
            }
            String[] cleanValues = new String[values.length];
            for (int i = 0; i < values.length; i++) {
                cleanValues[i] = cleanXSS(values[i]);
            }
            return cleanValues;
        }

        @Override
        public Map<String, String[]> getParameterMap() {
            Map<String, String[]> originalMap = super.getParameterMap();
            Map<String, String[]> cleanMap = new HashMap<>();
            for (Map.Entry<String, String[]> entry : originalMap.entrySet()) {
                String[] cleanValues = new String[entry.getValue().length];
                for (int i = 0; i < entry.getValue().length; i++) {
                    cleanValues[i] = cleanXSS(entry.getValue()[i]);
                }
                cleanMap.put(entry.getKey(), cleanValues);
            }
            return cleanMap;
        }

        @Override
        public String getHeader(String name) {
            if ("Authorization".equalsIgnoreCase(name) || 
                "Content-Type".equalsIgnoreCase(name) ||
                "Origin".equalsIgnoreCase(name)) {
                return super.getHeader(name);
            }
            String value = super.getHeader(name);
            return cleanXSS(value);
        }

        /**
         * 清除XSS攻击字符
         */
        private String cleanXSS(String value) {
            if (value == null || value.isEmpty()) {
                return value;
            }
            
            // XSS攻击字符过滤
            value = value.replaceAll("<", "&lt;");
            value = value.replaceAll(">", "&gt;");
            value = value.replaceAll("\\(", "&#40;");
            value = value.replaceAll("\\)", "&#41;");
            value = value.replaceAll("'", "&#39;");
            value = value.replaceAll("\"", "&quot;");
            value = value.replaceAll("eval\\((.*)\\)", "");
            value = value.replaceAll("[\"'][\\s]*javascript:(.*)[\"']", "\"\"");
            value = value.replaceAll("script", "");
            
            // SQL注入过滤
            value = value.replaceAll("(?i)select", "");
            value = value.replaceAll("(?i)insert", "");
            value = value.replaceAll("(?i)delete", "");
            value = value.replaceAll("(?i)update", "");
            value = value.replaceAll("(?i)drop", "");
            value = value.replaceAll("(?i)truncate", "");
            value = value.replaceAll("(?i)exec", "");
            value = value.replaceAll("(?i)execute", "");
            value = value.replaceAll("(?i)declare", "");
            value = value.replaceAll("(?i)--", "");
            value = value.replaceAll("(?i);", "");
            
            return value;
        }
    }
}