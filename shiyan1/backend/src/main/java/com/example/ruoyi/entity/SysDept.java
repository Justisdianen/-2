package com.example.ruoyi.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 部门表 sys_dept
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_dept")
@Schema(description = "部门实体")
public class SysDept extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 部门编号
     */
    @TableId(value = "dept_id", type = IdType.AUTO)
    @Schema(description = "部门编号")
    private Long deptId;

    /**
     * 上级部门编号（顶级部门为0）
     */
    @NotNull(message = "上级部门编号不能为空")
    @Schema(description = "上级部门编号（顶级部门为0）")
    private Long parentId;

    /**
     * 部门名称
     */
    @NotBlank(message = "部门名称不能为空")
    @Size(min = 1, max = 100, message = "部门名称长度必须在1-100个字符之间")
    @Schema(description = "部门名称")
    private String deptName;

    /**
     * 部门负责人
     */
    @Size(max = 50, message = "部门负责人长度不能超过50个字符")
    @Schema(description = "部门负责人")
    private String leader;

    /**
     * 部门负责人联系电话
     */
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "联系电话格式不正确")
    @Schema(description = "部门负责人联系电话")
    private String phone;

    /**
     * 排序号
     */
    @Schema(description = "排序号")
    private Integer orderNum;

    /**
     * 状态（0-禁用，1-启用）
     */
    @Pattern(regexp = "^[01]$", message = "状态值必须为0-禁用、1-启用")
    @Schema(description = "状态（0-禁用，1-启用）")
    private String status;

    /**
     * 逻辑删除（0-正常，2-删除）
     */
    @TableLogic(value = "0", delval = "2")
    @Schema(description = "逻辑删除标记", hidden = true)
    private String delFlag;

    /**
     * 祖级列表
     */
    @Size(max = 500, message = "祖级列表长度不能超过500个字符")
    @Schema(description = "祖级列表")
    private String ancestors;

    /**
     * 子部门列表
     */
    @TableField(exist = false)
    @Schema(description = "子部门列表")
    private List<SysDept> children;
}