package com.example.ruoyi.aspect;

import com.example.ruoyi.annotation.DataScope;
import com.example.ruoyi.common.UserContext;
import com.example.ruoyi.entity.SysRole;
import com.example.ruoyi.entity.SysUser;
import com.example.ruoyi.service.SysRoleService;
import com.example.ruoyi.service.SysUserService;
import com.example.ruoyi.util.StringUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;
import java.util.List;

/**
 * 数据权限切面
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class DataScopeAspect {

    private final SysUserService sysUserService;
    private final SysRoleService sysRoleService;

    /**
     * 数据权限过滤
     */
    @Before("@annotation(com.example.ruoyi.annotation.DataScope)")
    public void doBefore(JoinPoint joinPoint) {
        handleDataScope(joinPoint);
    }

    /**
     * 处理数据权限
     */
    private void handleDataScope(JoinPoint joinPoint) {
        try {
            // 获取注解
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            Method method = signature.getMethod();
            DataScope dataScope = method.getAnnotation(DataScope.class);
            if (dataScope == null) {
                return;
            }

            // 获取当前用户
            String userName = UserContext.getUserName();
            if (StringUtils.isEmpty(userName)) {
                return;
            }

            // 查询用户信息
            SysUser user = sysUserService.selectUserByUserName(userName);
            if (user == null) {
                return;
            }

            // 获取角色列表
            List<SysRole> roles = sysRoleService.selectRolesByUserId(user.getUserId());
            if (roles == null || roles.isEmpty()) {
                return;
            }

            // 构建数据权限SQL
            StringBuilder scopeSql = new StringBuilder();
            String deptAlias = dataScope.deptAlias();
            String userAlias = dataScope.userAlias();

            for (SysRole role : roles) {
                String dataScopeType = role.getDataScope();
                if (dataScopeType == null) continue;

                switch (dataScopeType) {
                    case "1": // 本人数据
                        if (StringUtils.isNotEmpty(userAlias)) {
                            scopeSql.append(userAlias).append(".user_id = ").append(user.getUserId());
                        }
                        break;
                    case "2": // 本部门数据
                        if (StringUtils.isNotEmpty(deptAlias)) {
                            scopeSql.append(deptAlias).append(".dept_id = ").append(user.getDeptId());
                        } else if (StringUtils.isNotEmpty(userAlias)) {
                            scopeSql.append(userAlias).append(".dept_id = ").append(user.getDeptId());
                        }
                        break;
                    case "3": // 全部数据
                        // 不添加限制条件
                        return;
                    default:
                        break;
                }
            }

            // 将数据权限SQL存入请求属性中
            if (scopeSql.length() > 0) {
                ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
                if (attributes != null) {
                    HttpServletRequest request = attributes.getRequest();
                    request.setAttribute("dataScopeSql", scopeSql.toString());
                }
            }
        } catch (Exception e) {
            log.error("数据权限处理失败", e);
        }
    }
}