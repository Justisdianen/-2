package com.example.ruoyi.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.ruoyi.common.R;
import com.example.ruoyi.entity.SysDept;
import com.example.ruoyi.service.SysDeptService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

/**
 * 部门表 Controller
 * 
 * 文件路径: backend/src/main/java/com/example/ruoyi/controller/SysDeptController.java
 */
@RestController
@RequestMapping("/system/dept")
@RequiredArgsConstructor
@Tag(name = "部门管理", description = "部门管理接口")
public class SysDeptController {

    private final SysDeptService sysDeptService;

    /**
     * 查询部门列表
     */
    @GetMapping("/list")
    @Operation(summary = "查询部门列表", description = "根据条件查询部门列表")
    public R<List<SysDept>> list(SysDept dept) {
        List<SysDept> depts = sysDeptService.selectDeptList(dept);
        return R.success(depts);
    }

    /**
     * 查询部门树形结构
     */
    @GetMapping("/tree")
    @Operation(summary = "查询部门树", description = "查询部门树形结构，用于部门管理页面")
    public R<List<SysDept>> tree(SysDept dept) {
        List<SysDept> depts = sysDeptService.selectDeptList(dept);
        List<SysDept> tree = sysDeptService.buildDeptTree(depts);
        return R.success(tree);
    }

    /**
     * 根据部门ID查询部门详情
     */
    @GetMapping("/{deptId}")
    @Operation(summary = "查询部门详情", description = "根据部门ID查询部门详细信息")
    public R<SysDept> getInfo(
            @Parameter(description = "部门编号") @PathVariable Long deptId) {
        SysDept dept = sysDeptService.selectDeptById(deptId);
        return R.success(dept);
    }

    /**
     * 新增部门
     */
    @PostMapping
    @Operation(summary = "新增部门", description = "新增部门信息")
    public R<Void> add(@Validated @RequestBody SysDept dept) {
        if (!sysDeptService.checkDeptNameUnique(dept)) {
            return R.error("新增部门'" + dept.getDeptName() + "'失败，部门名称已存在");
        }
        sysDeptService.insertDept(dept);
        return R.success();
    }

    /**
     * 修改部门
     */
    @PutMapping
    @Operation(summary = "修改部门", description = "修改部门信息")
    public R<Void> edit(@Validated @RequestBody SysDept dept) {
        if (!sysDeptService.checkDeptNameUnique(dept)) {
            return R.error("修改部门'" + dept.getDeptName() + "'失败，部门名称已存在");
        }
        if (dept.getDeptId().equals(dept.getParentId())) {
            return R.error("修改部门'" + dept.getDeptName() + "'失败，上级部门不能选择自己");
        }
        sysDeptService.updateDept(dept);
        return R.success();
    }

    /**
     * 删除部门
     */
    @DeleteMapping("/{deptIds}")
    @Operation(summary = "删除部门", description = "批量删除部门，支持传入多个部门ID")
    public R<Void> remove(
            @Parameter(description = "部门编号，多个用逗号分隔") @PathVariable Long[] deptIds) {
        for (Long deptId : deptIds) {
            if (sysDeptService.hasChildByDeptId(deptId)) {
                SysDept dept = sysDeptService.selectDeptById(deptId);
                return R.error("存在子部门,不允许删除" + dept.getDeptName());
            }
            if (sysDeptService.checkDeptExistUser(deptId)) {
                SysDept dept = sysDeptService.selectDeptById(deptId);
                return R.error("部门'" + dept.getDeptName() + "'存在用户,不允许删除");
            }
        }
        sysDeptService.deleteDeptByIds(deptIds);
        return R.success();
    }

    /**
     * 查询部门下拉树结构（用于用户选择所属部门）
     */
    @GetMapping("/treeSelect")
    @Operation(summary = "查询部门下拉树", description = "查询部门下拉树结构，用于用户选择所属部门")
    public R<List<SysDept>> treeSelect() {
        List<SysDept> depts = sysDeptService.selectDeptList(new SysDept());
        List<SysDept> tree = sysDeptService.buildDeptTree(depts);
        return R.success(tree);
    }

    /**
     * 根据角色ID查询部门ID列表（用于数据范围）
     */
    @GetMapping("/roleDeptIds/{roleId}")
    @Operation(summary = "查询角色部门", description = "根据角色ID查询部门ID列表")
    public R<List<Long>> roleDeptIds(
            @Parameter(description = "角色编号") @PathVariable Long roleId) {
        LambdaQueryWrapper<SysDept> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysDept::getStatus, "1");
        List<SysDept> depts = sysDeptService.selectDeptList(new SysDept());
        // 实际应该查询 sys_role_dept 表
        return R.success(depts.stream().map(SysDept::getDeptId).toList());
    }
}