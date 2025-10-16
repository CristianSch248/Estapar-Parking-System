package br.com.estapar.parking.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Estapar Parking API")
                        .version("v1")
                        .description("API do desafio: faturamento, eventos de estacionamento e configuração da garagem.")
                        .contact(new Contact().name("Cristian").email("cristianschmitzhaus2000@gmail.com")));
    }
}
