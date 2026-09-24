package no.nav.infotrygd.foreldrepenger

import no.nav.security.mock.oauth2.MockOAuth2Server
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource

@Configuration
@Profile(Profiler.TEST)
class SecurityTestConfiguration {
    @Bean(destroyMethod = "shutdown")
    fun mockOAuth2Server(): MockOAuth2Server = SecurityTestBase.server
}

abstract class SecurityTestBase {
    protected fun validCCJwt(): String =
        server.issueToken("issuer1", "subject", AUDIENCE, mapOf("idtyp" to "app")).serialize()

    protected fun jwt(audience: String, idtyp: String): String =
        server.issueToken("issuer1", "subject", audience, mapOf("idtyp" to idtyp)).serialize()


    companion object {
        protected const val AUDIENCE = "aud-localhost"
        val server = MockOAuth2Server().apply { start() }

        @JvmStatic
        @DynamicPropertySource
        fun oauthProperties(registry: DynamicPropertyRegistry) {
            registry.add("spring.security.oauth2.resourceserver.jwt.issuer-uri") {
                server.issuerUrl("issuer1").toString()
            }
            registry.add("entra.proxy") { "#{null}" }
        }
    }
}
