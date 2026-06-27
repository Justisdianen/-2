package com.example.ruoyi.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.ruoyi.entity.SysMenu;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 菜单表 Mapper 接口
 */
@Mapper
public interface SysMenuMapper extends BaseMapper<SysMenu> {

    /**
     * 根据用户ID查询菜单列表
     */
    List<SysMenu> selectMenusByUserId(Long userId);
}