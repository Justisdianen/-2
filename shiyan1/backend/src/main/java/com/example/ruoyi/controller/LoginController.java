package com.example.ruoyi.controller;

import com.example.ruoyi.annotation.Log;
import com.example.ruoyi.annotation.RepeatSubmit;
import com.example.ruoyi.common.R;
import com.example.ruoyi.entity.LoginBody;
import com.example.ruoyi.entity.SysUser;
import com.example.ruoyi.service.*;
import com.example.ruoyi.util.IpUtils;
import com.example.ruoyi.util.JwtUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/")
@RequiredArgsConstructor
@Tag(name = "登录管理", description = "登录和退出接口")
public class LoginController {

    private final SysUserService sysUserService;
    private final JwtUtils jwtUtils;
    private final BCryptPasswordEncoder passwordEncoder;
    private final LoginSecurityService loginSecurityService;
    private final TokenService tokenService;
    private final SysLoginLogService loginLogService;
    private final SysConfigService configService;

    @PostMapping("/login")
    @Operation(summary = "用户登录", description = "根据用户名和密码进行登录")
    @RepeatSubmit(interval = 3000, message = "请勿频繁登录")
    @Log(title = "用户登录", businessType = Log.BusinessType.OTHER)
    public R<Map<String, Object>> login(@Validated @RequestBody LoginBody loginBody, HttpServletRequest request) {
        String userName = loginBody.getUsername();
        String ipaddr = IpUtils.getIpAddr(request);
        String browser = IpUtils.getBrowser(request);
        String os = IpUtils.getOs(request);

        // 1. 检查账号是否被锁定
        if (loginSecurityService.isAccountLocked(userName)) {
            long remainingTime = loginSecurityService.getRemainingLockTime(userName);
            loginLogService.recordLoginLog(userName, ipaddr, "1", 
                    "账号已被锁定，请" + remainingTime + "秒后重试", browser, os);
            return R.error("账号已被锁定，请" + (remainingTime / 60) + "分钟后重试");
        }

        // 2. 查询用户
        SysUser user = sysUserService.selectUserByUserName(userName);
        
        if (user == null) {
            loginSecurityService.recordLoginFailure(userName);
            loginLogService.recordLoginLog(userName, ipaddr, "1", "用户名或密码错误", browser, os);
            return R.error("用户名或密码错误");
        }

        // 3. 验证密码
        if (!passwordEncoder.matches(loginBody.getPassword(), user.getPassword())) {
            loginSecurityService.recordLoginFailure(userName);
            loginLogService.recordLoginLog(userName, ipaddr, "1", "用户名或密码错误", browser, os);
            return R.error("用户名或密码错误");
        }

        // 4. 检查用户状态
        if ("0".equals(user.getStatus())) {
            loginLogService.recordLoginLog(userName, ipaddr, "1", "用户已被禁用", browser, os);
            return R.error("用户已被禁用");
        }

        // 5. 登录成功，清除失败记录
        loginSecurityService.clearLoginFailure(userName);

        // 6. 生成Token并创建会话
        String token = jwtUtils.generateToken(user.getUserId(), user.getUserName());
        tokenService.createSession(user.getUserId(), user.getUserName(), token, ipaddr, browser, os);

        // 7. 记录登录日志
        loginLogService.recordLoginLog(userName, ipaddr, "0", "登录成功", browser, os);

        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("user", user);
        
        return R.success(result);
    }

    @PostMapping("/logout")
    @Operation(summary = "退出登录", description = "清除登录状态和Token")
    @Log(title = "用户退出", businessType = Log.BusinessType.OTHER)
    public R<Void> logout(@RequestHeader(value = "Authorization", required = false) String authorization) {
        if (authorization != null) {
            String token = jwtUtils.extractTokenFromHeader(authorization);
            if (token != null) {
                tokenService.removeSession(token);
            }
        }
        R<Void> result = R.success();
        result.setMsg("退出成功");
        return result;
    }

    @GetMapping("/captcha")
    @Operation(summary = "获取验证码", description = "返回验证码是否启用")
    public R<Map<String, Object>> captcha() {
        Map<String, Object> result = new HashMap<>();
        boolean captchaEnabled = configService.getConfigValueBool("sys.captcha.enable", true);
        result.put("captchaEnabled", captchaEnabled);
        return R.success(result);
    }

    @GetMapping("/info")
    @Operation(summary = "获取当前用户信息", description = "获取当前登录用户的信息")
    public R<SysUser> getUserInfo() {
        Long userId = com.example.ruoyi.common.UserContext.getUserId();
        if (userId == null) {
            return R.error("未登录");
        }
        SysUser user = sysUserService.getById(userId);
        if (user != null) {
            // 手机号脱敏处理
            if (user.getPhonenumber() != null) {
                user.setPhonenumber(com.example.ruoyi.util.DesensitizedUtil.mobile(user.getPhonenumber()));
            }
        }
        return R.success(user);
    }

    @GetMapping("/resetPwd")
    @Operation(summary = "重置密码", description = "重置admin密码为123456（临时接口）")
    public R<String> resetPwd() {
        SysUser user = sysUserService.selectUserByUserName("admin");
        if (user != null) {
            user.setPassword(passwordEncoder.encode("123456"));
            sysUserService.updateUser(user);
            return R.success("密码已重置为123456");
        }
        return R.error("用户不存在");
    }
}