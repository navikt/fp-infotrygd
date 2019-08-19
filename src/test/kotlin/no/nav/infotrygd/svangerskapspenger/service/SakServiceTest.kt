package no.nav.infotrygd.svangerskapspenger.service

import no.nav.infotrygd.svangerskapspenger.model.Sak
import no.nav.infotrygd.svangerskapspenger.model.Status
import no.nav.infotrygd.svangerskapspenger.repository.SakRepository
import no.nav.infotrygd.svangerskapspenger.rest.dto.SakDto
import no.nav.infotrygd.svangerskapspenger.rest.dto.SakId
import no.nav.infotrygd.svangerskapspenger.rest.dto.SakResult
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
    private val relevantStatus get() = "Y"
    private val fnr get() = "123"

    @Autowired
    lateinit var sakService: SakService

    @MockBean
    lateinit var mockRepository: SakRepository

    @Test
    fun underBehandling() {
        testMM(setOf("S", "R")){it.underBehandling}
    }

    @Test
    fun klagesaker() {
        testMM(setOf("K")){it.klagesaker}
    }

    @Test
    fun ankesaker() {
        testMM(setOf("A")){it.ankesaker}
    }

    fun testMM(relevanteTyper: Set<String>, relevantListe: (SakResult) -> List<SakDto>) {
        val forventet = lagSak(type = relevanteTyper.first())

        Mockito.`when`(mockRepository.findSvangerskapssakerByFnrAndType(fnr, relevanteTyper))
            .thenReturn(listOf(forventet))

        sakService.findSakerByFnr(fnr).also {
            val liste = relevantListe(it)
            assertThat(liste).hasSize(1)
            liste[0].apply {
                assertThat(sakId).isEqualTo(SakId(forventet.saksblokk, forventet.saksnummer.toInt()))
                assertThat(status).isEqualTo(relevantStatus)
                assertThat(resultat).isEqualTo(forventet.resultat)
                assertThat(vedtatt).isEqualTo(forventet.vedtaksdato)
                assertThat(iverksatt).isEqualTo(forventet.iverksattdato)
            }
        }
    }

    private fun lagSak(type: String): Sak {
        return Sak(
            fnr = fnr,
            personKey = 123,
            saksblokk = "x",
            saksnummer = "11",
            kapittelNr = "FA",
            valg = "SV",
            type = type,
            resultat = "xx",
            vedtaksdato = LocalDate.now(),
            iverksattdato = LocalDate.now(),
            status = listOf(
                Status(personKey = 123, saksblokk = "x", saksnummer = "11", lopeNr = 1, status = "X"),
                Status(personKey = 123, saksblokk = "x", saksnummer = "11", lopeNr = 2, status = relevantStatus)
            )
        )
    }
}