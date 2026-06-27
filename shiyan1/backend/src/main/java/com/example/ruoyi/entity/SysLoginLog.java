package com.example.ruoyi.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 登录日志实体
 */
@Data
@TableName("sys_login_log")
public class SysLoginLog {

    @TableId(type = IdType.AUTO)
    private Long logId;

    private String userName;

    private String ipaddr;

    private String loginLocation;

    private String browser;

    private String os;

    private String status;

    private String msg;

    private LocalDateTime loginTime;

    private LocalDateTime createTime;
}