package com.example.demo.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Arrays;
import java.util.List;

@Configuration
public class SwaggerConfig {
    private String version = "V0.1";

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.any())
                .build()
                .useDefaultResponseMessages(false) //기본 오류메시지 200,400~등이 안나오게함.
                .apiInfo(apiInfo())
                .securityContexts(Arrays.asList(securityContext())) // jwt를 위해 추가 , 이걸설정하면 모든api에서 jwt를 입력해야함.
                .securitySchemes(Arrays.asList(apiKey()));  //jwt를 위해 추가
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("제목")
                .description("설명")
                .version(version)
                .contact(new Contact("이름", "홈페이지 URL", "e-mail"))
                .build();
    }
    //ApiKey 정의
    private ApiKey apiKey() {
        return new ApiKey("JWT", "X-ACCESS-TOKEN", "header");
    }

    //JWT SecurityContext 구성
    private SecurityContext securityContext() {
        return SecurityContext.builder().securityReferences(defaultAuth()).build();
    }

    private List<SecurityReference> defaultAuth() {
        AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEveryThing");
        AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
        authorizationScopes[0] = authorizationScope;
        return Arrays.asList(new SecurityReference("JWT", authorizationScopes));
    }
}
