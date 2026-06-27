package com.example.ruoyi.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.ruoyi.entity.SysUserRole;

import java.util.List;

/**
 * 用户角色关联表 Service 接口
 */
public interface SysUserRoleService extends IService<SysUserRole> {

    /**
     * 根据用户ID查询角色ID列表
     */
    List<Long> selectRoleIdsByUserId(Long userId);

    /**
     * 根据角色ID查询用户ID列表
     */
    List<Long> selectUserIdsByRoleId(Long roleId);

    /**
     * 删除用户与角色的关联
     */
    int deleteUserRoleByUserId(Long userId);

    /**
     * 批量删除用户与角色的关联
     */
    int deleteUserRole(Long userId, List<Long> roleIds);

    /**
     * 批量插入用户与角色的关联
     */
    int insertUserRole(Long userId, List<Long> roleIds);
}