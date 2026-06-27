/**
 * 敏感数据脱敏序列化器
 * 文件路径: backend/src/main/java/com/example/ruoyi/serializer/SensitiveSerializer.java
 */
package com.example.ruoyi.serializer;

import com.example.ruoyi.annotation.SensitiveData;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;

import java.io.IOException;
import java.util.regex.Pattern;

public class SensitiveSerializer extends JsonSerializer<String> implements ContextualSerializer {

    private SensitiveData.SensitiveType type;
    private int prefix;
    private int suffix;
    private String mask;

    private static final Pattern PHONE_PATTERN = Pattern.compile("(\\d{3})\\d{4}(\\d{4})");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("(\\w{1,2})\\w*@(\\w+)\\.(\\w+)");
    private static final Pattern ID_CARD_PATTERN = Pattern.compile("(\\d{4})\\d{10}(\\d{4})");

    public SensitiveSerializer() {
    }

    public SensitiveSerializer(SensitiveData.SensitiveType type, int prefix, int suffix, String mask) {
        this.type = type;
        this.prefix = prefix;
        this.suffix = suffix;
        this.mask = mask;
    }

    @Override
    public void serialize(String value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if (value == null || value.isEmpty()) {
            gen.writeString(value);
            return;
        }

        String maskedValue = maskValue(value);
        gen.writeString(maskedValue);
    }

    @Override
    public JsonSerializer<?> createContextual(SerializerProvider prov, BeanProperty property) {
        SensitiveData annotation = property.getAnnotation(SensitiveData.class);
        if (annotation != null) {
            return new SensitiveSerializer(
                    annotation.type(),
                    annotation.prefix(),
                    annotation.suffix(),
                    annotation.mask()
            );
        }
        return this;
    }

    private String maskValue(String value) {
        if (type == null) {
            return value;
        }

        return switch (type) {
            case PASSWORD -> "******";
            case PHONE -> PHONE_PATTERN.matcher(value).replaceAll("$1****$2");
            case EMAIL -> EMAIL_PATTERN.matcher(value).replaceAll("$1***@$2.$3");
            case ID_CARD -> ID_CARD_PATTERN.matcher(value).replaceAll("$1**********$2");
            case BANK_CARD -> {
                if (value.length() >= 8) {
                    yield value.substring(0, 4) + "********" + value.substring(value.length() - 4);
                }
                yield "******";
            }
            case CUSTOM -> maskCustom(value);
        };
    }

    private String maskCustom(String value) {
        if (prefix + suffix >= value.length()) {
            return value;
        }
        StringBuilder sb = new StringBuilder();
        if (prefix > 0) {
            sb.append(value.substring(0, Math.min(prefix, value.length())));
        }
        int maskLength = value.length() - prefix - suffix;
        for (int i = 0; i < maskLength; i++) {
            sb.append(mask != null ? mask : "*");
        }
        if (suffix > 0) {
            sb.append(value.substring(value.length() - Math.min(suffix, value.length())));
        }
        return sb.toString();
    }
}