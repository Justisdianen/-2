package com.example.ruoyi.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.ruoyi.entity.SysDept;
import com.example.ruoyi.entity.SysUser;
import com.example.ruoyi.mapper.SysDeptMapper;
import com.example.ruoyi.mapper.SysUserMapper;
import com.example.ruoyi.service.SysDeptService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 部门表 Service 实现类
 */
@Service
@RequiredArgsConstructor
public class SysDeptServiceImpl extends ServiceImpl<SysDeptMapper, SysDept> implements SysDeptService {

    private final SysDeptMapper sysDeptMapper;
    private final SysUserMapper sysUserMapper;

    @Override
    @Cacheable(value = "deptTreeCache", key = "#dept.deptName + '_' + #dept.status")
    public List<SysDept> selectDeptList(SysDept dept) {
        LambdaQueryWrapper<SysDept> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(dept.getDeptName() != null, SysDept::getDeptName, dept.getDeptName());
        wrapper.eq(dept.getStatus() != null, SysDept::getStatus, dept.getStatus());
        wrapper.orderByAsc(SysDept::getOrderNum);
        return sysDeptMapper.selectList(wrapper);
    }

    @Override
    public SysDept selectDeptById(Long deptId) {
        return sysDeptMapper.selectById(deptId);
    }

    @Override
    public List<SysDept> buildDeptTree(List<SysDept> depts) {
        List<SysDept> returnList = new ArrayList<>();
        List<Long> tempList = depts.stream().map(SysDept::getDeptId).collect(Collectors.toList());
        for (SysDept dept : depts) {
            // 如果是顶级节点, 遍历该父节点的所有子节点
            if (!tempList.contains(dept.getParentId())) {
                recursionFn(depts, dept);
                returnList.add(dept);
            }
        }
        if (returnList.isEmpty()) {
            returnList = depts;
        }
        return returnList;
    }

    /**
     * 递归列表
     */
    private void recursionFn(List<SysDept> list, SysDept t) {
        // 得到子节点列表
        List<SysDept> childList = getChildList(list, t);
        t.setChildren(childList);
        for (SysDept tChild : childList) {
            if (hasChild(list, tChild)) {
                recursionFn(list, tChild);
            }
        }
    }

    /**
     * 得到子节点列表
     */
    private List<SysDept> getChildList(List<SysDept> list, SysDept t) {
        List<SysDept> tlist = new ArrayList<>();
        for (SysDept n : list) {
            if (n.getParentId().equals(t.getDeptId())) {
                tlist.add(n);
            }
        }
        return tlist;
    }

    /**
     * 判断是否有子节点
     */
    private boolean hasChild(List<SysDept> list, SysDept t) {
        return getChildList(list, t).size() > 0;
    }

    @Override
    @Transactional
    @CacheEvict(value = "deptTreeCache", allEntries = true)
    public int insertDept(SysDept dept) {
        SysDept parentDept = sysDeptMapper.selectById(dept.getParentId());
        // 如果父节点不为空, 则设置祖级列表
        if (parentDept != null) {
            dept.setAncestors(parentDept.getAncestors() + "," + dept.getParentId());
        } else {
            dept.setAncestors("0");
        }
        return sysDeptMapper.insert(dept);
    }

    @Override
    @Transactional
    @CacheEvict(value = "deptTreeCache", allEntries = true)
    public int updateDept(SysDept dept) {
        SysDept parentDept = sysDeptMapper.selectById(dept.getParentId());
        SysDept oldDept = sysDeptMapper.selectById(dept.getDeptId());
        // 如果父节点不为空, 则设置祖级列表
        if (parentDept != null) {
            dept.setAncestors(parentDept.getAncestors() + "," + dept.getParentId());
        } else {
            dept.setAncestors("0");
        }
        return sysDeptMapper.updateById(dept);
    }

    @Override
    @Transactional
    @CacheEvict(value = "deptTreeCache", allEntries = true)
    public int deleteDeptByIds(Long[] deptIds) {
        return sysDeptMapper.deleteBatchIds(Arrays.asList(deptIds));
    }

    @Override
    public boolean checkDeptNameUnique(SysDept dept) {
        LambdaQueryWrapper<SysDept> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysDept::getDeptName, dept.getDeptName());
        wrapper.eq(SysDept::getParentId, dept.getParentId());
        wrapper.ne(dept.getDeptId() != null, SysDept::getDeptId, dept.getDeptId());
        return sysDeptMapper.selectCount(wrapper) == 0;
    }

    @Override
    public boolean hasChildByDeptId(Long deptId) {
        LambdaQueryWrapper<SysDept> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysDept::getParentId, deptId);
        return sysDeptMapper.selectCount(wrapper) > 0;
    }

    @Override
    public boolean checkDeptExistUser(Long deptId) {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getDeptId, deptId);
        return sysUserMapper.selectCount(wrapper) > 0;
    }
}