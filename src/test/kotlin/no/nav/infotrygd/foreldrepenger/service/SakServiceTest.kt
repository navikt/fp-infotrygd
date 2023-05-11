package no.nav.infotrygd.foreldrepenger.service

import no.nav.infotrygd.foreldrepenger.model.Sak
import no.nav.infotrygd.foreldrepenger.model.Status
import no.nav.infotrygd.foreldrepenger.model.kodeverk.Frisk
import no.nav.infotrygd.foreldrepenger.model.kodeverk.SakStatus
import no.nav.infotrygd.foreldrepenger.model.kodeverk.Stoenadstype
import no.nav.infotrygd.foreldrepenger.nextId
import no.nav.infotrygd.foreldrepenger.repository.PeriodeRepository
import no.nav.infotrygd.foreldrepenger.repository.SakRepository
import no.nav.infotrygd.foreldrepenger.rest.dto.*
import no.nav.infotrygd.foreldrepenger.testutil.TestData
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.math.BigInteger
import java.time.LocalDate

@SpringBootTest
@ExtendWith(SpringExtension::class)

@ActiveProfiles("test")
internal class SakServiceTest {
    private val relevantStatus  = SakStatus.FB
    private val fnr = TestData.foedselsNr()

    @Autowired
    lateinit var sakRepository: SakRepository

    @Autowired
    lateinit var periodeRepository: PeriodeRepository

    @Autowired
    lateinit var applicationUtil: ApplicationUtil

    val sakService: SakService
        get() = SakService(sakRepository, periodeRepository, applicationUtil)

    @Test
    fun saker() {
        val type = "S"
        val resultat = "xx"
        val sak = Sak(
            id = nextId(),
            fnr = fnr,
            personKey = BigInteger.valueOf(123),
            saksblokk = "x",
            saksnummer = "11",
            kapittelNr = "FA",
            valg = "AE",
            type = type,
            resultat = resultat,
            vedtaksdato = LocalDate.now(),
            iverksattdato = LocalDate.now(),
            statushistorikk = listOf(
                Status(id = nextId(), personKey = BigInteger.valueOf(123), saksblokk = "x", saksnummer = "11", lopeNr = BigInteger.valueOf(98), status = relevantStatus),
                Status(id = nextId(), personKey = BigInteger.valueOf(123), saksblokk = "x", saksnummer = "11", lopeNr = BigInteger.valueOf(99), status = SakStatus.IKKE_BEHANDLET)
            ),
            registrert = LocalDate.now()
        )

        sakRepository.save(sak)

        val res = sakService.findSakerByFnr(fnr, LocalDate.now().minusYears(1), LocalDate.now())

        val forventet = listOf(
            SakDto(
                sakId = SakId(sak.saksblokk, sak.saksnummer.toInt()),
                type = type,
                status = relevantStatus.kode,
                resultat = resultat,
                vedtatt = LocalDate.now(),
                iverksatt = LocalDate.now(),
                registrert = LocalDate.now()
            ), SakDto(
                sakId = SakId(sak.saksblokk, sak.saksnummer.toInt()),
                type = type,
                status = relevantStatus.kode,
                resultat = resultat,
                vedtatt = LocalDate.now(),
                iverksatt = LocalDate.now(),
                registrert = LocalDate.now()
            )
        )

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
            stoenadstype = Stoenadstype.ADOPSJON,
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
        val result = sakService.findSakerByFnr(fnr, LocalDate.now().minusYears(1), LocalDate.now())
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
            stoenadstype = Stoenadstype.ADOPSJON,
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

        val result = sakService.findSakerByFnr(fnr, LocalDate.now().minusYears(1), LocalDate.now())

        assertThat(result.avsluttedeSaker.fraOgMed).isEqualTo(fom)
        assertThat(result.avsluttedeSaker.saker).isEqualTo(forventet)
    }

    @Test
    fun ikkeStartetSaker() {
        val iverksatt = LocalDate.now().minusMonths(1)
        val registrert = iverksatt.plusMonths(1)

        val pf = TestData.PeriodeFactory(fnr = fnr)

        val periode = pf.periode().copy(
            stoenadstype = Stoenadstype.ADOPSJON,
            frisk = Frisk.PASSIV,
            arbufoer = iverksatt,
            registrert = registrert
        )

        periodeRepository.save(periode)

        val forventet = listOf(IkkeStartet(
            iverksatt = iverksatt,
            registrert = registrert
        ))

        val result = sakService.findSakerByFnr(fnr, LocalDate.now().minusYears(1), LocalDate.now())

        assertThat(result.ikkeStartet).isEqualTo(forventet)
    }
}