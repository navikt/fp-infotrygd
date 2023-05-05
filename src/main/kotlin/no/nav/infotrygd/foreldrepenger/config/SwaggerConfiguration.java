package no.nav.infotrygd.foreldrepenger.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfiguration {

    @Bean
    public OpenAPI swaggerOpenAPI() {
        return new OpenAPI()
            .info(new Info().title("fp-infotrygd")
                .description("Gir mulighet for å innhente grunnlag fra saker behandlet i infotrygd.")
                .version("v1.0")
                .license(new License().name("MIT").url("http://nav.no")));
    }
}
