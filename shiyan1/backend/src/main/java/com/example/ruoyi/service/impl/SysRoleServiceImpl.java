package com.example.ruoyi.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.ruoyi.entity.SysRole;
import com.example.ruoyi.mapper.SysRoleMapper;
import com.example.ruoyi.mapper.SysUserRoleMapper;
import com.example.ruoyi.service.SysRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

/**
 * 角色表 Service 实现类
 */
@Service
@RequiredArgsConstructor
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements SysRoleService {

    private final SysRoleMapper sysRoleMapper;
    private final SysUserRoleMapper sysUserRoleMapper;

    @Override
    @Cacheable(value = "roleListCache", key = "#role.roleName + '_' + #role.status")
    public List<SysRole> selectRoleList(SysRole role) {
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(role.getRoleName() != null, SysRole::getRoleName, role.getRoleName());
        wrapper.like(role.getRoleKey() != null, SysRole::getRoleKey, role.getRoleKey());
        wrapper.eq(role.getStatus() != null, SysRole::getStatus, role.getStatus());
        wrapper.orderByAsc(SysRole::getRoleSort);
        return sysRoleMapper.selectList(wrapper);
    }

    @Override
    public SysRole selectRoleById(Long roleId) {
        return sysRoleMapper.selectById(roleId);
    }

    @Override
    @Transactional
    @CacheEvict(value = "roleListCache", allEntries = true)
    public int insertRole(SysRole role) {
        return sysRoleMapper.insert(role);
    }

    @Override
    @Transactional
    @CacheEvict(value = "roleListCache", allEntries = true)
    public int updateRole(SysRole role) {
        return sysRoleMapper.updateById(role);
    }

    @Override
    @Transactional
    @CacheEvict(value = "roleListCache", allEntries = true)
    public int deleteRoleByIds(Long[] roleIds) {
        return sysRoleMapper.deleteBatchIds(Arrays.asList(roleIds));
    }

    @Override
    public boolean checkRoleNameUnique(String roleName) {
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysRole::getRoleName, roleName);
        return sysRoleMapper.selectCount(wrapper) == 0;
    }

    @Override
    public boolean checkRoleKeyUnique(String roleKey) {
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysRole::getRoleKey, roleKey);
        return sysRoleMapper.selectCount(wrapper) == 0;
    }

    @Override
    public List<SysRole> selectRolesByUserId(Long userId) {
        List<Long> roleIds = sysUserRoleMapper.selectRoleIdsByUserId(userId);
        if (roleIds == null || roleIds.isEmpty()) {
            return List.of();
        }
        return sysRoleMapper.selectBatchIds(roleIds);
    }
}