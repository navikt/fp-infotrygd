package no.nav.infotrygd.svangerskapspenger.service

import no.nav.infotrygd.svangerskapspenger.model.Sak
import no.nav.infotrygd.svangerskapspenger.model.Status
import no.nav.infotrygd.svangerskapspenger.model.kodeverk.Frisk
import no.nav.infotrygd.svangerskapspenger.model.kodeverk.SakStatus
import no.nav.infotrygd.svangerskapspenger.model.kodeverk.Stoenadstype
import no.nav.infotrygd.svangerskapspenger.nextId
import no.nav.infotrygd.svangerskapspenger.repository.PeriodeRepository
import no.nav.infotrygd.svangerskapspenger.repository.SakRepository
import no.nav.infotrygd.svangerskapspenger.rest.dto.*
import no.nav.infotrygd.svangerskapspenger.testutil.TestData
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
    private val relevantStatus  = SakStatus.FB
    private val fnr = TestData.foedselsNr()

    @Autowired
    lateinit var sakRepository: SakRepository

    @Autowired
    lateinit var periodeRepository: PeriodeRepository

    val sakService: SakService
        get() = SakService(sakRepository, periodeRepository)

    @Test
    fun saker() {
        val type = "S"
        val resultat = "xx"
        val sak = Sak(
            id = nextId(),
            fnr = fnr,
            personKey = 123,
            saksblokk = "x",
            saksnummer = "11",
            kapittelNr = "FA",
            valg = "SV",
            type = type,
            resultat = resultat,
            vedtaksdato = LocalDate.now(),
            iverksattdato = LocalDate.now(),
            statushistorikk = listOf(
                Status(id = nextId(), personKey = 123, saksblokk = "x", saksnummer = "11", lopeNr = 98, status = relevantStatus),
                Status(id = nextId(), personKey = 123, saksblokk = "x", saksnummer = "11", lopeNr = 99, status = SakStatus.IKKE_BEHANDLET)
            ),
            registrert = LocalDate.now()
        )

        sakRepository.save(sak)

        val res = sakService.findSakerByFnr(fnr, LocalDate.now().minusYears(1))

        val forventet = listOf(SakDto(
            sakId = SakId(sak.saksblokk, sak.saksnummer.toInt()),
            type = type,
            status = relevantStatus.kode,
            resultat = resultat,
            vedtatt = LocalDate.now(),
            iverksatt = LocalDate.now(),
            registrert = LocalDate.now()
        ))

        assertThat(res.saker).isEqualTo(forventet)
    }

    @Test
    fun apneSakerMedLopendeUtbetaling() {
        val iverksatt = LocalDate.now()
        val utbetaltFom = iverksatt
        val utbetaltTom = iverksatt.plusDays(1)
        val registrert = iverksatt.plusMonths(1)
        val gradering = 20

        val pf = TestData.PeriodeFactory(fnr = fnr)

        val periode = pf.periode().copy(
            stoenadstype = Stoenadstype.RISIKOFYLT_ARBMILJOE,
            frisk = Frisk.LOPENDE,
            arbufoer = iverksatt,
            stoppdato = null,
            registrert = registrert,
            utbetalingshistorikk = listOf(
                pf.utbetaling().copy(
                    utbetaltFom = utbetaltFom,
                    utbetaltTom = utbetaltTom,
                    grad = gradering
                )
            )
        )

        periodeRepository.save(periode)

        val forventet = listOf(ApenSakMedLopendeUtbetaling(
            iverksatt = iverksatt,
            registrert = registrert,
            utbetalinger = listOf(
                UtbetalingDto(
                    utbetaltFom = utbetaltFom,
                    utbetaltTom = utbetaltTom,
                    gradering = gradering
                )
            )
        ))
        val result = sakService.findSakerByFnr(fnr, LocalDate.now().minusYears(1))
        assertThat(result.apneSakerMedLopendeUtbetaling).isEqualTo(forventet)
    }

    @Test
    fun avsluttedeSaker() {
        val iverksatt = LocalDate.now().minusMonths(1)
        val stoppdato = LocalDate.now().minusWeeks(1)
        val registrert = iverksatt.plusMonths(1)
        val gradering = 20

        val pf = TestData.PeriodeFactory(fnr = fnr)

        val periode = pf.periode().copy(
            stoenadstype = Stoenadstype.RISIKOFYLT_ARBMILJOE,
            frisk = Frisk.FRISKMELDT,
            arbufoer = iverksatt,
            stoppdato = stoppdato,
            registrert = registrert,
            utbetalingshistorikk = listOf(
                pf.utbetaling().copy(
                    utbetaltFom = iverksatt,
                    utbetaltTom = stoppdato,
                    grad = gradering
                )
            )
        )

        val fom = LocalDate.now().minusYears(1)

        periodeRepository.save(periode)

        val forventet = listOf(AvsluttetSak(
            iverksatt = iverksatt,
            stoppdato = stoppdato,
            registrert = registrert,
            utbetalinger = listOf(
                UtbetalingDto(
                    utbetaltFom = iverksatt,
                    utbetaltTom = stoppdato,
                    gradering = gradering
                )
            )
        ))

        val result = sakService.findSakerByFnr(fnr, LocalDate.now().minusYears(1))

        assertThat(result.avsluttedeSaker.fraOgMed).isEqualTo(fom)
        assertThat(result.avsluttedeSaker.saker).isEqualTo(forventet)
    }
}