package com.example.ruoyi.interceptor;

import com.alibaba.fastjson2.JSON;
import com.example.ruoyi.common.R;
import com.example.ruoyi.common.UserContext;
import com.example.ruoyi.service.SysUserService;
import com.example.ruoyi.service.TokenService;
import com.example.ruoyi.util.IpUtils;
import com.example.ruoyi.util.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;

/**
 * Token验证拦截器
 */
@Component
@RequiredArgsConstructor
public class TokenInterceptor implements HandlerInterceptor {

    private final TokenService tokenService;
    private final JwtUtils jwtUtils;
    private final SysUserService sysUserService;

    // 不需要Token验证的路径
    private static final String[] EXCLUDE_PATHS = {
            "/login",
            "/logout",
            "/register",
            "/captcha",
            "/resetPwd",
            "/swagger-ui",
            "/v3/api-docs",
            "/api-docs",
            "/doc.html",
            "/webjars",
            "/favicon.ico",
            "/error"
    };

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 检查是否是排除路径
        String uri = request.getRequestURI();
        String method = request.getMethod();
        System.out.println("[TokenInterceptor] Request: " + method + " " + uri);
        
        for (String excludePath : EXCLUDE_PATHS) {
            if (uri.startsWith(excludePath) || uri.equals(excludePath)) {
                System.out.println("[TokenInterceptor] Skip token check for excluded path: " + uri);
                return true;
            }
        }

        // OPTIONS请求直接放行
        if ("OPTIONS".equals(method)) {
            System.out.println("[TokenInterceptor] Skip token check for OPTIONS request");
            return true;
        }

        // 获取Token
        String authorization = request.getHeader("Authorization");
        System.out.println("[TokenInterceptor] Authorization header: " + (authorization != null ? authorization.substring(0, Math.min(30, authorization.length())) + "..." : "null"));
        
        String token = jwtUtils.extractTokenFromHeader(authorization);
        System.out.println("[TokenInterceptor] Extracted token: " + (token != null ? token.substring(0, Math.min(20, token.length())) + "..." : "null"));

        if (token == null || token.isEmpty()) {
            System.out.println("[TokenInterceptor] Token is null or empty, returning 401");
            return unauthorized(response, "未登录，请先登录");
        }

        // 验证Token是否有效
        boolean isValid = tokenService.validateToken(token);
        System.out.println("[TokenInterceptor] Token validation result: " + isValid);
        
        if (!isValid) {
            System.out.println("[TokenInterceptor] Token invalid, returning 401");
            return unauthorized(response, "登录已过期，请重新登录");
        }

        // 刷新最后访问时间
        tokenService.refreshLastAccessTime(token);

        // 设置用户上下文
        Long userId = tokenService.getUserIdByToken(token);
        String userName = tokenService.getUserNameByToken(token);
        UserContext.setUserId(userId);
        UserContext.setUserName(userName);
        UserContext.setToken(token);
        UserContext.setIp(IpUtils.getIpAddr(request));

        // 设置部门信息（用于数据权限）
        if (userId != null) {
            var user = sysUserService.getById(userId);
            if (user != null) {
                UserContext.setDeptId(user.getDeptId());
            }
        }

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        // 清除用户上下文
        UserContext.clear();
    }

    /**
     * 返回未授权响应
     */
    private boolean unauthorized(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        R<Void> result = R.error(message);
        result.setCode(401);
        response.getWriter().write(JSON.toJSONString(result));
        return false;
    }
}