package com.example.ruoyi.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户会话实体(Token管理)
 */
@Data
@TableName("sys_user_session")
public class SysUserSession {

    @TableId(type = IdType.AUTO)
    private Long sessionId;

    private Long userId;

    private String userName;

    private String token;

    private String ipaddr;

    private String browser;

    private String os;

    private LocalDateTime loginTime;

    private LocalDateTime lastAccessTime;

    private LocalDateTime expireTime;

    private String status;

    private LocalDateTime createTime;
}