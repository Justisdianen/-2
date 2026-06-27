package com.example.ruoyi.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.ruoyi.entity.SysMenu;

import java.util.List;

/**
 * 菜单表 Service 接口
 */
public interface SysMenuService extends IService<SysMenu> {

    /**
     * 查询菜单列表
     */
    List<SysMenu> selectMenuList(SysMenu menu);

    /**
     * 根据菜单ID查询菜单详情
     */
    SysMenu selectMenuById(Long menuId);

    /**
     * 构建菜单树形结构
     */
    List<SysMenu> buildMenuTree(List<SysMenu> menus);

    /**
     * 新增菜单
     */
    int insertMenu(SysMenu menu);

    /**
     * 修改菜单
     */
    int updateMenu(SysMenu menu);

    /**
     * 批量删除菜单
     */
    int deleteMenuByIds(Long[] menuIds);

    /**
     * 根据用户ID查询菜单列表
     */
    List<SysMenu> selectMenusByUserId(Long userId);

    /**
     * 校验菜单名称是否唯一
     */
    boolean checkMenuNameUnique(SysMenu menu);

    /**
     * 是否存在子菜单
     */
    boolean hasChildByMenuId(Long menuId);
}