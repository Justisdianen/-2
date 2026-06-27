package com.example.ruoyi.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.ruoyi.entity.SysUserRole;
import com.example.ruoyi.mapper.SysUserRoleMapper;
import com.example.ruoyi.service.SysUserRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 用户角色关联表 Service 实现类
 */
@Service
@RequiredArgsConstructor
public class SysUserRoleServiceImpl extends ServiceImpl<SysUserRoleMapper, SysUserRole> implements SysUserRoleService {

    private final SysUserRoleMapper sysUserRoleMapper;

    @Override
    public List<Long> selectRoleIdsByUserId(Long userId) {
        return sysUserRoleMapper.selectRoleIdsByUserId(userId);
    }

    @Override
    public List<Long> selectUserIdsByRoleId(Long roleId) {
        return sysUserRoleMapper.selectUserIdsByRoleId(roleId);
    }

    @Override
    @Transactional
    public int deleteUserRoleByUserId(Long userId) {
        return sysUserRoleMapper.deleteUserRoleByUserId(userId);
    }

    @Override
    @Transactional
    public int deleteUserRole(Long userId, List<Long> roleIds) {
        return sysUserRoleMapper.deleteUserRole(userId, roleIds);
    }

    @Override
    @Transactional
    public int insertUserRole(Long userId, List<Long> roleIds) {
        return sysUserRoleMapper.insertUserRole(userId, roleIds);
    }
}