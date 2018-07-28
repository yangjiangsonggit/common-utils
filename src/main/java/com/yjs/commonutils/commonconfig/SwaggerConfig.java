package com.yjs.commonutils.commonconfig;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfig {

    @Bean
    public Docket platformApi() {
        return new Docket(DocumentationType.SWAGGER_2).apiInfo(apiInfo()).forCodeGeneration(true);
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder().title("yjs-API").description("©2016 Copyright. Powered By yjs.")
                // .termsOfServiceUrl("")
                .contact(new springfox.documentation.service.Contact("iBase4J", "", "yjs@163.com")).license("Apache License Version 2.0")
                .licenseUrl("https://github.com/").version("2.0").build();
    }

}