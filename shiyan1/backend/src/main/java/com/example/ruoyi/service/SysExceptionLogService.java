package com.example.ruoyi.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.ruoyi.entity.SysExceptionLog;
import com.example.ruoyi.mapper.SysExceptionLogMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * 异常日志服务
 */
@Service
public class SysExceptionLogService extends ServiceImpl<SysExceptionLogMapper, SysExceptionLog> {

    /**
     * 记录异常日志
     */
    public void recordExceptionLog(SysExceptionLog log) {
        log.setOperTime(LocalDateTime.now());
        log.setCreateTime(LocalDateTime.now());
        save(log);
    }
}