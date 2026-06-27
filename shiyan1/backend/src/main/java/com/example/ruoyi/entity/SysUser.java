package com.example.ruoyi.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.example.ruoyi.annotation.SensitiveData;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 用户表 sys_user
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_user")
@Schema(description = "用户实体")
public class SysUser extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 用户编号
     */
    @TableId(value = "user_id", type = IdType.AUTO)
    @Schema(description = "用户编号")
    private Long userId;

    /**
     * 所属部门编号
     */
    @Schema(description = "所属部门编号")
    private Long deptId;

    /**
     * 用户名称（登录账号）
     */
    @NotBlank(message = "用户名称不能为空")
    @Size(min = 2, max = 30, message = "用户名称长度必须在2-30个字符之间")
    @Schema(description = "用户名称（登录账号）")
    private String userName;

    /**
     * 用户昵称（显示名称）
     */
    @NotBlank(message = "用户昵称不能为空")
    @Size(min = 2, max = 30, message = "用户昵称长度必须在2-30个字符之间")
    @Schema(description = "用户昵称（显示名称）")
    private String nickName;

    /**
     * 邮箱地址
     */
    @Email(message = "邮箱地址格式不正确")
    @Size(max = 50, message = "邮箱地址长度不能超过50个字符")
    @Schema(description = "邮箱地址")
    @SensitiveData(type = SensitiveData.SensitiveType.EMAIL)
    private String email;

    /**
     * 手机号码
     */
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号码格式不正确")
    @Schema(description = "手机号码")
    @SensitiveData(type = SensitiveData.SensitiveType.PHONE)
    private String phonenumber;

    /**
     * 性别（0-男，1-女，2-未知）
     */
    @Pattern(regexp = "^[0-2]$", message = "性别值必须为0-男、1-女、2-未知")
    @Schema(description = "性别（0-男，1-女，2-未知）")
    private String sex;

    /**
     * 头像路径
     */
    @Size(max = 100, message = "头像路径长度不能超过100个字符")
    @Schema(description = "头像路径")
    private String avatar;

    /**
     * 密码（BCrypt加密）
     */
    @Size(min = 5, max = 100, message = "密码长度必须在5-100个字符之间")
    @Schema(description = "密码（BCrypt加密）")
    @SensitiveData(type = SensitiveData.SensitiveType.PASSWORD)
    private String password;

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
     * 最后登录IP
     */
    @Schema(description = "最后登录IP")
    private String loginIp;

    /**
     * 最后登录时间
     */
    @Schema(description = "最后登录时间")
    private LocalDateTime loginDate;
}