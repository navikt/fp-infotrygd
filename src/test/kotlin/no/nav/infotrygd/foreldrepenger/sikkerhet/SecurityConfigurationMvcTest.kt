package no.nav.infotrygd.foreldrepenger.sikkerhet

import no.nav.infotrygd.foreldrepenger.Profiler
import no.nav.infotrygd.foreldrepenger.integration.TableIntegrator
import no.nav.infotrygd.foreldrepenger.rest.controller.InfotrygdController
import no.nav.infotrygd.foreldrepenger.rest.controller.TableController
import no.nav.infotrygd.foreldrepenger.service.GrunnlagService
import no.nav.infotrygd.foreldrepenger.service.SakService
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.autoconfigure.ImportAutoConfiguration
import org.springframework.boot.security.oauth2.server.resource.autoconfigure.OAuth2ResourceServerAutoConfiguration
import org.springframework.boot.security.oauth2.server.resource.autoconfigure.OAuth2ResourceServerProperties
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.http.HttpHeaders
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

// Tester sikkerhetskjeden i SecurityConfiguration
@ActiveProfiles(Profiler.TEST)
@WebMvcTest(controllers = [InfotrygdController::class, TableController::class])
@Import(
    InfotrygdController::class, TableController::class, SecurityConfiguration::class
)
@EnableConfigurationProperties(OAuth2ResourceServerProperties::class)
@ImportAutoConfiguration(OAuth2ResourceServerAutoConfiguration::class)
class SecurityConfigurationMvcTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockitoBean
    private lateinit var sakService: SakService

    @MockitoBean
    private lateinit var grunnlagService: GrunnlagService

    @MockitoBean
    private lateinit var tableIntegrator: TableIntegrator

    @MockitoBean
    private lateinit var jwtDecoder: JwtDecoder

    @Test
    fun protectedEndpointsRequireToken() {
        for (path in listOf("/sak", "/grunnlag")) {
            mockMvc.perform(post(path).contentType(MediaType.APPLICATION_JSON).content(PERSON_REQUEST))
                .andExpect(status().isUnauthorized)
        }
        mockMvc.perform(get("/restanse")).andExpect(status().isUnauthorized)
    }

    @Test
    fun missingSystemRoleIsForbidden() {
        for (roles in listOf(emptyList(), listOf("tilfeldig-verdi"))) {
            mockJwtDecodingWithClaimRoles(roles)

            mockMvc.perform(get("/restanse").header(HttpHeaders.AUTHORIZATION, "Bearer " + TOKEN))
                .andExpect(status().isForbidden)
        }
    }

    @Test
    fun validSystemRoleCanAccessProtectedEndpoint() {
        `when`(sakService.findRestanse()).thenReturn(emptyList())
        mockJwtDecodingWithClaimRoles(listOf("access_as_application"))

        mockMvc.perform(get("/restanse").header(HttpHeaders.AUTHORIZATION, "Bearer " + TOKEN))
            .andExpect(status().isOk)
    }

    @Test
    fun tablesCanBeAccessedWithoutToken() {
        `when`(tableIntegrator.tables).thenReturn(emptyMap())

        mockMvc.perform(get(SecurityConfiguration.TABLES_INFO_ENDPOINT)).andExpect(status().isOk)
    }

    @Test
    fun actuatorHealthIsNotBlockedBySecurity() {
        val response = mockMvc.perform(get("/actuator/health")).andReturn().response
        assertThat(response.status).isNotIn(401, 403)
    }

    private fun mockJwtDecodingWithClaimRoles(roles: List<String>) {
        `when`(jwtDecoder.decode(TOKEN)).thenReturn(
            Jwt.withTokenValue(TOKEN)
                .header("alg", "none")
                .audience(listOf(AUDIENCE))
                .claim("roles", roles)
                .build()
        )
    }

    companion object {
        private const val PERSON_REQUEST = """{"fnr":["12345678910"],"fom":"2020-01-01"}"""
        private const val AUDIENCE = "aud-localhost"
        private const val TOKEN = "token"
    }

}
