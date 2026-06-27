package com.example.ruoyi.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.ruoyi.entity.SysUser;

import java.util.List;

/**
 * 用户表 Service 接口
 */
public interface SysUserService extends IService<SysUser> {

    /**
     * 分页查询用户列表
     */
    List<SysUser> selectUserList(SysUser user);

    /**
     * 根据用户ID查询用户详情
     */
    SysUser selectUserById(Long userId);

    /**
     * 新增用户
     */
    int insertUser(SysUser user);

    /**
     * 修改用户
     */
    int updateUser(SysUser user);

    /**
     * 批量删除用户
     */
    int deleteUserByIds(Long[] userIds);

    /**
     * 根据用户名查询用户
     */
    SysUser selectUserByUserName(String userName);

    /**
     * 校验用户名是否唯一
     */
    boolean checkUserNameUnique(String userName);
}