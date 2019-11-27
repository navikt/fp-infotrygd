package no.nav.infotrygd.svangerskapspenger.repository

import no.nav.infotrygd.svangerskapspenger.model.Periode
import no.nav.infotrygd.svangerskapspenger.model.Utbetaling
import no.nav.infotrygd.svangerskapspenger.model.kodeverk.Frisk
import no.nav.infotrygd.svangerskapspenger.model.kodeverk.Stoenadstype
import no.nav.infotrygd.svangerskapspenger.nextId
import no.nav.infotrygd.svangerskapspenger.testutil.TestData
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
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
class PeriodeRepositoryTest {

    @Autowired
    lateinit var repository: PeriodeRepository

    @Before
    fun setUp() {
        repository.deleteAll()
    }

    private val fnr = TestData.foedselsNr()

    @Test
    fun findAvsluttedeSakerByFnr() {

        val relevant = periode(Stoenadstype.RISIKOFYLT_ARBMILJOE, Frisk.FRISKMELDT)

        val utgatt = periode(Stoenadstype.RISIKOFYLT_ARBMILJOE, Frisk.FRISKMELDT, LocalDate.now().minusYears(2))
        val ikkeFrisk = periode(Stoenadstype.RISIKOFYLT_ARBMILJOE, Frisk.DOEDSSYK)
        val ikkeSv = periode(Stoenadstype.SYKEPENGER, Frisk.FRISKMELDT)

        repository.saveAll(listOf(relevant, utgatt, ikkeFrisk, ikkeSv))

        val result = repository.findAvsluttedeSakerByFnr(fnr, LocalDate.now().minusYears(1))
        assertThat(listOf(relevant)).isEqualTo(result) // relevant hibernate bug: https://hibernate.atlassian.net/browse/HHH-5409
    }

    @Test
    fun countAvsluttedeSaker() {
        val relevant = periode(Stoenadstype.RISIKOFYLT_ARBMILJOE, Frisk.FRISKMELDT)
        repository.save(relevant)
        assertThat(repository.countAvsluttedeSaker(LocalDate.now().minusYears(1))).isEqualTo(1)
    }

    @Test
    fun findOpneSakerMedLopendeUtbetalingByFnr() {
        val relevant = periode(Stoenadstype.RISIKOFYLT_ARBMILJOE, Frisk.LOPENDE)

        val frisk = periode(Stoenadstype.RISIKOFYLT_ARBMILJOE, Frisk.FRISKMELDT)
        val ikkeSv = periode(Stoenadstype.SYKEPENGER, Frisk.LOPENDE)

        repository.saveAll(listOf(relevant, frisk, ikkeSv))

        val result = repository.findOpneSakerMedLopendeUtbetalingByFnr(fnr)
        assertThat(listOf(relevant)).isEqualTo(result) // relevant hibernate bug: https://hibernate.atlassian.net/browse/HHH-5409
    }

    @Test
    fun utbetalinger() {
        var p = periode(Stoenadstype.RISIKOFYLT_ARBMILJOE, Frisk.FRISKMELDT, LocalDate.now())
        p = p.copy(utbetalingshistorikk = listOf(
            Utbetaling(
                id = nextId(),
                personKey = p.personKey,
                arbufoerSeq = p.arbufoerSeq,
                utbetaltFom = LocalDate.now().minusYears(1),
                utbetaltTom = LocalDate.now(),
                grad = 10,
                korr = null,
                type = null
            )
        ))

        repository.save(p)

        val result = repository.findAvsluttedeSakerByFnr(fnr, LocalDate.now().minusYears(1))
        assertThat(listOf(p)).isEqualTo(result) // relevant hibernate bug: https://hibernate.atlassian.net/browse/HHH-5409
    }

    @Test
    fun findOpneSakerMedLopendeUtbetaling() {
        val relevant = periode(Stoenadstype.RISIKOFYLT_ARBMILJOE, Frisk.LOPENDE)
        repository.save(relevant)

        assertThat(repository.findOpneSakerMedLopendeUtbetaling()).hasSize(1)
    }

    @Test
    fun countOpneSakerMedLopendeUtbetaling() {
        val relevant = periode(Stoenadstype.RISIKOFYLT_ARBMILJOE, Frisk.LOPENDE)
        repository.save(relevant)

        assertThat(repository.countOpneSakerMedLopendeUtbetaling()).isEqualTo(1)
    }

    @Test
    fun findByFnrAndStoenadstypeAndDates() {
        val tema = Stoenadstype.RISIKOFYLT_ARBMILJOE
        val dato = LocalDate.now()
        val relevant = periode(tema, arbufoer = dato, frisk = Frisk.LOPENDE)
        val feilTema = periode(Stoenadstype.ADOPSJON, arbufoer = dato, frisk = Frisk.LOPENDE)
        val forTidlig = periode(tema, arbufoer = dato.minusYears(1), frisk = Frisk.LOPENDE)
        val forSen = periode(tema, arbufoer = dato.plusYears(1), frisk = Frisk.LOPENDE)

        repository.saveAll(listOf(relevant, feilTema, forTidlig, forSen))

        val result = repository.findByFnrAndStoenadstypeAndDates(fnr, listOf(tema), dato.minusDays(1), dato.plusDays(1))

        assertThat(listOf(relevant)).isEqualTo(result) // relevant hibernate bug: https://hibernate.atlassian.net/browse/HHH-5409
    }

    private fun periode(
        stoenadstype: Stoenadstype,
        frisk: Frisk,
        arbufoer: LocalDate = LocalDate.now()
    ): Periode {
        return TestData.periode().copy(
            fnr = fnr,
            stoenadstype = stoenadstype,
            frisk = frisk,
            arbufoer = arbufoer
        )
    }
}