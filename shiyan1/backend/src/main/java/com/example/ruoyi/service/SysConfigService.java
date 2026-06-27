package com.example.ruoyi.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.ruoyi.entity.SysConfig;
import com.example.ruoyi.mapper.SysConfigMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 系统参数配置服务
 */
@Service
public class SysConfigService extends ServiceImpl<SysConfigMapper, SysConfig> {

    // 配置缓存
    private static final ConcurrentHashMap<String, String> CONFIG_CACHE = new ConcurrentHashMap<>();

    /**
     * 根据key获取配置值
     */
    public String getConfigValue(String configKey) {
        // 先从缓存获取
        String value = CONFIG_CACHE.get(configKey);
        if (value != null) {
            return value;
        }
        // 从数据库获取
        LambdaQueryWrapper<SysConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysConfig::getConfigKey, configKey);
        SysConfig config = getOne(wrapper);
        if (config != null) {
            CONFIG_CACHE.put(configKey, config.getConfigValue());
            return config.getConfigValue();
        }
        return null;
    }

    /**
     * 获取配置值并转换为整数
     */
    public Integer getConfigValueInt(String configKey, Integer defaultValue) {
        String value = getConfigValue(configKey);
        if (value != null) {
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException e) {
                return defaultValue;
            }
        }
        return defaultValue;
    }

    /**
     * 获取配置值并转换为布尔值
     */
    public Boolean getConfigValueBool(String configKey, Boolean defaultValue) {
        String value = getConfigValue(configKey);
        if (value != null) {
            return "true".equalsIgnoreCase(value) || "1".equals(value);
        }
        return defaultValue;
    }

    /**
     * 刷新配置缓存
     */
    public void refreshCache() {
        CONFIG_CACHE.clear();
        List<SysConfig> configs = list();
        for (SysConfig config : configs) {
            CONFIG_CACHE.put(config.getConfigKey(), config.getConfigValue());
        }
    }

    /**
     * 更新配置
     */
    public boolean updateConfig(String configKey, String configValue) {
        LambdaQueryWrapper<SysConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysConfig::getConfigKey, configKey);
        SysConfig config = getOne(wrapper);
        if (config != null) {
            config.setConfigValue(configValue);
            boolean result = updateById(config);
            if (result) {
                CONFIG_CACHE.put(configKey, configValue);
            }
            return result;
        }
        return false;
    }
}