package com.example.ruoyi.controller;

import com.example.ruoyi.common.R;
import com.example.ruoyi.entity.SysUserRole;
import com.example.ruoyi.service.SysUserRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户角色关联表 Controller
 */
@RestController
@RequestMapping("/system/userRole")
@RequiredArgsConstructor
public class SysUserRoleController {

    private final SysUserRoleService sysUserRoleService;

    /**
     * 根据用户ID查询角色ID列表
     */
    @GetMapping("/roleIds/{userId}")
    public R<List<Long>> getRoleIdsByUserId(@PathVariable Long userId) {
        List<Long> roleIds = sysUserRoleService.selectRoleIdsByUserId(userId);
        return R.success(roleIds);
    }

    /**
     * 根据角色ID查询用户ID列表
     */
    @GetMapping("/userIds/{roleId}")
    public R<List<Long>> getUserIdsByRoleId(@PathVariable Long roleId) {
        List<Long> userIds = sysUserRoleService.selectUserIdsByRoleId(roleId);
        return R.success(userIds);
    }

    /**
     * 分配用户角色
     */
    @PostMapping
    public R<Void> assignRole(@RequestBody SysUserRole userRole) {
        // 先删除用户原有的角色
        sysUserRoleService.deleteUserRoleByUserId(userRole.getUserId());
        // 再插入新的角色（如果有）
        // 这里简化处理，实际应该传入角色ID列表
        sysUserRoleService.getBaseMapper().insert(userRole);
        return R.success();
    }

    /**
     * 批量分配用户角色
     */
    @PostMapping("/batch")
    public R<Void> assignRoleBatch(@RequestParam Long userId, @RequestBody List<Long> roleIds) {
        // 先删除用户原有的角色
        sysUserRoleService.deleteUserRoleByUserId(userId);
        // 再批量插入新的角色
        if (roleIds != null && !roleIds.isEmpty()) {
            sysUserRoleService.insertUserRole(userId, roleIds);
        }
        return R.success();
    }

    /**
     * 删除用户角色关联
     */
    @DeleteMapping("/{userId}")
    public R<Void> remove(@PathVariable Long userId) {
        sysUserRoleService.deleteUserRoleByUserId(userId);
        return R.success();
    }
}