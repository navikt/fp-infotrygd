package no.nav.infotrygd.svangerskapspenger.repository

import no.nav.infotrygd.svangerskapspenger.model.Periode
import no.nav.infotrygd.svangerskapspenger.nextId
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
class PeriodeRepositoryTest {

    @Autowired
    lateinit var repository: PeriodeRepository

    @Test
    fun findAvsluttedeSakerByFnr() {

        val relevant = periode("SV", "F")

        val utgatt = periode("SV", "F", LocalDate.now().minusYears(2))
        val ikkeFrisk = periode("SV", "X")
        val ikkeSv = periode("XX", "F")

        repository.saveAll(listOf(relevant, utgatt, ikkeFrisk, ikkeSv))

        val result = repository.findAvsluttedeSakerByFnr("123", LocalDate.now().minusYears(1))
        assertThat(result).isEqualTo(listOf(relevant))
    }

    @Test
    fun findOpneSakerMedLopendeUtbetaling() {
        val relevant = periode("SV", " ")

        val frisk = periode("SV", "F")
        val ikkeSv = periode("X", " ")

        repository.saveAll(listOf(relevant, frisk, ikkeSv))

        val result = repository.findOpneSakerMedLopendeUtbetaling("123")
        assertThat(result).isEqualTo(listOf(relevant))
    }

    private fun periode(
        stoenadstype: String,
        frisk: String,
        arbufoer: LocalDate = LocalDate.now()
    ): Periode {
        return Periode(
            id = nextId(),
            fnr = "123",
            stoenadstype = stoenadstype,
            frisk = frisk,
            arbufoer = arbufoer,
            stoppdato = null
        )
    }
}