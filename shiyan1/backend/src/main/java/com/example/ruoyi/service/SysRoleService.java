package com.example.ruoyi.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.ruoyi.entity.SysRole;

import java.util.List;

/**
 * 角色表 Service 接口
 */
public interface SysRoleService extends IService<SysRole> {

    /**
     * 查询角色列表
     */
    List<SysRole> selectRoleList(SysRole role);

    /**
     * 根据角色ID查询角色详情
     */
    SysRole selectRoleById(Long roleId);

    /**
     * 新增角色
     */
    int insertRole(SysRole role);

    /**
     * 修改角色
     */
    int updateRole(SysRole role);

    /**
     * 批量删除角色
     */
    int deleteRoleByIds(Long[] roleIds);

    /**
     * 校验角色名称是否唯一
     */
    boolean checkRoleNameUnique(String roleName);

    /**
     * 校验角色权限标识是否唯一
     */
    boolean checkRoleKeyUnique(String roleKey);

    /**
     * 根据用户ID查询角色列表
     */
    List<SysRole> selectRolesByUserId(Long userId);
}