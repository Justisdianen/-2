package com.example.ruoyi.controller;

import com.example.ruoyi.common.R;
import com.example.ruoyi.entity.SysMenu;
import com.example.ruoyi.service.SysMenuService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

/**
 * 菜单表 Controller
 * 
 * 文件路径: backend/src/main/java/com/example/ruoyi/controller/SysMenuController.java
 */
@RestController
@RequestMapping("/system/menu")
@RequiredArgsConstructor
@Tag(name = "菜单管理", description = "菜单管理接口")
public class SysMenuController {

    private final SysMenuService sysMenuService;

    /**
     * 查询菜单列表
     */
    @GetMapping("/list")
    @Operation(summary = "查询菜单列表", description = "根据条件查询菜单列表")
    public R<List<SysMenu>> list(SysMenu menu) {
        List<SysMenu> menus = sysMenuService.selectMenuList(menu);
        return R.success(menus);
    }

    /**
     * 查询菜单树形结构
     */
    @GetMapping("/tree")
    @Operation(summary = "查询菜单树", description = "查询菜单树形结构，用于菜单管理页面")
    public R<List<SysMenu>> tree(SysMenu menu) {
        List<SysMenu> menus = sysMenuService.selectMenuList(menu);
        List<SysMenu> tree = sysMenuService.buildMenuTree(menus);
        return R.success(tree);
    }

    /**
     * 根据菜单ID查询菜单详情
     */
    @GetMapping("/{menuId}")
    @Operation(summary = "查询菜单详情", description = "根据菜单ID查询菜单详细信息")
    public R<SysMenu> getInfo(
            @Parameter(description = "菜单编号") @PathVariable Long menuId) {
        SysMenu menu = sysMenuService.selectMenuById(menuId);
        return R.success(menu);
    }

    /**
     * 新增菜单
     */
    @PostMapping
    @Operation(summary = "新增菜单", description = "新增菜单信息")
    public R<Void> add(@Validated @RequestBody SysMenu menu) {
        if (!sysMenuService.checkMenuNameUnique(menu)) {
            return R.error("新增菜单'" + menu.getMenuName() + "'失败，菜单名称已存在");
        }
        sysMenuService.insertMenu(menu);
        return R.success();
    }

    /**
     * 修改菜单
     */
    @PutMapping
    @Operation(summary = "修改菜单", description = "修改菜单信息")
    public R<Void> edit(@Validated @RequestBody SysMenu menu) {
        if (!sysMenuService.checkMenuNameUnique(menu)) {
            return R.error("修改菜单'" + menu.getMenuName() + "'失败，菜单名称已存在");
        }
        if (menu.getMenuId().equals(menu.getParentId())) {
            return R.error("修改菜单'" + menu.getMenuName() + "'失败，上级菜单不能选择自己");
        }
        sysMenuService.updateMenu(menu);
        return R.success();
    }

    /**
     * 删除菜单
     */
    @DeleteMapping("/{menuIds}")
    @Operation(summary = "删除菜单", description = "批量删除菜单，支持传入多个菜单ID")
    public R<Void> remove(
            @Parameter(description = "菜单编号，多个用逗号分隔") @PathVariable Long[] menuIds) {
        for (Long menuId : menuIds) {
            if (sysMenuService.hasChildByMenuId(menuId)) {
                SysMenu menu = sysMenuService.selectMenuById(menuId);
                return R.error("存在子菜单,不允许删除" + menu.getMenuName());
            }
        }
        sysMenuService.deleteMenuByIds(menuIds);
        return R.success();
    }

    /**
     * 根据用户ID查询菜单列表（用于权限控制）
     */
    @GetMapping("/userMenu/{userId}")
    @Operation(summary = "查询用户菜单", description = "根据用户ID查询该用户有权限的菜单列表")
    public R<List<SysMenu>> getUserMenu(
            @Parameter(description = "用户编号") @PathVariable Long userId) {
        List<SysMenu> menus = sysMenuService.selectMenusByUserId(userId);
        List<SysMenu> tree = sysMenuService.buildMenuTree(menus);
        return R.success(tree);
    }
}