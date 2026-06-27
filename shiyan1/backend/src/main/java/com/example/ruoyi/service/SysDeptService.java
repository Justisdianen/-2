package com.example.ruoyi.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.ruoyi.entity.SysDept;

import java.util.List;

/**
 * 部门表 Service 接口
 */
public interface SysDeptService extends IService<SysDept> {

    /**
     * 查询部门列表（树形结构）
     */
    List<SysDept> selectDeptList(SysDept dept);

    /**
     * 根据部门ID查询部门详情
     */
    SysDept selectDeptById(Long deptId);

    /**
     * 构建部门树形结构
     */
    List<SysDept> buildDeptTree(List<SysDept> depts);

    /**
     * 新增部门
     */
    int insertDept(SysDept dept);

    /**
     * 修改部门
     */
    int updateDept(SysDept dept);

    /**
     * 批量删除部门
     */
    int deleteDeptByIds(Long[] deptIds);

    /**
     * 校验部门名称是否唯一
     */
    boolean checkDeptNameUnique(SysDept dept);

    /**
     * 是否存在子部门
     */
    boolean hasChildByDeptId(Long deptId);

    /**
     * 查询部门是否存在用户
     */
    boolean checkDeptExistUser(Long deptId);
}