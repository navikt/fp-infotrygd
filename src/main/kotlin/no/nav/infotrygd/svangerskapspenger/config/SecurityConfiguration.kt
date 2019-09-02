package no.nav.infotrygd.svangerskapspenger.config

import no.nav.security.spring.oidc.api.EnableOIDCTokenValidation
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

@EnableOIDCTokenValidation(ignore = ["org.springframework"])
@Profile("!noauth")
@Configuration
class SecurityConfiguration
