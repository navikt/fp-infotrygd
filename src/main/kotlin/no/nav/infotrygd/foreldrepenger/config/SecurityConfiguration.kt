package no.nav.infotrygd.foreldrepenger.config

import no.nav.infotrygd.foreldrepenger.Profiles
import no.nav.security.token.support.spring.api.EnableJwtTokenValidation
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

@EnableJwtTokenValidation(ignore = ["org.springframework", "org.springdoc"])
@Profile("!${Profiles.NOAUTH}")
@Configuration
class SecurityConfiguration
