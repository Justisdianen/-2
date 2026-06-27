package com.example.ruoyi.entity;

import com.baomidou.mybatisplus.annotation.IdType;
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

/**
 * 角色表 sys_role
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_role")
@Schema(description = "角色实体")
public class SysRole extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 角色编号
     */
    @TableId(value = "role_id", type = IdType.AUTO)
    @Schema(description = "角色编号")
    private Long roleId;

    /**
     * 角色名称
     */
    @NotBlank(message = "角色名称不能为空")
    @Size(min = 2, max = 30, message = "角色名称长度必须在2-30个字符之间")
    @Schema(description = "角色名称")
    private String roleName;

    /**
     * 角色权限标识
     */
    @NotBlank(message = "角色权限标识不能为空")
    @Size(min = 2, max = 100, message = "角色权限标识长度必须在2-100个字符之间")
    @Schema(description = "角色权限标识")
    private String roleKey;

    /**
     * 排序号
     */
    @NotNull(message = "排序号不能为空")
    @Schema(description = "排序号")
    private Integer roleSort;

    /**
     * 数据范围（1-全部数据，2-本部门及以下，3-本部门，4-仅本人，5-自定义）
     */
    @Pattern(regexp = "^[1-5]$", message = "数据范围值必须为1-5")
    @Schema(description = "数据范围（1-全部数据，2-本部门及以下，3-本部门，4-仅本人，5-自定义）")
    private String dataScope;

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
}