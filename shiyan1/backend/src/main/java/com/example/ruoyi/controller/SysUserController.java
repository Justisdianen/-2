package com.example.ruoyi.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.ruoyi.annotation.Log;
import com.example.ruoyi.annotation.RepeatSubmit;
import com.example.ruoyi.annotation.DataScope;
import com.example.ruoyi.common.R;
import com.example.ruoyi.entity.SysUser;
import com.example.ruoyi.service.SysUserService;
import com.example.ruoyi.util.ExcelUtil;
import com.example.ruoyi.vo.UserExportVO;
import com.example.ruoyi.vo.UserImportVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 用户表 Controller
 * 
 * 文件路径: backend/src/main/java/com/example/ruoyi/controller/SysUserController.java
 */
@RestController
@RequestMapping("/system/user")
@RequiredArgsConstructor
@Tag(name = "用户管理", description = "用户管理接口")
public class SysUserController {

    private final SysUserService sysUserService;
    private final PasswordEncoder passwordEncoder;

    /**
     * 查询用户列表
     */
    @GetMapping("/list")
    @Operation(summary = "查询用户列表", description = "根据条件查询用户列表")
    @DataScope(deptAlias = "u", userAlias = "u")
    public R<List<SysUser>> list(SysUser user) {
        List<SysUser> list = sysUserService.selectUserList(user);
        return R.success(list);
    }

    /**
     * 分页查询用户列表
     */
    @GetMapping("/page")
    @Operation(summary = "分页查询用户", description = "分页查询用户列表，支持按用户名、昵称、部门、状态筛选")
    @DataScope(deptAlias = "u", userAlias = "u")
    public R<IPage<SysUser>> page(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") Integer pageSize,
            SysUser user) {
        Page<SysUser> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(user.getUserName() != null, SysUser::getUserName, user.getUserName());
        wrapper.like(user.getNickName() != null, SysUser::getNickName, user.getNickName());
        wrapper.eq(user.getDeptId() != null, SysUser::getDeptId, user.getDeptId());
        wrapper.eq(user.getStatus() != null, SysUser::getStatus, user.getStatus());
        wrapper.orderByDesc(SysUser::getCreateTime);
        IPage<SysUser> result = sysUserService.page(page, wrapper);
        return R.success(result);
    }

    /**
     * 根据用户ID查询用户详情
     */
    @GetMapping("/{userId}")
    @Operation(summary = "查询用户详情", description = "根据用户ID查询用户详细信息")
    public R<SysUser> getInfo(
            @Parameter(description = "用户编号") @PathVariable Long userId) {
        SysUser user = sysUserService.selectUserById(userId);
        return R.success(user);
    }

    /**
     * 新增用户
     */
    @PostMapping
    @Operation(summary = "新增用户", description = "新增用户信息")
    public R<Void> add(@Validated @RequestBody SysUser user) {
        if (!sysUserService.checkUserNameUnique(user.getUserName())) {
            return R.error("新增用户'" + user.getUserName() + "'失败，用户名已存在");
        }
        sysUserService.insertUser(user);
        return R.success();
    }

    /**
     * 修改用户
     */
    @PutMapping
    @Operation(summary = "修改用户", description = "修改用户信息")
    public R<Void> edit(@Validated @RequestBody SysUser user) {
        sysUserService.updateUser(user);
        return R.success();
    }

    /**
     * 删除用户
     */
    @DeleteMapping("/{userIds}")
    @Operation(summary = "删除用户", description = "批量删除用户，支持传入多个用户ID")
    public R<Void> remove(
            @Parameter(description = "用户编号，多个用逗号分隔") @PathVariable Long[] userIds) {
        if (Arrays.asList(userIds).contains(1L)) {
            return R.error("不允许删除超级管理员用户");
        }
        sysUserService.deleteUserByIds(userIds);
        return R.success();
    }

    /**
     * 重置密码
     */
    @PutMapping("/resetPwd")
    @Operation(summary = "重置密码", description = "重置用户密码")
    public R<Void> resetPwd(@RequestBody SysUser user) {
        sysUserService.updateUser(user);
        return R.success();
    }

    /**
     * 修改用户状态
     */
    @PutMapping("/changeStatus")
    @Operation(summary = "修改用户状态", description = "修改用户状态（启用/禁用）")
    public R<Void> changeStatus(@RequestBody SysUser user) {
        sysUserService.updateUser(user);
        return R.success();
    }

