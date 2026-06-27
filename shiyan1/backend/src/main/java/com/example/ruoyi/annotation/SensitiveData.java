/**
 * 敏感数据脱敏注解
 * 文件路径: backend/src/main/java/com/example/ruoyi/annotation/SensitiveData.java
 */
package com.example.ruoyi.annotation;

import com.example.ruoyi.serializer.SensitiveSerializer;
import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@JacksonAnnotationsInside
@JsonSerialize(using = SensitiveSerializer.class)
public @interface SensitiveData {

    SensitiveType type() default SensitiveType.CUSTOM;

    int prefix() default 0;

    int suffix() default 0;

    String mask() default "*";

    enum SensitiveType {
        PASSWORD,
        PHONE,
        EMAIL,
        ID_CARD,
        BANK_CARD,
        CUSTOM
    }
}