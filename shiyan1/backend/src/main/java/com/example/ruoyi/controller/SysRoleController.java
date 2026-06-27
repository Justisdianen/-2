package com.example.ruoyi.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.ruoyi.common.R;
import com.example.ruoyi.entity.SysRole;
import com.example.ruoyi.service.SysRoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

/**
 * 角色表 Controller
 * 
 * 文件路径: backend/src/main/java/com/example/ruoyi/controller/SysRoleController.java
 */
@RestController
@RequestMapping("/system/role")
@RequiredArgsConstructor
@Tag(name = "角色管理", description = "角色管理接口")
public class SysRoleController {

    private final SysRoleService sysRoleService;

    /**
     * 查询角色列表
     */
    @GetMapping("/list")
    @Operation(summary = "查询角色列表", description = "根据条件查询角色列表")
    public R<List<SysRole>> list(SysRole role) {
        List<SysRole> list = sysRoleService.selectRoleList(role);
        return R.success(list);
    }

    /**
     * 分页查询角色列表
     */
    @GetMapping("/page")
    @Operation(summary = "分页查询角色", description = "分页查询角色列表，支持按角色名、权限标识、状态筛选")
    public R<IPage<SysRole>> page(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") Integer pageSize,
            SysRole role) {
        Page<SysRole> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(role.getRoleName() != null, SysRole::getRoleName, role.getRoleName());
        wrapper.like(role.getRoleKey() != null, SysRole::getRoleKey, role.getRoleKey());
        wrapper.eq(role.getStatus() != null, SysRole::getStatus, role.getStatus());
        wrapper.orderByAsc(SysRole::getRoleSort);
        IPage<SysRole> result = sysRoleService.page(page, wrapper);
        return R.success(result);
    }

    /**
     * 根据角色ID查询角色详情
     */
    @GetMapping("/{roleId}")
    @Operation(summary = "查询角色详情", description = "根据角色ID查询角色详细信息")
    public R<SysRole> getInfo(
            @Parameter(description = "角色编号") @PathVariable Long roleId) {
        SysRole role = sysRoleService.selectRoleById(roleId);
        return R.success(role);
    }

    /**
     * 新增角色
     */
    @PostMapping
    @Operation(summary = "新增角色", description = "新增角色信息")
    public R<Void> add(@Validated @RequestBody SysRole role) {
        if (!sysRoleService.checkRoleNameUnique(role.getRoleName())) {
            return R.error("新增角色'" + role.getRoleName() + "'失败，角色名称已存在");
        }
        if (!sysRoleService.checkRoleKeyUnique(role.getRoleKey())) {
            return R.error("新增角色'" + role.getRoleName() + "'失败，角色权限已存在");
        }
        sysRoleService.insertRole(role);
        return R.success();
    }

    /**
     * 修改角色
     */
    @PutMapping
    @Operation(summary = "修改角色", description = "修改角色信息")
    public R<Void> edit(@Validated @RequestBody SysRole role) {
        if (!sysRoleService.checkRoleNameUnique(role.getRoleName())) {
            return R.error("修改角色'" + role.getRoleName() + "'失败，角色名称已存在");
        }
        if (!sysRoleService.checkRoleKeyUnique(role.getRoleKey())) {
            return R.error("修改角色'" + role.getRoleName() + "'失败，角色权限已存在");
        }
        sysRoleService.updateRole(role);
        return R.success();
    }

    /**
     * 删除角色
     */
    @DeleteMapping("/{roleIds}")
    @Operation(summary = "删除角色", description = "批量删除角色，支持传入多个角色ID")
    public R<Void> remove(
            @Parameter(description = "角色编号，多个用逗号分隔") @PathVariable Long[] roleIds) {
        if (Arrays.asList(roleIds).contains(1L)) {
            return R.error("不允许删除超级管理员角色");
        }
        sysRoleService.deleteRoleByIds(roleIds);
        return R.success();
    }

    /**
     * 修改角色状态
     */
    @PutMapping("/changeStatus")
    @Operation(summary = "修改角色状态", description = "修改角色状态（启用/禁用）")
    public R<Void> changeStatus(@RequestBody SysRole role) {
        sysRoleService.updateRole(role);
        return R.success();
    }

    /**
     * 查询所有角色列表（用于下拉选择）
     */
    @GetMapping("/optionselect")
    @Operation(summary = "查询角色选项", description = "查询所有启用的角色列表，用于下拉选择")
    public R<List<SysRole>> optionselect() {
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysRole::getStatus, "1");
        wrapper.orderByAsc(SysRole::getRoleSort);
        List<SysRole> roles = sysRoleService.selectRoleList(new SysRole());
        return R.success(roles);
    }
}