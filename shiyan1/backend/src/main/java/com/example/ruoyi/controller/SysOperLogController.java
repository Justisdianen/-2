package com.example.ruoyi.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.ruoyi.annotation.Log;
import com.example.ruoyi.common.R;
import com.example.ruoyi.entity.SysOperLog;
import com.example.ruoyi.service.SysOperLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

/**
 * 操作日志Controller
 */
@RestController
@RequestMapping("/system/operlog")
@RequiredArgsConstructor
@Tag(name = "操作日志", description = "操作日志管理接口")
public class SysOperLogController {

    private final SysOperLogService operLogService;

    @GetMapping("/list")
    @Operation(summary = "查询操作日志列表")
    public R<Page<SysOperLog>> list(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String operName,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String beginTime,
            @RequestParam(required = false) String endTime) {
        
        Page<SysOperLog> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<SysOperLog> wrapper = new LambdaQueryWrapper<>();
        
        if (title != null && !title.isEmpty()) {
            wrapper.like(SysOperLog::getTitle, title);
        }
        if (operName != null && !operName.isEmpty()) {
            wrapper.like(SysOperLog::getOperName, operName);
        }
        if (status != null && !status.isEmpty()) {
            wrapper.eq(SysOperLog::getStatus, status);
        }
        if (beginTime != null && !beginTime.isEmpty()) {
            wrapper.ge(SysOperLog::getOperTime, LocalDateTime.parse(beginTime + "T00:00:00"));
        }
        if (endTime != null && !endTime.isEmpty()) {
            wrapper.le(SysOperLog::getOperTime, LocalDateTime.parse(endTime + "T23:59:59"));
        }
        
        wrapper.orderByDesc(SysOperLog::getOperTime);
        
        return R.success(operLogService.page(page, wrapper));
    }

    @DeleteMapping("/{operIds}")
    @Operation(summary = "批量删除操作日志")
    @Log(title = "操作日志", businessType = Log.BusinessType.DELETE)
    public R<Void> remove(@PathVariable Long[] operIds) {
        List<Long> ids = Arrays.asList(operIds);
        operLogService.removeByIds(ids);
        return R.success();
    }

    @DeleteMapping("/clean")
    @Operation(summary = "清空操作日志")
    @Log(title = "操作日志", businessType = Log.BusinessType.CLEAN)
    public R<Void> clean() {
        operLogService.cleanOperLog();
        return R.success();
    }

    @GetMapping("/export")
    @Operation(summary = "导出操作日志")
    @Log(title = "操作日志", businessType = Log.BusinessType.EXPORT)
    public R<String> export() {
        List<SysOperLog> list = operLogService.list();
        return R.success("operlog_export_" + LocalDateTime.now().toString());
    }
}