package com.example.demo.config.Swagger;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.stereotype.Component;
import springfox.documentation.oas.web.OpenApiTransformationContext;
import springfox.documentation.oas.web.WebMvcOpenApiTransformationFilter;
import springfox.documentation.spi.DocumentationType;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class Workaround implements WebMvcOpenApiTransformationFilter {

    @Override
    public OpenAPI transform(OpenApiTransformationContext<HttpServletRequest> context) {
        OpenAPI openApi = context.getSpecification();

        Server localServer = new Server();
        localServer.setDescription("dev_local");
        localServer.setUrl("http://localhost:9001");

        Server prodServer = new Server();
        prodServer.setDescription("prod");
        prodServer.setUrl("https://prod.wogus4048.shop");

        Server devServer = new Server();
        devServer.setDescription("dev");
        devServer.setUrl("https://dev.wogus4048.shop");



        openApi.setServers(Arrays.asList(localServer,devServer,prodServer));
        return openApi;
    }

    @Override
    public boolean supports(DocumentationType documentationType) {
        return documentationType.equals(DocumentationType.OAS_30);
    }
}
