package com.example.ruoyi.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.ruoyi.entity.SysUserSession;
import com.example.ruoyi.mapper.SysUserSessionMapper;
import com.example.ruoyi.util.JwtUtils;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Token会话管理服务
 */
@Service
@RequiredArgsConstructor
public class TokenService extends ServiceImpl<SysUserSessionMapper, SysUserSession> {

    private final JwtUtils jwtUtils;
    private final SysConfigService configService;

    // Token缓存（用于快速验证）
    private static final ConcurrentHashMap<String, SysUserSession> TOKEN_CACHE = new ConcurrentHashMap<>();
    // 用户ID到Token的映射（用于踢下线）
    private static final ConcurrentHashMap<Long, String> USER_TOKEN_MAP = new ConcurrentHashMap<>();

    /**
     * 创建会话
     */
    public SysUserSession createSession(Long userId, String userName, String token, String ipaddr, String browser, String os) {
        // 先踢掉该用户之前的会话
        kickOutUser(userId);

        int expireSeconds = configService.getConfigValueInt("sys.token.expire", 7200);
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expireTime = now.plusSeconds(expireSeconds);

        SysUserSession session = new SysUserSession();
        session.setUserId(userId);
        session.setUserName(userName);
        session.setToken(token);
        session.setIpaddr(ipaddr);
        session.setBrowser(browser);
        session.setOs(os);
        session.setLoginTime(now);
        session.setLastAccessTime(now);
        session.setExpireTime(expireTime);
        session.setStatus("0"); // 在线
        session.setCreateTime(now);

        save(session);

        // 缓存
        TOKEN_CACHE.put(token, session);
        USER_TOKEN_MAP.put(userId, token);

        return session;
    }

    /**
     * 验证Token
     */
    public boolean validateToken(String token) {
        if (token == null || token.isEmpty()) {
            return false;
        }

        // 先检查本地缓存
        SysUserSession session = TOKEN_CACHE.get(token);
        if (session != null) {
            // 检查是否过期
            if (LocalDateTime.now().isAfter(session.getExpireTime())) {
                removeSession(token);
                return false;
            }
            // 检查会话超时（长时间未操作）
            int sessionTimeoutMinutes = configService.getConfigValueInt("sys.session.timeout", 30);
            if (LocalDateTime.now().isAfter(session.getLastAccessTime().plusMinutes(sessionTimeoutMinutes))) {
                removeSession(token);
                return false;
            }
            return true;
        }

        // 从数据库查询
        LambdaQueryWrapper<SysUserSession> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUserSession::getToken, token);
        wrapper.eq(SysUserSession::getStatus, "0");
        session = getOne(wrapper);

        if (session == null) {
            return false;
        }

        // 检查过期
        if (LocalDateTime.now().isAfter(session.getExpireTime())) {
            removeSession(token);
            return false;
        }

        // 检查会话超时
        int sessionTimeoutMinutes = configService.getConfigValueInt("sys.session.timeout", 30);
        if (LocalDateTime.now().isAfter(session.getLastAccessTime().plusMinutes(sessionTimeoutMinutes))) {
            removeSession(token);
            return false;
        }

        // 更新缓存
        TOKEN_CACHE.put(token, session);
        USER_TOKEN_MAP.put(session.getUserId(), token);

