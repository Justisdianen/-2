package com.example.ruoyi.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 操作日志实体
 */
@Data
@TableName("sys_oper_log")
public class SysOperLog {

    @TableId(type = IdType.AUTO)
    private Long operId;

    private String title;

    private String businessType;

    private String method;

    private String requestMethod;

    private String operatorName;

    private String operName;

    private String deptName;

    private String operUrl;

    private String operIp;

    private String operLocation;

    private String operParam;

    private String jsonResult;

    private String status;

    private String errorMsg;

    private LocalDateTime operTime;

    private Long costTime;

    private LocalDateTime createTime;
}