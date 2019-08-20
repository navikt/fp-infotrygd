package no.nav.infotrygd.svangerskapspenger.service

import no.nav.infotrygd.svangerskapspenger.model.Periode
import no.nav.infotrygd.svangerskapspenger.model.Sak
import no.nav.infotrygd.svangerskapspenger.model.Status
import no.nav.infotrygd.svangerskapspenger.repository.PeriodeRepository
import no.nav.infotrygd.svangerskapspenger.repository.SakRepository
import no.nav.infotrygd.svangerskapspenger.rest.dto.*
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
    private val relevantStatus  = "Y"
    private val fnr = "123"

    @Autowired
    lateinit var sakService: SakService

    @MockBean
    lateinit var mockSakRepository: SakRepository

    @MockBean
    lateinit var mockPeriodeRepository: PeriodeRepository

    @Test
    fun underBehandling() {
        testSak(setOf("S", "R")){it.underBehandling}
    }

    @Test
    fun klagesaker() {
        testSak(setOf("K")){it.klagesaker}
    }

    @Test
    fun ankesaker() {
        testSak(setOf("A")){it.ankesaker}
    }

    @Test
    fun apneSakerMedLopendeUtbetaling() {
        val iverksatt = LocalDate.now()
        val periode = Periode(
            fnr = fnr,
            stoenadstype = "SV",
            frisk = " ",
            arbufoer = iverksatt,
            stoppdato = null
        )

        Mockito.`when`(mockPeriodeRepository.findOpneSakerMedLopendeUtbetaling(fnr))
            .thenReturn(listOf(periode))

        val forventet = listOf(ApenSakMedLopendeUtbetaling(iverksatt = iverksatt))
        val result = sakService.findSakerByFnr(fnr)
        assertThat(result.apneSakerMedLopendeUtbetaling).isEqualTo(forventet)
    }

    @Test
    fun avsluttedeSaker() {
        val iverksatt = LocalDate.now().minusMonths(1)
        val stoppdato = LocalDate.now().minusWeeks(1)

        val periode = Periode(
            fnr = fnr,
            stoenadstype = "SV",
            frisk = "F",
            arbufoer = iverksatt,
            stoppdato = stoppdato
        )

        val fom = LocalDate.now().minusYears(1)

        Mockito.`when`(mockPeriodeRepository.findAvsluttedeSakerByFnr(fnr, fom))
            .thenReturn(listOf(periode))

        val forventet = listOf(AvsluttetSak(
            iverksatt = iverksatt,
            stoppdato = stoppdato
        ))

        val result = sakService.findSakerByFnr(fnr)

        assertThat(result.avsluttedeSaker.fraOgMed).isEqualTo(fom)
        assertThat(result.avsluttedeSaker.saker).isEqualTo(forventet)
    }

    fun testSak(relevanteTyper: Set<String>, relevantListe: (SakResult) -> List<SakDto>) {
        val forventet = lagSak(type = relevanteTyper.first())

        Mockito.`when`(mockSakRepository.findSvangerskapssakerByFnrAndType(fnr, relevanteTyper))
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