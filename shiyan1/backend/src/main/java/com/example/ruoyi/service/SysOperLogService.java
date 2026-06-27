package com.example.ruoyi.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.ruoyi.entity.SysOperLog;
import com.example.ruoyi.mapper.SysOperLogMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * 操作日志服务
 */
@Service
public class SysOperLogService extends ServiceImpl<SysOperLogMapper, SysOperLog> {

    /**
     * 记录操作日志
     */
    public void recordOperLog(SysOperLog operLog) {
        operLog.setOperTime(LocalDateTime.now());
        operLog.setCreateTime(LocalDateTime.now());
        save(operLog);
    }

    /**
     * 清空操作日志
     */
    public void cleanOperLog() {
        baseMapper.delete(null);
    }
}