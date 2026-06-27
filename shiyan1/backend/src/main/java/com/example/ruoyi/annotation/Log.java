package com.example.ruoyi.annotation;

import java.lang.annotation.*;

/**
 * 操作日志注解
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Log {

    /**
     * 模块标题 
     */
    String title() default "";

    /**
     * 业务类型
     * 0=其它 1=新增 2=修改 3=删除
     */
    BusinessType businessType() default BusinessType.OTHER;

    /**
     * 是否保存请求参数
     */
    boolean isSaveRequestData() default true;

    /**
     * 是否保存响应参数
     */
    boolean isSaveResponseData() default true;

    /**
     * 排除指定的请求参数字段
     */
    String[] excludeParamNames() default {"password", "token", "oldPassword", "newPassword", "confirmPassword"};

    /**
     * 业务类型枚举
     */
    enum BusinessType {
        OTHER("0", "其它"),
        INSERT("1", "新增"),
        UPDATE("2", "修改"),
        DELETE("3", "删除"),
        GRANT("4", "授权"),
        EXPORT("5", "导出"),
        IMPORT("6", "导入"),
        FORCE("7", "强退"),
        CLEAN("8", "清空");

        private final String code;
        private final String desc;

        BusinessType(String code, String desc) {
            this.code = code;
            this.desc = desc;
        }

        public String getCode() {
            return code;
        }

        public String getDesc() {
            return desc;
        }
    }
}