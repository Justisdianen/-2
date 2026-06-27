package com.example.ruoyi.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.ruoyi.annotation.Log;
import com.example.ruoyi.annotation.RepeatSubmit;
import com.example.ruoyi.common.R;
import com.example.ruoyi.entity.SysConfig;
import com.example.ruoyi.service.SysConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 系统配置Controller
 */
@RestController
@RequestMapping("/system/config")
@RequiredArgsConstructor
@Tag(name = "系统配置", description = "系统配置管理接口")
public class SysConfigController {

    private final SysConfigService configService;

    @GetMapping("/list")
    @Operation(summary = "查询配置列表")
    public R<Page<SysConfig>> list(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String configName,
            @RequestParam(required = false) String configKey) {
        
        Page<SysConfig> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<SysConfig> wrapper = new LambdaQueryWrapper<>();
        
        if (configName != null && !configName.isEmpty()) {
            wrapper.like(SysConfig::getConfigName, configName);
        }
        if (configKey != null && !configKey.isEmpty()) {
            wrapper.like(SysConfig::getConfigKey, configKey);
        }
        
        wrapper.orderByAsc(SysConfig::getConfigId);
        
        return R.success(configService.page(page, wrapper));
    }

    @GetMapping("/{configId}")
    @Operation(summary = "查询配置详情")
    public R<SysConfig> getInfo(@PathVariable Long configId) {
        return R.success(configService.getById(configId));
    }

    @GetMapping("/key/{configKey}")
    @Operation(summary = "根据key查询配置值")
    public R<String> getConfigKey(@PathVariable String configKey) {
        return R.success(configService.getConfigValue(configKey));
    }

    @PostMapping
    @Operation(summary = "新增配置")
    @RepeatSubmit(interval = 5000)
    @Log(title = "系统配置", businessType = Log.BusinessType.INSERT)
    public R<Void> add(@RequestBody SysConfig config) {
        // 检查key是否重复
        LambdaQueryWrapper<SysConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysConfig::getConfigKey, config.getConfigKey());
        if (configService.exists(wrapper)) {
            return R.error("配置键名已存在");
        }
        configService.save(config);
        return R.success();
    }

    @PutMapping
    @Operation(summary = "修改配置")
    @RepeatSubmit(interval = 5000)
    @Log(title = "系统配置", businessType = Log.BusinessType.UPDATE)
    public R<Void> edit(@RequestBody SysConfig config) {
        // 检查key是否重复
        LambdaQueryWrapper<SysConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysConfig::getConfigKey, config.getConfigKey());
        wrapper.ne(SysConfig::getConfigId, config.getConfigId());
        if (configService.exists(wrapper)) {
            return R.error("配置键名已存在");
        }
        configService.updateById(config);
        configService.refreshCache();
        return R.success();
    }

    @DeleteMapping("/{configIds}")
    @Operation(summary = "删除配置")
    @Log(title = "系统配置", businessType = Log.BusinessType.DELETE)
    public R<Void> remove(@PathVariable Long[] configIds) {
        for (Long configId : configIds) {
            SysConfig config = configService.getById(configId);
            if (config != null && "Y".equals(config.getConfigType())) {
                return R.error("系统内置配置不能删除");
            }
        }
        configService.removeByIds(java.util.Arrays.asList(configIds));
        configService.refreshCache();
        return R.success();
    }

    @PostMapping("/refreshCache")
    @Operation(summary = "刷新配置缓存")
    @Log(title = "系统配置", businessType = Log.BusinessType.OTHER)
    public R<Void> refreshCache() {
        configService.refreshCache();
        return R.success();
    }
}