    /**
     * 导出用户
     */
    @GetMapping("/export")
    @Operation(summary = "导出用户", description = "导出用户数据到Excel")
    @Log(title = "用户管理", businessType = Log.BusinessType.EXPORT)
    public ResponseEntity<byte[]> export(SysUser user) {
        try {
            List<SysUser> userList = sysUserService.selectUserList(user);
            List<UserExportVO> exportList = new ArrayList<>();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            
            for (SysUser sysUser : userList) {
                UserExportVO vo = new UserExportVO();
                vo.setUserId(sysUser.getUserId());
                vo.setUserName(sysUser.getUserName());
                vo.setNickName(sysUser.getNickName());
                vo.setEmail(sysUser.getEmail());
                vo.setPhonenumber(sysUser.getPhonenumber());
                vo.setSex(sysUser.getSex());
                vo.setStatus(sysUser.getStatus());
                vo.setDeptId(sysUser.getDeptId());
                vo.setCreateTime(sysUser.getCreateTime() != null ? sysUser.getCreateTime().format(formatter) : "");
                exportList.add(vo);
            }

            byte[] data = ExcelUtil.exportExcel(exportList, UserExportVO.class, "用户数据");
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            String fileName = URLEncoder.encode("用户数据.xlsx", StandardCharsets.UTF_8);
            headers.setContentDispositionFormData("attachment", fileName);
            return ResponseEntity.ok().headers(headers).body(data);
        } catch (IOException | IllegalAccessException e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 导入用户
     */
    @PostMapping("/import")
    @Operation(summary = "导入用户", description = "从Excel导入用户数据")
    @Log(title = "用户管理", businessType = Log.BusinessType.IMPORT)
    @RepeatSubmit(interval = 5000, message = "请勿重复提交")
    public R<String> importData(@RequestParam("file") MultipartFile file, @RequestParam(defaultValue = "false") boolean updateSupport) {
        try {
            List<UserImportVO> userList = ExcelUtil.importExcel(file.getInputStream(), UserImportVO.class);
            int successNum = 0;
            int failureNum = 0;
            StringBuilder failureMsg = new StringBuilder();

            for (UserImportVO vo : userList) {
                try {
                    SysUser user = new SysUser();
                    user.setUserName(vo.getUserName());
                    user.setNickName(vo.getNickName());
                    user.setEmail(vo.getEmail());
                    user.setPhonenumber(vo.getPhonenumber());
                    user.setSex(vo.getSex());
                    user.setStatus(vo.getStatus());
                    user.setDeptId(vo.getDeptId());

                    SysUser existUser = sysUserService.selectUserByUserName(vo.getUserName());
                    if (existUser == null) {
                        // 新用户
                        if (vo.getPassword() != null && !vo.getPassword().isEmpty()) {
                            user.setPassword(passwordEncoder.encode(vo.getPassword()));
                        } else {
                            user.setPassword(passwordEncoder.encode("123456")); // 默认密码
                        }
                        sysUserService.insertUser(user);
                        successNum++;
                    } else if (updateSupport) {
                        // 更新用户
                        user.setUserId(existUser.getUserId());
                        sysUserService.updateUser(user);
                        successNum++;
                    } else {
                        failureNum++;
                        failureMsg.append("\n").append(vo.getUserName()).append(" 已存在");
                    }
                } catch (Exception e) {
                    failureNum++;
                    failureMsg.append("\n").append(vo.getUserName()).append(" 导入失败：").append(e.getMessage());
                }
            }

            String message = "导入结果：成功 " + successNum + " 条，失败 " + failureNum + " 条";
            if (failureNum > 0) {
                message += failureMsg.toString();
            }
            return R.success(message);
        } catch (IOException | IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
            return R.error("导入失败：" + e.getMessage());
        }
    }

    /**
     * 下载导入模板
     */
    @GetMapping("/importTemplate")
    @Operation(summary = "下载导入模板", description = "下载用户导入Excel模板")
    public ResponseEntity<byte[]> importTemplate() {
        try {
            List<UserImportVO> templateList = new ArrayList<>();
            UserImportVO template = new UserImportVO();
            template.setUserName("admin");
            template.setNickName("管理员");
            template.setEmail("admin@example.com");
            template.setPhonenumber("15888888888");
            template.setSex("0");
            template.setStatus("0");
            template.setPassword("123456");
            templateList.add(template);

            byte[] data = ExcelUtil.exportExcel(templateList, UserImportVO.class, "用户导入模板");
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            String fileName = URLEncoder.encode("用户导入模板.xlsx", StandardCharsets.UTF_8);
            headers.setContentDispositionFormData("attachment", fileName);
            return ResponseEntity.ok().headers(headers).body(data);
        } catch (IOException | IllegalAccessException e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
}