package Obsidian.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI ChatSulAPI() {
        Info info = new Info()
                .title("우리 FISA 옵시디언 API")
                .description("우리 FISA 옵시디언 API 명세서")
                .version("1.0.0");

        // API Key 인증 방식
        String apiKeySchemeName = "X-API-KEY";
        SecurityScheme apiKeySecurityScheme = new SecurityScheme()
                .name(apiKeySchemeName)
                .type(SecurityScheme.Type.APIKEY)
                .in(SecurityScheme.In.HEADER)
                .name("X-API-KEY");

        // SecurityRequirement 추가
        SecurityRequirement securityRequirement = new SecurityRequirement()
                .addList(apiKeySchemeName);

        Components components = new Components()
                .addSecuritySchemes(apiKeySchemeName, apiKeySecurityScheme);

        return new OpenAPI()
                .addServersItem(new Server().url("/"))
                .info(info)
                .addSecurityItem(securityRequirement)
                .components(components);
    }
}