package com.example.ruoyi.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户登录锁定记录实体(防暴力破解)
 */
@Data
@TableName("sys_user_lock")
public class SysUserLock {

    @TableId(type = IdType.AUTO)
    private Long lockId;

    private String userName;

    private Integer failCount;

    private LocalDateTime lockTime;

    private LocalDateTime unlockTime;

    private String status;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}