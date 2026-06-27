package com.example.ruoyi.exception;

import com.alibaba.fastjson2.JSON;
import com.example.ruoyi.common.R;
import com.example.ruoyi.common.UserContext;
import com.example.ruoyi.entity.SysExceptionLog;
import com.example.ruoyi.service.SysExceptionLogService;
import com.example.ruoyi.util.IpUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

/**
 * 全局异常处理器
 */
@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final SysExceptionLogService exceptionLogService;

    /**
     * 处理所有未捕获异常
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public R<Void> handleException(Exception e, HttpServletRequest request) {
        log.error("系统异常：", e);

        // 记录异常日志
        recordExceptionLog(e, request);

        return R.error("系统内部错误，请联系管理员");
    }

    /**
     * 处理运行时异常
     */
    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public R<Void> handleRuntimeException(RuntimeException e, HttpServletRequest request) {
        log.error("运行时异常：", e);

        // 记录异常日志
        recordExceptionLog(e, request);

        return R.error("系统内部错误： " + e.getMessage());
    }

    /**
     * 处理参数校验异常
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public R<Void> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));
        return R.error(message);
    }

    /**
     * 处理绑定异常
     */
    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public R<Void> handleBindException(BindException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));
        return R.error(message);
    }

    /**
     * 处理业务异常
     */
    @ExceptionHandler(BusinessException.class)
    @ResponseStatus(HttpStatus.OK)
    public R<Void> handleBusinessException(BusinessException e) {
        return R.error(e.getMessage());
    }

    /**
     * 处理权限异常
     */
    @ExceptionHandler(PermissionException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public R<Void> handlePermissionException(PermissionException e) {
        return R.error("无权限访问");
    }

    /**
     * 记录异常日志
     */
    private void recordExceptionLog(Exception e, HttpServletRequest request) {
        try {
            SysExceptionLog log = new SysExceptionLog();
            log.setExceptionType(e.getClass().getSimpleName());
            log.setExceptionMsg(e.getMessage());
            
            // 异常堆栈信息
            StringBuilder stackTrace = new StringBuilder();
            for (StackTraceElement element : e.getStackTrace()) {
                stackTrace.append(element.toString()).append("\n");
                if (stackTrace.length() > 5000) {
                    break;
                }
            }
            log.setExceptionStack(stackTrace.toString());
            
            log.setRequestUrl(request.getRequestURI());
            log.setRequestMethod(request.getMethod());
            log.setRequestParam(JSON.toJSONString(request.getParameterMap()));
            log.setOperatorName(UserContext.getUserName());
            log.setOperIp(IpUtils.getIpAddr(request));
            log.setOperTime(LocalDateTime.now());
            
            exceptionLogService.recordExceptionLog(log);
        } catch (Exception ex) {
            // 记录异常日志失败时，直接输出到控制台
            log.error("记录异常日志失败：", ex);
        }
    }
}