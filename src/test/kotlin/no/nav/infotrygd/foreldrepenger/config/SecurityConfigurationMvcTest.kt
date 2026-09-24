package no.nav.infotrygd.foreldrepenger.config

import no.nav.commons.foedselsnummer.FoedselsNr
import no.nav.infotrygd.foreldrepenger.Profiler
import no.nav.infotrygd.foreldrepenger.SecurityTestBase
import no.nav.infotrygd.foreldrepenger.SecurityTestConfiguration
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
import org.springframework.boot.security.oauth2.server.resource.autoconfigure.OAuth2ResourceServerProperties
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.security.access.AccessDeniedException
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.time.LocalDate


@ActiveProfiles(Profiler.TEST)
@WebMvcTest(controllers = [InfotrygdController::class, TableController::class])
@Import(
    InfotrygdController::class,
    TableController::class,
    SecurityConfiguration::class,
    JwtDecoderConfiguration::class,
    SecurityTestConfiguration::class
)
@EnableConfigurationProperties(OAuth2ResourceServerProperties::class)
class SecurityConfigurationMvcTest : SecurityTestBase() {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockitoBean
    private lateinit var sakService: SakService

    @MockitoBean
    private lateinit var grunnlagService: GrunnlagService

    @MockitoBean
    private lateinit var tableIntegrator: TableIntegrator

    @Test
    fun protectedEndpointsRequireToken() {
        for (path in listOf("/sak", "/grunnlag")) {
            mockMvc.perform(post(path).contentType(MediaType.APPLICATION_JSON).content(PERSON_REQUEST))
                .andExpect(status().isUnauthorized)
                .andExpect { assertThat(it.response.contentAsString).isEmpty() }
        }
        mockMvc.perform(get("/restanse"))
            .andExpect(status().isUnauthorized)
        mockMvc.perform(get("/unknown"))
            .andExpect(status().isUnauthorized)
    }

    @Test
    fun invalidAudienceIsUnauthorized() {
        mockMvc.perform(get("/restanse").headers { it.setBearerAuth(jwt("wrong-audience", "app")) })
            .andExpect(status().isUnauthorized)
    }

    @Test
    fun invalidIdtypIsUnauthorized() {
        mockMvc.perform(get("/restanse").headers { it.setBearerAuth(jwt(AUDIENCE, "invalid")) })
            .andExpect(status().isUnauthorized)
    }

    @Test
    fun validAppTokenCanAccessProtectedEndpoint() {
        `when`(sakService.findRestanse()).thenReturn(emptyList())

        mockMvc.perform(get("/restanse").headers { it.setBearerAuth(validCCJwt()) })
            .andExpect(status().isOk)
    }

    @Test
    fun accessDeniedReturnsForbidden() {
        `when`(sakService.findRestanse()).thenThrow(AccessDeniedException("Ingen tilgang"))

        mockMvc.perform(get("/restanse").headers { it.setBearerAuth(validCCJwt()) })
            .andExpect(status().isForbidden)
            .andExpect { assertThat(it.response.contentAsString).isEmpty() }
    }

    @Test
    fun validAppTokenCanAccessBothPostEndpoints() {
        val fnr = FoedselsNr("12345678910")
        val fom = LocalDate.parse("2020-01-01")
        `when`(sakService.findSakerByFnr(fnr, fom, null)).thenReturn(emptyList())
        `when`(grunnlagService.hentYtelse(fnr, fom, null)).thenReturn(emptyList())

        for (path in listOf("/sak", "/grunnlag")) {
            mockMvc.perform(
                post(path).headers { it.setBearerAuth(validCCJwt()) }
                    .contentType(MediaType.APPLICATION_JSON).content(PERSON_REQUEST)
            ).andExpect(status().isOk)
        }
    }

    @Test
    fun tablesCanBeAccessedWithoutToken() {
        `when`(tableIntegrator.tables).thenReturn(emptyMap())

        mockMvc.perform(get(SecurityConfiguration.TABLES_DEFINITIONS_ENDPOINT))
            .andExpect(status().isOk)
    }

    @Test
    fun actuatorHealthIsNotBlockedBySecurity() {
        val response = mockMvc.perform(get("/actuator/health")).andReturn().response
        assertThat(response.status).isNotIn(401, 403)
    }

    companion object {
        private const val PERSON_REQUEST = """{"fnr":["12345678910"],"fom":"2020-01-01"}"""
    }

}
