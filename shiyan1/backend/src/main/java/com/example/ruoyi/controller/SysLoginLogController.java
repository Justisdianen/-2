package com.example.ruoyi.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.ruoyi.annotation.Log;
import com.example.ruoyi.common.R;
import com.example.ruoyi.entity.SysLoginLog;
import com.example.ruoyi.service.SysLoginLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

/**
 * 登录日志Controller
 */
@RestController
@RequestMapping("/system/loginlog")
@RequiredArgsConstructor
@Tag(name = "登录日志", description = "登录日志管理接口")
public class SysLoginLogController {

    private final SysLoginLogService loginLogService;

    @GetMapping("/list")
    @Operation(summary = "查询登录日志列表")
    public R<Page<SysLoginLog>> list(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String userName,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String beginTime,
            @RequestParam(required = false) String endTime) {
        
        Page<SysLoginLog> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<SysLoginLog> wrapper = new LambdaQueryWrapper<>();
        
        if (userName != null && !userName.isEmpty()) {
            wrapper.like(SysLoginLog::getUserName, userName);
        }
        if (status != null && !status.isEmpty()) {
            wrapper.eq(SysLoginLog::getStatus, status);
        }
        if (beginTime != null && !beginTime.isEmpty()) {
            wrapper.ge(SysLoginLog::getLoginTime, LocalDateTime.parse(beginTime + "T00:00:00"));
        }
        if (endTime != null && !endTime.isEmpty()) {
            wrapper.le(SysLoginLog::getLoginTime, LocalDateTime.parse(endTime + "T23:59:59"));
        }
        
        wrapper.orderByDesc(SysLoginLog::getLoginTime);
        
        return R.success(loginLogService.page(page, wrapper));
    }

    @DeleteMapping("/{logIds}")
    @Operation(summary = "批量删除登录日志")
    @Log(title = "登录日志", businessType = Log.BusinessType.DELETE)
    public R<Void> remove(@PathVariable Long[] logIds) {
        List<Long> ids = Arrays.asList(logIds);
        loginLogService.removeByIds(ids);
        return R.success();
    }

    @DeleteMapping("/clean")
    @Operation(summary = "清空登录日志")
    @Log(title = "登录日志", businessType = Log.BusinessType.CLEAN)
    public R<Void> clean() {
        loginLogService.cleanLoginLog();
        return R.success();
    }

    @GetMapping("/export")
    @Operation(summary = "导出登录日志")
    @Log(title = "登录日志", businessType = Log.BusinessType.EXPORT)
    public R<String> export() {
        List<SysLoginLog> list = loginLogService.list();
        // 返回导出文件名（前端调用导出接口）
        return R.success("loginlog_export_" + LocalDateTime.now().toString());
    }
}