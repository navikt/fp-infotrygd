package no.nav.infotrygd.svangerskapspenger.config

import no.nav.infotrygd.svangerskapspenger.Profiles
import no.nav.security.spring.oidc.api.EnableOIDCTokenValidation
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

@EnableOIDCTokenValidation(ignore = ["org.springframework", "springfox"])
@Profile("!${Profiles.NOAUTH}")
@Configuration
class SecurityConfiguration
