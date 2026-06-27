package com.example.ruoyi.annotation;

import java.lang.annotation.*;

/**
 * 防重复提交注解
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RepeatSubmit {

    /**
     * 间隔时间(ms)，小于此时间视为重复提交
     */
    int interval() default 5000;

    /**
     * 提示消息
     */
    String message() default "请勿重复提交";
}