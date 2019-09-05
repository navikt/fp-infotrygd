package no.nav.infotrygd.svangerskapspenger.service

import no.nav.infotrygd.svangerskapspenger.model.Periode
import no.nav.infotrygd.svangerskapspenger.model.Sak
import no.nav.infotrygd.svangerskapspenger.model.Status
import no.nav.infotrygd.svangerskapspenger.model.Utbetaling
import no.nav.infotrygd.svangerskapspenger.nextId
import no.nav.infotrygd.svangerskapspenger.repository.PeriodeRepository
import no.nav.infotrygd.svangerskapspenger.repository.SakRepository
import no.nav.infotrygd.svangerskapspenger.rest.dto.*
import no.nav.infotrygd.svangerskapspenger.values.FodselNr
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit4.SpringRunner
import java.time.LocalDate

@RunWith(SpringRunner::class)
@DataJpaTest
@ActiveProfiles("test")
internal class SakServiceTest {
    private val relevantStatus  = "Y"
    private val fnr = FodselNr("10000000001")

    @Autowired
    lateinit var sakRepository: SakRepository

    @Autowired
    lateinit var periodeRepository: PeriodeRepository

    val sakService: SakService
        get() = SakService(sakRepository, periodeRepository)

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
        val utbetaltFom = iverksatt
        val utbetaltTom = iverksatt.plusDays(1)
        val arbufoerSeq = nextId()
        val personKey = nextId()
        val periode = Periode(
            id = nextId(),
            personKey = personKey,
            arbufoerSeq = arbufoerSeq,
            fnr = fnr,
            stoenadstype = "SV",
            frisk = " ",
            arbufoer = iverksatt,
            stoppdato = null,
            utbetalinger = listOf(
                Utbetaling(
                    id = nextId(),
                    personKey = personKey,
                    arbufoerSeq = arbufoerSeq,
                    utbetaltFom = utbetaltFom,
                    utbetaltTom = utbetaltTom
                )
            )
        )

        periodeRepository.save(periode)

        val forventet = listOf(ApenSakMedLopendeUtbetaling(
            iverksatt = iverksatt,
            utbetalinger = listOf(
                UtbetalingDto(
                    utbetaltFom = utbetaltFom,
                    utbetaltTom = utbetaltTom
                )
            )
        ))
        val result = sakService.findSakerByFnr(fnr)
        assertThat(result.apneSakerMedLopendeUtbetaling).isEqualTo(forventet)
    }

    @Test
    fun avsluttedeSaker() {
        val iverksatt = LocalDate.now().minusMonths(1)
        val stoppdato = LocalDate.now().minusWeeks(1)

        val periode = Periode(
            id = nextId(),
            personKey = 1,
            arbufoerSeq = 1,
            fnr = fnr,
            stoenadstype = "SV",
            frisk = "F",
            arbufoer = iverksatt,
            stoppdato = stoppdato,
            utbetalinger = listOf(
                Utbetaling(
                    id = nextId(),
                    personKey = 1,
                    arbufoerSeq = 1,
                    utbetaltFom = iverksatt,
                    utbetaltTom = stoppdato
                )
            )
        )

        val fom = LocalDate.now().minusYears(1)

        periodeRepository.save(periode)

        val forventet = listOf(AvsluttetSak(
            iverksatt = iverksatt,
            stoppdato = stoppdato,
            utbetalinger = listOf(
                UtbetalingDto(
                    utbetaltFom = iverksatt,
                    utbetaltTom = stoppdato
                )
            )
        ))

        val result = sakService.findSakerByFnr(fnr)

        assertThat(result.avsluttedeSaker.fraOgMed).isEqualTo(fom)
        assertThat(result.avsluttedeSaker.saker).isEqualTo(forventet)
    }

    fun testSak(relevanteTyper: Set<String>, relevantListe: (SakResult) -> List<SakDto>) {
        val forventet = lagSak(type = relevanteTyper.first())

        sakRepository.save(forventet)

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
            id = nextId(),
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
                Status(id = nextId(), personKey = 123, saksblokk = "x", saksnummer = "11", lopeNr = 98, status = relevantStatus),
                Status(id = nextId(), personKey = 123, saksblokk = "x", saksnummer = "11", lopeNr = 99, status = "X")
            )
        )
    }
}