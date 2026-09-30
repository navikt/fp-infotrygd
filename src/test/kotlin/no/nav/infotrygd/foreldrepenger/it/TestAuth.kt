package no.nav.infotrygd.foreldrepenger.it

import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user
import org.springframework.test.web.servlet.request.RequestPostProcessor

fun autentisertSystem(): RequestPostProcessor =
    user("system").roles("SYSTEM")