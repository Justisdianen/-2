package com.example.ruoyi.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("若依管理系统 API文档")
                        .version("1.0.0")
                        .description("若依管理系统接口文档")
                        .contact(new Contact()
                                .name("开发团队")
                                .email("dev@example.com")));
    }
}