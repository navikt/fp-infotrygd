package no.nav.infotrygd.foreldrepenger.it

import no.nav.infotrygd.foreldrepenger.Profiler
import no.nav.infotrygd.foreldrepenger.model.kodeverk.SakType
import no.nav.infotrygd.foreldrepenger.repository.SakRepository
import no.nav.infotrygd.foreldrepenger.rest.dto.PersonRequest
import no.nav.infotrygd.foreldrepenger.rest.dto.SakDto
import no.nav.infotrygd.foreldrepenger.testutil.TestData
import no.nav.infotrygd.foreldrepenger.testutil.body
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import tools.jackson.module.kotlin.jacksonObjectMapper
import java.time.LocalDate

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles(Profiler.TEST)
class SakTest {

    @Autowired
    private lateinit var sakRepository: SakRepository

    @Autowired
    private lateinit var mockMvc: MockMvc

    private val fnr = TestData.foedselsNr()
    private val fom = LocalDate.parse("2020-01-01")

    @Test
    fun sak() {
        val sak = TestData.sak(fnr)
        sakRepository.save(sak)

        val sakDto = mockMvc.perform(
            post("/sak")
                .with(autentisertSystem())
                .contentType(MediaType.APPLICATION_JSON)
                .content(jacksonObjectMapper().writeValueAsString(PersonRequest(fom, null, listOf(fnr.asString))))
        )
            .andExpect(status().isOk)
            .body<List<SakDto>>()
            .single()

        assertThat(sakDto.type.kode).isEqualTo(SakType.A.kode)
    }

}