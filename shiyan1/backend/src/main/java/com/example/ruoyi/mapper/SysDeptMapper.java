package com.example.ruoyi.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.ruoyi.entity.SysDept;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 部门表 Mapper 接口
 */
@Mapper
public interface SysDeptMapper extends BaseMapper<SysDept> {

    /**
     * 查询部门列表（树形结构）
     */
    List<SysDept> selectDeptList(SysDept dept);

    /**
     * 根据角色ID查询部门ID列表
     */
    List<Long> selectDeptIdsByRoleId(Long roleId);
}