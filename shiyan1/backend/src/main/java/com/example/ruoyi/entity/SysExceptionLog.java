package com.example.ruoyi.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 异常日志实体
 */
@Data
@TableName("sys_exception_log")
public class SysExceptionLog {

    @TableId(type = IdType.AUTO)
    private Long logId;

    private String exceptionType;

    private String exceptionMsg;

    private String exceptionStack;

    private String requestUrl;

    private String requestMethod;

    private String requestParam;

    private String operatorName;

    private String operIp;

    private LocalDateTime operTime;

    private LocalDateTime createTime;
}