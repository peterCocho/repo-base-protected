package com.churninsight.backend_api.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("ChurnInsight - API de Predicción")
                        .version("1.0")
                        .description("Servicio REST para la detección temprana de deserción de clientes. " +
                                "Integra modelos de Machine Learning y persistencia en PostgreSQL.")
                        .contact(new Contact()
                                .name("Backend Team - ChurnInsight") // Nombre del equipo
                                .url("https://github.com/peterCocho/repo-base-protected/tree/main")) // Tu URL real de GitHub
                        .license(new License()
                                .name("Apache 2.0")
                                .url("http://springdoc.org")));
    }
}
