package com.example.ruoyi.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.ruoyi.entity.SysLoginLog;
import com.example.ruoyi.mapper.SysLoginLogMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 登录日志服务
 */
@Service
public class SysLoginLogService extends ServiceImpl<SysLoginLogMapper, SysLoginLog> {

    /**
     * 记录登录日志
     */
    public void recordLoginLog(String userName, String ipaddr, String status, String msg, String browser, String os) {
        SysLoginLog log = new SysLoginLog();
        log.setUserName(userName);
        log.setIpaddr(ipaddr);
        log.setStatus(status);
        log.setMsg(msg);
        log.setBrowser(browser);
        log.setOs(os);
        log.setLoginTime(LocalDateTime.now());
        log.setCreateTime(LocalDateTime.now());
        save(log);
    }

    /**
     * 清空登录日志
     */
    public void cleanLoginLog() {
        baseMapper.delete(null);
    }
}