        return true;
    }

    /**
     * 刷新Token最后访问时间
     */
    public void refreshLastAccessTime(String token) {
        SysUserSession session = TOKEN_CACHE.get(token);
        if (session != null) {
            session.setLastAccessTime(LocalDateTime.now());
            TOKEN_CACHE.put(token, session);
            
            // 异步更新数据库
            LambdaQueryWrapper<SysUserSession> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(SysUserSession::getToken, token);
            SysUserSession dbSession = new SysUserSession();
            dbSession.setLastAccessTime(LocalDateTime.now());
            update(dbSession, wrapper);
        }
    }

    /**
     * 检查是否需要刷新Token
     */
    public boolean needRefreshToken(String token) {
        SysUserSession session = TOKEN_CACHE.get(token);
        if (session == null) {
            return false;
        }

        int refreshThresholdSeconds = configService.getConfigValueInt("sys.token.refresh.threshold", 1800);
        LocalDateTime refreshThreshold = session.getExpireTime().minusSeconds(refreshThresholdSeconds);

        return LocalDateTime.now().isAfter(refreshThreshold);
    }

    /**
     * 刷新Token
     */
    public String refreshToken(String oldToken) {
        SysUserSession session = TOKEN_CACHE.get(oldToken);
        if (session == null) {
            return null;
        }

        // 生成新Token
        String newToken = jwtUtils.generateToken(session.getUserId(), session.getUserName());

        // 更新会话
        int expireSeconds = configService.getConfigValueInt("sys.token.expire", 7200);
        LocalDateTime expireTime = LocalDateTime.now().plusSeconds(expireSeconds);

        session.setToken(newToken);
        session.setLastAccessTime(LocalDateTime.now());
        session.setExpireTime(expireTime);

        // 更新数据库
        LambdaQueryWrapper<SysUserSession> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUserSession::getToken, oldToken);
        SysUserSession dbSession = new SysUserSession();
        dbSession.setToken(newToken);
        dbSession.setLastAccessTime(LocalDateTime.now());
        dbSession.setExpireTime(expireTime);
        update(dbSession, wrapper);

        // 更新缓存
        TOKEN_CACHE.remove(oldToken);
        TOKEN_CACHE.put(newToken, session);
        USER_TOKEN_MAP.put(session.getUserId(), newToken);

        return newToken;
    }

    /**
     * 移除会话（退出登录）
     */
    public void removeSession(String token) {
        // 从缓存移除
        SysUserSession session = TOKEN_CACHE.remove(token);
        if (session != null) {
            USER_TOKEN_MAP.remove(session.getUserId());
        }

        // 更新数据库状态为离线
        LambdaQueryWrapper<SysUserSession> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUserSession::getToken, token);
        SysUserSession dbSession = new SysUserSession();
        dbSession.setStatus("1"); // 离线
        update(dbSession, wrapper);
    }

    /**
     * 踢下线某用户的所有会话
     */
    public void kickOutUser(Long userId) {
        String token = USER_TOKEN_MAP.get(userId);
        if (token != null) {
            removeSession(token);
        }

        // 也检查数据库中该用户的其他会话
        LambdaQueryWrapper<SysUserSession> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUserSession::getUserId, userId);
        wrapper.eq(SysUserSession::getStatus, "0");
        List<SysUserSession> sessions = list(wrapper);
        for (SysUserSession session : sessions) {
            TOKEN_CACHE.remove(session.getToken());
            removeById(session.getSessionId());
        }
    }

    /**
     * 获取用户会话信息
     */
    public SysUserSession getSession(String token) {
        return TOKEN_CACHE.get(token);
    }

    /**
     * 根据Token获取用户ID
     */
    public Long getUserIdByToken(String token) {
        SysUserSession session = getSession(token);
        return session != null ? session.getUserId() : null;
    }

    /**
     * 根据Token获取用户名
     */
    public String getUserNameByToken(String token) {
        SysUserSession session = getSession(token);
        return session != null ? session.getUserName() : null;
    }

    /**
     * 清理过期会话
     */
    public void cleanExpiredSessions() {
        LambdaQueryWrapper<SysUserSession> wrapper = new LambdaQueryWrapper<>();
        wrapper.lt(SysUserSession::getExpireTime, LocalDateTime.now());
        List<SysUserSession> expiredSessions = list(wrapper);
        
        for (SysUserSession session : expiredSessions) {
            TOKEN_CACHE.remove(session.getToken());
            USER_TOKEN_MAP.remove(session.getUserId());
        }
        
        // 删除数据库中的过期会话
        remove(wrapper);
    }

    /**
     * 初始化缓存（应用启动时）
     */
    public void initCache() {
        LambdaQueryWrapper<SysUserSession> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUserSession::getStatus, "0");
        wrapper.gt(SysUserSession::getExpireTime, LocalDateTime.now());
        List<SysUserSession> sessions = list(wrapper);
        
        for (SysUserSession session : sessions) {
            TOKEN_CACHE.put(session.getToken(), session);
            USER_TOKEN_MAP.put(session.getUserId(), session.getToken());
        }
    }
}