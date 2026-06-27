package com.example.ruoyi.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.ruoyi.entity.SysUserLock;
import com.example.ruoyi.mapper.SysUserLockMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 登录安全服务(防暴力破解)
 */
@Service
@RequiredArgsConstructor
public class LoginSecurityService extends ServiceImpl<SysUserLockMapper, SysUserLock> {

    private final SysConfigService configService;

    // 本地缓存登录失败次数（备用）
    private static final ConcurrentHashMap<String, Integer> FAIL_COUNT_CACHE = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, LocalDateTime> LOCK_TIME_CACHE = new ConcurrentHashMap<>();

    /**
     * 检查账号是否被锁定
     */
    public boolean isAccountLocked(String userName) {
        // 从数据库查询锁定状态
        LambdaQueryWrapper<SysUserLock> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUserLock::getUserName, userName);
        wrapper.eq(SysUserLock::getStatus, "1"); // 锁定状态
        SysUserLock lock = getOne(wrapper);
        
        if (lock != null && lock.getUnlockTime() != null) {
            // 检查是否已解锁
            if (LocalDateTime.now().isAfter(lock.getUnlockTime())) {
                // 自动解锁
                unlockAccount(userName);
                return false;
            }
            return true;
        }
        
        // 也检查本地缓存
        LocalDateTime lockTime = LOCK_TIME_CACHE.get(userName);
        if (lockTime != null) {
            int lockMinutes = configService.getConfigValueInt("sys.login.lock.time", 10);
            if (LocalDateTime.now().isBefore(lockTime.plusMinutes(lockMinutes))) {
                return true;
            } else {
                // 自动解锁
                LOCK_TIME_CACHE.remove(userName);
                FAIL_COUNT_CACHE.remove(userName);
                return false;
            }
        }
        
        return false;
    }

    /**
     * 记录登录失败
     */
    public void recordLoginFailure(String userName) {
        // 更新数据库记录
        LambdaQueryWrapper<SysUserLock> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUserLock::getUserName, userName);
        SysUserLock lock = getOne(wrapper);
        
        if (lock == null) {
            lock = new SysUserLock();
            lock.setUserName(userName);
            lock.setFailCount(1);
            lock.setStatus("0");
            lock.setCreateTime(LocalDateTime.now());
            lock.setUpdateTime(LocalDateTime.now());
            save(lock);
        } else {
            lock.setFailCount(lock.getFailCount() + 1);
            lock.setUpdateTime(LocalDateTime.now());
            updateById(lock);
        }
        
        // 也更新本地缓存
        Integer failCount = FAIL_COUNT_CACHE.getOrDefault(userName, 0) + 1;
        FAIL_COUNT_CACHE.put(userName, failCount);
        
        // 检查是否达到锁定阈值
        int maxFailCount = configService.getConfigValueInt("sys.login.fail.count", 5);
        if (failCount >= maxFailCount || lock.getFailCount() >= maxFailCount) {
            lockAccount(userName);
        }
    }

    /**
     * 锁定账号
     */
    public void lockAccount(String userName) {
        int lockMinutes = configService.getConfigValueInt("sys.login.lock.time", 10);
        LocalDateTime unlockTime = LocalDateTime.now().plusMinutes(lockMinutes);
        
        // 更新数据库
        LambdaQueryWrapper<SysUserLock> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUserLock::getUserName, userName);
        SysUserLock lock = getOne(wrapper);
        
        if (lock != null) {
            lock.setStatus("1");
            lock.setLockTime(LocalDateTime.now());
            lock.setUnlockTime(unlockTime);
            lock.setUpdateTime(LocalDateTime.now());
            updateById(lock);
        } else {
            lock = new SysUserLock();
            lock.setUserName(userName);
            lock.setFailCount(configService.getConfigValueInt("sys.login.fail.count", 5));
            lock.setStatus("1");
            lock.setLockTime(LocalDateTime.now());
            lock.setUnlockTime(unlockTime);
            lock.setCreateTime(LocalDateTime.now());
            lock.setUpdateTime(LocalDateTime.now());
            save(lock);
        }
        
        // 更新本地缓存
        LOCK_TIME_CACHE.put(userName, LocalDateTime.now());
    }

    /**
     * 解锁账号
     */
    public void unlockAccount(String userName) {
        // 清除数据库锁定状态
        LambdaQueryWrapper<SysUserLock> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUserLock::getUserName, userName);
        SysUserLock lock = getOne(wrapper);
        
        if (lock != null) {
            lock.setStatus("0");
            lock.setFailCount(0);
            lock.setUnlockTime(LocalDateTime.now());
            lock.setUpdateTime(LocalDateTime.now());
            updateById(lock);
        }
        
        // 清除本地缓存
        FAIL_COUNT_CACHE.remove(userName);
        LOCK_TIME_CACHE.remove(userName);
    }

    /**
     * 登录成功后清除失败记录
     */
    public void clearLoginFailure(String userName) {
        LambdaQueryWrapper<SysUserLock> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUserLock::getUserName, userName);
        SysUserLock lock = getOne(wrapper);
        
        if (lock != null) {
            lock.setFailCount(0);
            lock.setStatus("0");
            lock.setUpdateTime(LocalDateTime.now());
            updateById(lock);
        }
        
        // 清除本地缓存
        FAIL_COUNT_CACHE.remove(userName);
        LOCK_TIME_CACHE.remove(userName);
    }

    /**
     * 获取剩余锁定时间（秒）
     */
    public long getRemainingLockTime(String userName) {
        LocalDateTime lockTime = LOCK_TIME_CACHE.get(userName);
        if (lockTime != null) {
            int lockMinutes = configService.getConfigValueInt("sys.login.lock.time", 10);
            LocalDateTime unlockTime = lockTime.plusMinutes(lockMinutes);
            long remaining = ChronoUnit.SECONDS.between(LocalDateTime.now(), unlockTime);
            return Math.max(0, remaining);
        }
        return 0;
    }
}