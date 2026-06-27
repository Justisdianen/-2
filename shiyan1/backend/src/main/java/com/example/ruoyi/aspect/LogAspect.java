package com.example.ruoyi.aspect;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONWriter;
import com.example.ruoyi.annotation.Log;
import com.example.ruoyi.common.UserContext;
import com.example.ruoyi.entity.SysOperLog;
import com.example.ruoyi.service.SysOperLogService;
import com.example.ruoyi.util.IpUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * 操作日志AOP切面
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class LogAspect {

    private final SysOperLogService operLogService;

    /**
     * 定义切点：所有带有@Log注解的方法
     */
    @Pointcut("@annotation(com.example.ruoyi.annotation.Log)")
    public void logPointcut() {}

    /**
     * 正常返回时记录日志
     */
    @AfterReturning(pointcut = "logPointcut()", returning = "result")
    public void doAfterReturning(JoinPoint joinPoint, Object result) {
        handleLog(joinPoint, null, result);
    }

    /**
     * 异常时记录日志
     */
    @AfterThrowing(pointcut = "logPointcut()", throwing = "e")
    public void doAfterThrowing(JoinPoint joinPoint, Exception e) {
        handleLog(joinPoint, e, null);
    }

    /**
     * 处理日志
     */
    private void handleLog(JoinPoint joinPoint, Exception e, Object result) {
        try {
            // 获取注解
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            Log logAnnotation = signature.getMethod().getAnnotation(Log.class);
            if (logAnnotation == null) {
                return;
            }

            // 获取请求信息
            HttpServletRequest request = getRequest();
            if (request == null) {
                return;
            }

            // 构建日志对象
            SysOperLog operLog = new SysOperLog();
            operLog.setStatus(e == null ? "0" : "1"); // 0正常 1异常
            operLog.setOperIp(IpUtils.getIpAddr(request));
            operLog.setOperUrl(request.getRequestURI());
            operLog.setOperName(UserContext.getUserName());
            operLog.setRequestMethod(request.getMethod());
            operLog.setMethod(joinPoint.getTarget().getClass().getName() + "." + signature.getName());
            operLog.setOperTime(LocalDateTime.now());

            // 设置模块标题和业务类型
            operLog.setTitle(logAnnotation.title());
            operLog.setBusinessType(logAnnotation.businessType().getCode());

            // 记录请求参数（排除敏感字段）
            if (logAnnotation.isSaveRequestData()) {
                String params = getRequestParams(joinPoint, logAnnotation.excludeParamNames());
                operLog.setOperParam(params);
            }

            // 记录响应结果
            if (logAnnotation.isSaveResponseData() && result != null) {
                operLog.setJsonResult(JSON.toJSONString(result, JSONWriter.Feature.IgnoreErrorGetter));
            }

            // 异常信息
            if (e != null) {
                operLog.setErrorMsg(e.getMessage());
            }

            // 记录耗时（从请求开始到现在）
            Long startTime = (Long) request.getAttribute("startTime");
            if (startTime != null) {
                operLog.setCostTime(System.currentTimeMillis() - startTime);
            } else {
                operLog.setCostTime(0L);
            }

            // 保存日志
            operLogService.recordOperLog(operLog);
        } catch (Exception ex) {
            log.error("记录操作日志异常：", ex);
        }
    }

    /**
     * 获取请求参数
     */
    private String getRequestParams(JoinPoint joinPoint, String[] excludeParamNames) {
        Object[] args = joinPoint.getArgs();
        if (args == null || args.length == 0) {
            return "";
        }

        Map<String, Object> params = new HashMap<>();
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String[] paramNames = signature.getParameterNames();

        for (int i = 0; i < args.length; i++) {
            // 排除文件类型和HttpServletRequest/HttpServletResponse
            if (args[i] instanceof MultipartFile 
                    || args[i] instanceof HttpServletRequest 
                    || args[i] instanceof jakarta.servlet.http.HttpServletResponse) {
                continue;
            }

            String paramName = paramNames != null && i < paramNames.length ? paramNames[i] : "arg" + i;
            
            // 排除敏感字段
            boolean exclude = Arrays.stream(excludeParamNames)
                    .anyMatch(name -> paramName.equalsIgnoreCase(name) || paramName.contains(name));
            
            if (exclude) {
                params.put(paramName, "******");
            } else {
                params.put(paramName, args[i]);
            }
        }

        return JSON.toJSONString(params, JSONWriter.Feature.IgnoreErrorGetter);
    }

    /**
     * 获取请求对象
     */
    private HttpServletRequest getRequest() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return attributes != null ? attributes.getRequest() : null;
    }
}