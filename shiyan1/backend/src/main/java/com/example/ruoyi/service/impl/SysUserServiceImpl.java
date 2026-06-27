package com.example.ruoyi.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.ruoyi.common.UserContext;
import com.example.ruoyi.entity.SysRole;
import com.example.ruoyi.entity.SysUser;
import com.example.ruoyi.mapper.SysUserMapper;
import com.example.ruoyi.service.SysRoleService;
import com.example.ruoyi.service.SysUserService;
import com.example.ruoyi.util.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

/**
 * 用户表 Service 实现类
 */
@Service
@RequiredArgsConstructor
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {

    private final SysUserMapper sysUserMapper;
    private final SysRoleService sysRoleService;

    @Override
    public List<SysUser> selectUserList(SysUser user) {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(user.getUserName() != null, SysUser::getUserName, user.getUserName());
        wrapper.like(user.getNickName() != null, SysUser::getNickName, user.getNickName());
        wrapper.eq(user.getDeptId() != null, SysUser::getDeptId, user.getDeptId());
        wrapper.eq(user.getStatus() != null, SysUser::getStatus, user.getStatus());
        wrapper.orderByDesc(SysUser::getCreateTime);
        
        // 添加数据权限过滤
        addDataScope(wrapper);
        
        return sysUserMapper.selectList(wrapper);
    }
    
    /**
     * 添加数据权限过滤条件
     */
    private void addDataScope(LambdaQueryWrapper<SysUser> wrapper) {
        try {
            String userName = UserContext.getUserName();
            if (StringUtils.isEmpty(userName)) {
                return;
            }
            
            SysUser currentUser = selectUserByUserName(userName);
            if (currentUser == null) {
                return;
            }
            
            List<SysRole> roles = sysRoleService.selectRolesByUserId(currentUser.getUserId());
            if (roles == null || roles.isEmpty()) {
                return;
            }
            
            boolean hasAllData = false;
            for (SysRole role : roles) {
                String dataScope = role.getDataScope();
                if ("1".equals(dataScope)) {
                    hasAllData = true;
                    break;
                }
            }
            
            if (hasAllData) {
                return;
            }
            
            for (SysRole role : roles) {
                String dataScope = role.getDataScope();
                if (dataScope == null) continue;
                
                switch (dataScope) {
                    case "2":
                        wrapper.eq(SysUser::getDeptId, currentUser.getDeptId());
                        break;
                    case "3":
                        wrapper.eq(SysUser::getDeptId, currentUser.getDeptId());
                        break;
                    case "4":
                        wrapper.eq(SysUser::getUserId, currentUser.getUserId());
                        break;
                    default:
                        break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public SysUser selectUserById(Long userId) {
        return sysUserMapper.selectById(userId);
    }

    @Override
    @Transactional
    public int insertUser(SysUser user) {
        return sysUserMapper.insert(user);
    }

    @Override
    @Transactional
    public int updateUser(SysUser user) {
        return sysUserMapper.updateById(user);
    }

    @Override
    @Transactional
    public int deleteUserByIds(Long[] userIds) {
        return sysUserMapper.deleteBatchIds(Arrays.asList(userIds));
    }

    @Override
    public SysUser selectUserByUserName(String userName) {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getUserName, userName);
        return sysUserMapper.selectOne(wrapper);
    }

    @Override
    public boolean checkUserNameUnique(String userName) {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getUserName, userName);
        return sysUserMapper.selectCount(wrapper) == 0;
    }
}