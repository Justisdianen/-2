package com.example.ruoyi.interceptor;

import com.alibaba.fastjson2.JSON;
import com.example.ruoyi.annotation.RepeatSubmit;
import com.example.ruoyi.common.R;
import com.example.ruoyi.common.UserContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 防重复提交拦截器
 */
@Component
@RequiredArgsConstructor
public class RepeatSubmitInterceptor implements HandlerInterceptor {

    // 缓存请求信息
    private static final ConcurrentHashMap<String, Long> REQUEST_CACHE = new ConcurrentHashMap<>();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            Method method = handlerMethod.getMethod();
            RepeatSubmit annotation = method.getAnnotation(RepeatSubmit.class);
            
            if (annotation != null) {
                // 检查是否重复提交
                if (isRepeatSubmit(request, annotation)) {
                    return reject(response, annotation.message());
                }
            }
        }
        return true;
    }

    /**
     * 检查是否重复提交
     */
    private boolean isRepeatSubmit(HttpServletRequest request, RepeatSubmit annotation) {
        String key = generateKey(request);
        if (key == null) {
            return false;
        }

        Long lastTime = REQUEST_CACHE.get(key);
        if (lastTime == null) {
            REQUEST_CACHE.put(key, System.currentTimeMillis());
            return false;
        }

        long currentTime = System.currentTimeMillis();
        long interval = annotation.interval();

        if (currentTime - lastTime < interval) {
            return true;
        }

        REQUEST_CACHE.put(key, currentTime);
        return false;
    }

    /**
     * 生成请求唯一标识
     */
    private String generateKey(HttpServletRequest request) {
        String userId = String.valueOf(UserContext.getUserId());
        String uri = request.getRequestURI();
        String method = request.getMethod();
        String params = request.getQueryString();
        
        return userId + ":" + method + ":" + uri + ":" + (params != null ? params : "");
    }

    /**
     * 拒绝重复提交
     */
    private boolean reject(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/json;charset=UTF-8");
        R<Void> result = R.error(message);
        result.setCode(500);
        response.getWriter().write(JSON.toJSONString(result));
        return false;
    }

    /**
     * 清理过期缓存
     */
    public void cleanExpiredCache(long expireTime) {
        long currentTime = System.currentTimeMillis();
        REQUEST_CACHE.entrySet().removeIf(entry -> currentTime - entry.getValue() > expireTime);
    }
}