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
 * 菜单表 sys_menu
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_menu")
@Schema(description = "菜单实体")
public class SysMenu extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 菜单编号
     */
    @TableId(value = "menu_id", type = IdType.AUTO)
    @Schema(description = "菜单编号")
    private Long menuId;

    /**
     * 菜单名称
     */
    @NotBlank(message = "菜单名称不能为空")
    @Size(min = 1, max = 50, message = "菜单名称长度必须在1-50个字符之间")
    @Schema(description = "菜单名称")
    private String menuName;

    /**
     * 上级菜单编号（顶级菜单为0）
     */
    @NotNull(message = "上级菜单编号不能为空")
    @Schema(description = "上级菜单编号（顶级菜单为0）")
    private Long parentId;

    /**
     * 排序号
     */
    @Schema(description = "排序号")
    private Integer orderNum;

    /**
     * 路由地址
     */
    @Size(max = 200, message = "路由地址长度不能超过200个字符")
    @Schema(description = "路由地址")
    private String path;

    /**
     * 组件路径
     */
    @Size(max = 255, message = "组件路径长度不能超过255个字符")
    @Schema(description = "组件路径")
    private String component;

    /**
     * 路由参数
     */
    @Size(max = 255, message = "路由参数长度不能超过255个字符")
    @Schema(description = "路由参数")
    private String query;

    /**
     * 是否为外链（0-是，1-否）
     */
    @Schema(description = "是否为外链（0-是，1-否）")
    private Integer isFrame;

    /**
     * 是否缓存（0-否，1-是）
     */
    @Schema(description = "是否缓存（0-否，1-是）")
    private Integer isCache;

    /**
     * 菜单类型（1-目录，2-菜单，3-按钮）
     */
    @Pattern(regexp = "^[1-3]$", message = "菜单类型必须为1-目录、2-菜单、3-按钮")
    @Schema(description = "菜单类型（1-目录，2-菜单，3-按钮）")
    private String menuType;

    /**
     * 是否显示（0-隐藏，1-显示）
     */
    @Pattern(regexp = "^[01]$", message = "是否显示值必须为0-隐藏、1-显示")
    @Schema(description = "是否显示（0-隐藏，1-显示）")
    private String visible;

    /**
     * 状态（0-禁用，1-启用）
     */
    @Pattern(regexp = "^[01]$", message = "状态值必须为0-禁用、1-启用")
    @Schema(description = "状态（0-禁用，1-启用）")
    private String status;

    /**
     * 权限标识
     */
    @Size(max = 100, message = "权限标识长度不能超过100个字符")
    @Schema(description = "权限标识")
    private String perms;

    /**
     * 菜单图标
     */
    @Size(max = 100, message = "菜单图标长度不能超过100个字符")
    @Schema(description = "菜单图标")
    private String icon;

    /**
     * 逻辑删除（0-正常，2-删除）
     */
    @TableLogic(value = "0", delval = "2")
    @Schema(description = "逻辑删除标记", hidden = true)
    private String delFlag;

    /**
     * 路由名称
     */
    @Size(max = 100, message = "路由名称长度不能超过100个字符")
    @Schema(description = "路由名称")
    private String routeName;

    /**
     * 是否始终显示（0-否，1-是）
     */
    @Schema(description = "是否始终显示（0-否，1-是）")
    private Integer alwaysShow;

    /**
     * 子菜单列表
     */
    @TableField(exist = false)
    @Schema(description = "子菜单列表")
    private List<SysMenu> children;
}