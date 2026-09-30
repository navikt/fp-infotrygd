package no.nav.infotrygd.foreldrepenger.sikkerhet

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.security.oauth2.jwt.Jwt

class EntraJwtAuthenticationConverterTest {

    private val converter = EntraJwtAuthenticationConverter()

    @Test
    fun accessAsApplicationRoleIsMappedToSystemAuthority() {
        val authentication = converter.convert(jwt(listOf("access_as_application")))

        assertThat(authentication.authorities)
            .extracting<String> { it.authority }
            .containsExactly("ROLE_SYSTEM")
    }

    @Test
    fun otherRolesDoNotGrantSystemAuthority() {
        val authentication = converter.convert(jwt(listOf("some_other_role")))

        assertThat(authentication.authorities).isEmpty()
    }

    @Test
    fun missingRolesDoNotGrantSystemAuthority() {
        val authentication = converter.convert(jwt(null))

        assertThat(authentication.authorities).isEmpty()
    }

    private fun jwt(roles: List<String>?): Jwt =
        Jwt.withTokenValue("token")
            .header("alg", "none")
            .subject("subject")
            .apply {
                if (roles != null) {
                    claim("roles", roles)
                }
            }
            .build()
}
