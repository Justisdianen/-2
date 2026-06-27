package com.example.ruoyi.task;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.ruoyi.entity.SysLoginLog;
import com.example.ruoyi.entity.SysOperLog;
import com.example.ruoyi.entity.SysExceptionLog;
import com.example.ruoyi.entity.SysUserSession;
import com.example.ruoyi.mapper.SysLoginLogMapper;
import com.example.ruoyi.mapper.SysOperLogMapper;
import com.example.ruoyi.mapper.SysExceptionLogMapper;
import com.example.ruoyi.mapper.SysUserSessionMapper;
import com.example.ruoyi.service.SysConfigService;
import com.example.ruoyi.service.TokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 定时任务：清理过期日志数据
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class LogCleanTask {

    private final SysLoginLogMapper loginLogMapper;
    private final SysOperLogMapper operLogMapper;
    private final SysExceptionLogMapper exceptionLogMapper;
    private final SysUserSessionMapper userSessionMapper;
    private final SysConfigService configService;
    private final TokenService tokenService;

    /**
     * 每天凌晨1点清理过期日志
     */
    @Scheduled(cron = "0 0 1 * * ?")
    public void cleanExpiredLogs() {
        log.info("开始清理过期日志数据...");
        
        try {
            // 获取保留天数
            int retentionDays = configService.getConfigValueInt("sys.log.retention.days", 30);
            LocalDateTime cutoffTime = LocalDateTime.now().minusDays(retentionDays);
            
            // 清理登录日志
            LambdaQueryWrapper<SysLoginLog> loginWrapper = new LambdaQueryWrapper<>();
            loginWrapper.lt(SysLoginLog::getLoginTime, cutoffTime);
            int loginLogCount = Math.toIntExact(loginLogMapper.delete(loginWrapper));
            log.info("清理登录日志 {} 条", loginLogCount);
            
            // 清理操作日志
            LambdaQueryWrapper<SysOperLog> operWrapper = new LambdaQueryWrapper<>();
            operWrapper.lt(SysOperLog::getOperTime, cutoffTime);
            int operLogCount = Math.toIntExact(operLogMapper.delete(operWrapper));
            log.info("清理操作日志 {} 条", operLogCount);
            
            // 清理异常日志
            LambdaQueryWrapper<SysExceptionLog> exceptionWrapper = new LambdaQueryWrapper<>();
            exceptionWrapper.lt(SysExceptionLog::getOperTime, cutoffTime);
            int exceptionLogCount = Math.toIntExact(exceptionLogMapper.delete(exceptionWrapper));
            log.info("清理异常日志 {} 条", exceptionLogCount);
            
            log.info("过期日志清理完成，共清理 {} 条", loginLogCount + operLogCount + exceptionLogCount);
        } catch (Exception e) {
            log.error("清理过期日志失败：", e);
        }
    }

    /**
     * 每小时清理过期会话
     */
    @Scheduled(cron = "0 0 * * * ?")
    public void cleanExpiredSessions() {
        log.info("开始清理过期会话...");
        
        try {
            tokenService.cleanExpiredSessions();
            
            // 清理数据库中的过期会话
            LambdaQueryWrapper<SysUserSession> wrapper = new LambdaQueryWrapper<>();
            wrapper.lt(SysUserSession::getExpireTime, LocalDateTime.now());
            int count = Math.toIntExact(userSessionMapper.delete(wrapper));
            log.info("清理过期会话 {} 条", count);
        } catch (Exception e) {
            log.error("清理过期会话失败：", e);
        }
    }
}