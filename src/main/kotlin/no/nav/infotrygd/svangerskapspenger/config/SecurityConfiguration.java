package no.nav.infotrygd.svangerskapspenger.config;

import no.nav.security.spring.oidc.api.EnableOIDCTokenValidation;
import org.springframework.context.annotation.Configuration;

@EnableOIDCTokenValidation
@Configuration
public class SecurityConfiguration {
}
