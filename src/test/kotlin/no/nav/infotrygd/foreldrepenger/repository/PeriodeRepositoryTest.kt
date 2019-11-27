package no.nav.infotrygd.foreldrepenger.repository

import no.nav.infotrygd.foreldrepenger.model.Periode
import no.nav.infotrygd.foreldrepenger.model.Utbetaling
import no.nav.infotrygd.foreldrepenger.model.kodeverk.Frisk
import no.nav.infotrygd.foreldrepenger.model.kodeverk.Stoenadstype
import no.nav.infotrygd.foreldrepenger.nextId
import no.nav.infotrygd.foreldrepenger.testutil.TestData
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

        val relevant = periode(Stoenadstype.ADOPSJON, Frisk.FRISKMELDT)

        val utgatt = periode(Stoenadstype.ADOPSJON, Frisk.FRISKMELDT, LocalDate.now().minusYears(2))
        val ikkeFrisk = periode(Stoenadstype.ADOPSJON, Frisk.DOEDSSYK)
        val feilType = periode(Stoenadstype.SYKEPENGER, Frisk.FRISKMELDT)

        repository.saveAll(listOf(relevant, utgatt, ikkeFrisk, feilType))

        val result = repository.findAvsluttedeSakerByFnr(fnr, LocalDate.now().minusYears(1))
        assertThat(listOf(relevant)).isEqualTo(result) // relevant hibernate bug: https://hibernate.atlassian.net/browse/HHH-5409
    }

    @Test
    fun countAvsluttedeSaker() {
        val relevant = periode(Stoenadstype.ADOPSJON, Frisk.FRISKMELDT)
        repository.save(relevant)
        assertThat(repository.countAvsluttedeSaker(LocalDate.now().minusYears(1))).isEqualTo(1)
    }

    @Test
    fun findOpneSakerMedLopendeUtbetalingByFnr() {
        val relevant = periode(Stoenadstype.ADOPSJON, Frisk.LOPENDE)

        val frisk = periode(Stoenadstype.ADOPSJON, Frisk.FRISKMELDT)
        val feilType = periode(Stoenadstype.SYKEPENGER, Frisk.LOPENDE)

        repository.saveAll(listOf(relevant, frisk, feilType))

        val result = repository.findOpneSakerMedLopendeUtbetalingByFnr(fnr)
        assertThat(listOf(relevant)).isEqualTo(result) // relevant hibernate bug: https://hibernate.atlassian.net/browse/HHH-5409
    }

    @Test
    fun utbetalinger() {
        var p = periode(Stoenadstype.ADOPSJON, Frisk.FRISKMELDT, LocalDate.now())
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
        val relevant = periode(Stoenadstype.ADOPSJON, Frisk.LOPENDE)
        repository.save(relevant)

        assertThat(repository.findOpneSakerMedLopendeUtbetaling()).hasSize(1)
    }

    @Test
    fun countOpneSakerMedLopendeUtbetaling() {
        val relevant = periode(Stoenadstype.ADOPSJON, Frisk.LOPENDE)
        repository.save(relevant)

        assertThat(repository.countOpneSakerMedLopendeUtbetaling()).isEqualTo(1)
    }

    @Test
    fun findByFnrAndStoenadstypeAndDates() {
        val tema = Stoenadstype.ADOPSJON
        val dato = LocalDate.now()
        val relevant = periode(tema, arbufoer = dato, frisk = Frisk.LOPENDE)
        val feilTema = periode(Stoenadstype.RISIKOFYLT_ARBMILJOE, arbufoer = dato, frisk = Frisk.LOPENDE)
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