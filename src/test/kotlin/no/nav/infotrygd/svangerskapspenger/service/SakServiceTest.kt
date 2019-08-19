package no.nav.infotrygd.svangerskapspenger.service

import no.nav.infotrygd.svangerskapspenger.model.Sak
import no.nav.infotrygd.svangerskapspenger.repository.SakRepository
import no.nav.infotrygd.svangerskapspenger.rest.dto.SakId
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.SpringRunner
import java.time.LocalDate

@RunWith(SpringRunner::class)
@ContextConfiguration(classes = [
    SakService::class
])
internal class SakServiceTest {

    @Autowired
    lateinit var sakService: SakService

    @MockBean
    lateinit var mockRepository: SakRepository

    @Test
    fun underBehandling() {
        val forventet = lagSak(
            fnr = "123",
            kapittelNr = "FA",
            valg = "SV",
            type = "S"
        )

        Mockito.`when`(mockRepository.findSvangerskapssakerByFnrAndType("123", setOf("S", "R")))
            .thenReturn(listOf(forventet))

        sakService.findSakerByFnr("123").also {
            assertThat(it.underBehandling).hasSize(1)
            it.underBehandling[0].apply {
                assertThat(sakId).isEqualTo(SakId(forventet.saksblokk, forventet.saksnummer.toInt()))
//                assertThat(status).isEqualTo() // S15_STATUS
                assertThat(resultat).isEqualTo(forventet.resultat)
                assertThat(vedtatt).isEqualTo(forventet.vedtaksdato)
                assertThat(iverksatt).isEqualTo(forventet.iverksattdato)
            }
        }
    }

    private fun lagSak(fnr: String, kapittelNr: String, valg: String, type: String): Sak {
        return Sak(
            fnr = fnr,
            personKey = 123,
            saksblokk = "x",
            saksnummer = "11",
            kapittelNr = kapittelNr,
            valg = valg,
            type = type,
            resultat = "xx",
            vedtaksdato = LocalDate.now(),
            iverksattdato = LocalDate.now()
        )
    }
}