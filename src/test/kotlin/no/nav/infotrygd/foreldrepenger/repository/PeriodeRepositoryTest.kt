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

        val ikkeFrisk = periode(Stoenadstype.ADOPSJON, Frisk.DOEDSSYK)
        val feilType = periode(Stoenadstype.SYKEPENGER, Frisk.FRISKMELDT)

        repository.saveAll(listOf(relevant, ikkeFrisk, feilType))

        val result = repository.findByFnrAndFrisk(fnr, PeriodeRepository.avsluttede)
        assertThat(listOf(relevant)).isEqualTo(result) // relevant hibernate bug: https://hibernate.atlassian.net/browse/HHH-5409
    }

    @Test
    fun countAvsluttedeSaker() {
        val relevant = periode(Stoenadstype.ADOPSJON, Frisk.FRISKMELDT)
        repository.save(relevant)
        assertThat(repository.countByFrisk(PeriodeRepository.avsluttede)).isEqualTo(1)
    }

    @Test
    fun findOpneSakerMedLopendeUtbetalingByFnr() {
        val relevant = periode(Stoenadstype.ADOPSJON, Frisk.LOPENDE)

        val frisk = periode(Stoenadstype.ADOPSJON, Frisk.FRISKMELDT)
        val feilType = periode(Stoenadstype.SYKEPENGER, Frisk.LOPENDE)

        repository.saveAll(listOf(relevant, frisk, feilType))

        val result = repository.findByFnrAndFrisk(fnr, PeriodeRepository.lopende)
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

        val result = repository.findByFnrAndFrisk(fnr, PeriodeRepository.avsluttede)
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

        assertThat(repository.countByFrisk(PeriodeRepository.lopende)).isEqualTo(1)
    }

    @Test
    fun findByFnrAndStoenadstype() {
        val tema = Stoenadstype.ADOPSJON
        val relevant = periode(tema, frisk = Frisk.LOPENDE)
        val historikk = periode(tema, frisk = Frisk.HISTORIKK)
        val feilTema = periode(Stoenadstype.RISIKOFYLT_ARBMILJOE, frisk = Frisk.LOPENDE)

        repository.saveAll(listOf(relevant, historikk, feilTema))

        val result = repository.findByFnrAndStoenadstype(fnr, listOf(tema))

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