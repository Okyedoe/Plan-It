package com.example.demo.config.Swagger;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.oas.annotations.EnableOpenApi;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;

import java.util.*;

@Configuration
@EnableOpenApi
public class Swaggerconfig {
    private String version = "V0.1";

    @Bean
    public Docket api() {
        Server localServer = new Server("local","http://localhost:9001","for local usage", Collections.emptyList(),Collections.emptyList()) ;
        Server devServer = new Server("dev","https://dev.wogus4048.shop","for dev testing", Collections.emptyList(),Collections.emptyList());
        Server prodServer = new Server("prod","https://prod.wogus4048.shop","for prod testing", Collections.emptyList(),Collections.emptyList());
        return new Docket(DocumentationType.OAS_30)
//                .consumes(getConsumeContentTypes())
//                .produces(getProduceContentTypes())
                .servers(localServer,devServer,prodServer)
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.any())
                .build()
                .useDefaultResponseMessages(false) //기본 오류메시지 200,400~등이 안나오게함.
                .apiInfo(apiInfo())
                .securityContexts(Arrays.asList(securityContext())) // jwt를 위해 추가 , 이걸설정하면 모든api에서 jwt를 입력해야함.
                .securitySchemes(Arrays.asList(apiKey()));  //jwt를 위해 추가
    }

//    private Set<String> getConsumeContentTypes() {
//        Set<String> consumes = new HashSet<>();
//        consumes.add("application/json;charset=UTF-8");
//        consumes.add("application/x-www-form-urlencoded");
//        return consumes;
//    }
//
//    private Set<String> getProduceContentTypes() {
//        Set<String> produces = new HashSet<>();
//        produces.add("application/json;charset=UTF-8");
//        return produces;
//    }

    private ApiInfo apiInfo() {;

        return new ApiInfoBuilder()
                .title("Planet api 명세서입니다.")
                .description("오류가 발생하면 연락주세요")
                .version(version)
//                .contact(new Contact("서버 : 길재현-오리, 김민수-만두", "홈페이지 URL", "e-mail"))
                .build();
    }
    //ApiKey 정의
    private ApiKey apiKey() {
        return new ApiKey("X-ACCESS-TOKEN", "Authorization", "header");
    }

    //JWT SecurityContext 구성
    private SecurityContext securityContext() {
        return SecurityContext.builder().securityReferences(defaultAuth()).build();
    }

    private List<SecurityReference> defaultAuth() {
        AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEveryThing");
        AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
        authorizationScopes[0] = authorizationScope;
        return Arrays.asList(new SecurityReference("X-ACCESS-TOKEN", authorizationScopes));
    }
}