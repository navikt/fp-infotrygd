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
import jakarta.persistence.EntityManager

@RunWith(SpringRunner::class)
@DataJpaTest
@ActiveProfiles("test")
class PeriodeRepositoryTest {

    @Autowired
    lateinit var repository: PeriodeRepository

    @Autowired
    lateinit var entityManager: EntityManager

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

        val result = repository.findByFnrAndFrisk(fnr, PeriodeRepository.avsluttede, PeriodeRepository.stoenadstypeFp)
        assertThat(listOf(relevant)).isEqualTo(result) // relevant hibernate bug: https://hibernate.atlassian.net/browse/HHH-5409
    }

    @Test
    fun findOpneSakerMedLopendeUtbetalingByFnr() {
        val relevant = periode(Stoenadstype.ADOPSJON, Frisk.LOPENDE)

        val frisk = periode(Stoenadstype.ADOPSJON, Frisk.FRISKMELDT)
        val feilType = periode(Stoenadstype.SYKEPENGER, Frisk.LOPENDE)

        repository.saveAll(listOf(relevant, frisk, feilType))

        val result = repository.findByFnrAndFrisk(fnr, PeriodeRepository.lopende, PeriodeRepository.stoenadstypeFp)
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
                utbetalingsdato = LocalDate.now(),
                grad = 10,
                korr = null,
                type = null,
                region = "X"
            )
        ))

        repository.save(p)

        val result = repository.findByFnrAndFrisk(fnr, PeriodeRepository.avsluttede, PeriodeRepository.stoenadstypeFp)
        assertThat(listOf(p)).isEqualTo(result) // relevant hibernate bug: https://hibernate.atlassian.net/browse/HHH-5409
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

    @Test
    fun `leser perioder som er flyttet mellom regioner`() {
        val fnr = TestData.foedselsNr()
        val personKey = 99L
        val arbufoerSeq = 199L;
        val stoenadstype = Stoenadstype.ADOPSJON

        val pfA = TestData.PeriodeFactory(
            region = "A",
            stoenadstype = stoenadstype,
            fnr = fnr,
            personKey = personKey,
            arbufoerSeq = arbufoerSeq
        )

        val periodeA = pfA.periode().copy(
            inntekter = listOf(pfA.inntekt()),
            utbetalingshistorikk = listOf(pfA.utbetaling())
        )

        repository.save(periodeA)

        val pfB = TestData.PeriodeFactory(
            region = "B",
            stoenadstype = stoenadstype,
            fnr = fnr,
            personKey = personKey,
            arbufoerSeq = arbufoerSeq
        )

        val periodeB = pfB.periode().copy(
            inntekter = listOf(pfB.inntekt()),
            utbetalingshistorikk = listOf(pfB.utbetaling())
        )

        repository.save(periodeB)

        repository.flush()
        entityManager.clear()

        val resultat = repository.findByFnrAndStoenadstype(fnr, listOf(stoenadstype))
        assertThat(resultat).hasSize(2)
        for (periode in resultat) {
            assertThat(periode.inntekter).hasSize(1)
            assertThat(periode.utbetalingshistorikk).hasSize(1)
        }
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