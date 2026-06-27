package com.example.ruoyi.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.ruoyi.entity.SysUserRole;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 用户角色关联表 Mapper 接口
 */
@Mapper
public interface SysUserRoleMapper extends BaseMapper<SysUserRole> {

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