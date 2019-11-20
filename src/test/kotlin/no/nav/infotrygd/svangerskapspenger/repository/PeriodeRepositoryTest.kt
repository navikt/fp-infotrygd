package no.nav.infotrygd.svangerskapspenger.repository

import no.nav.infotrygd.svangerskapspenger.model.Periode
import no.nav.infotrygd.svangerskapspenger.model.Utbetaling
import no.nav.infotrygd.svangerskapspenger.nextId
import no.nav.infotrygd.svangerskapspenger.values.FodselNr
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

    private val fnr = FodselNr("10000000001")

    @Test
    fun findAvsluttedeSakerByFnr() {

        val relevant = periode("SV", "F")

        val utgatt = periode("SV", "F", LocalDate.now().minusYears(2))
        val ikkeFrisk = periode("SV", "X")
        val ikkeSv = periode("XX", "F")

        repository.saveAll(listOf(relevant, utgatt, ikkeFrisk, ikkeSv))

        val result = repository.findAvsluttedeSakerByFnr(fnr, LocalDate.now().minusYears(1))
        assertThat(listOf(relevant)).isEqualTo(result) // relevant hibernate bug: https://hibernate.atlassian.net/browse/HHH-5409
    }

    @Test
    fun countAvsluttedeSaker() {
        val relevant = periode("SV", "F")
        repository.save(relevant)
        assertThat(repository.countAvsluttedeSaker(LocalDate.now().minusYears(1))).isEqualTo(1)
    }

    @Test
    fun findOpneSakerMedLopendeUtbetalingByFnr() {
        val relevant = periode("SV", " ")

        val frisk = periode("SV", "F")
        val ikkeSv = periode("X", " ")

        repository.saveAll(listOf(relevant, frisk, ikkeSv))

        val result = repository.findOpneSakerMedLopendeUtbetalingByFnr(fnr)
        assertThat(listOf(relevant)).isEqualTo(result) // relevant hibernate bug: https://hibernate.atlassian.net/browse/HHH-5409
    }

    @Test
    fun utbetalinger() {
        var p = periode("SV", "F", LocalDate.now())
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
        val relevant = periode("SV", " ")
        repository.save(relevant)

        assertThat(repository.findOpneSakerMedLopendeUtbetaling()).hasSize(1)
    }

    @Test
    fun countOpneSakerMedLopendeUtbetaling() {
        val relevant = periode("SV", " ")
        repository.save(relevant)

        assertThat(repository.countOpneSakerMedLopendeUtbetaling()).isEqualTo(1)
    }

    private fun periode(
        stoenadstype: String,
        frisk: String,
        arbufoer: LocalDate = LocalDate.now()
    ): Periode {
        return Periode(
            id = nextId(),
            personKey = 1,
            arbufoerSeq = 1,
            fnr = fnr,
            stoenadstype = stoenadstype,
            frisk = frisk,
            arbufoer = arbufoer,
            stoppdato = null,
            utbetalingshistorikk = listOf(),
            registrert = LocalDate.now()
        )
    }
}