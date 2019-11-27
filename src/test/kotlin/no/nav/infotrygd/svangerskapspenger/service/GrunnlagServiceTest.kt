package no.nav.infotrygd.svangerskapspenger.service

import no.nav.infotrygd.svangerskapspenger.model.kodeverk.Stoenadstype
import no.nav.infotrygd.svangerskapspenger.repository.PeriodeRepository
import no.nav.infotrygd.svangerskapspenger.repository.VedtakBarnRepository
import no.nav.infotrygd.svangerskapspenger.testutil.TestData
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit4.SpringRunner
import java.time.LocalDate

@SpringBootTest
@RunWith(SpringRunner::class)
@ActiveProfiles("test")
class GrunnlagServiceTest {

    @Autowired
    private lateinit var grunnlagService: GrunnlagService

    @Autowired
    private lateinit var periodeRepository: PeriodeRepository

    @Autowired
    private lateinit var vedtakBarnRepository: VedtakBarnRepository

    @Test
    fun hentForeldrepenger() {
        val gradering = 10.toBigDecimal()

        val factory = TestData.PeriodeFactory()

        val inntekt = factory.inntekt()
        val utbetaling = factory.utbetaling()

        val periode = factory.periode().copy(
            stoenadstype = Stoenadstype.SVANGERSKAP,
            foedselsdatoBarn = LocalDate.now().minusYears(1),
            inntekter = listOf(inntekt),
            utbetalingshistorikk = listOf(utbetaling)
        )

        periodeRepository.save(periode)

        val vedtak = factory.vedtakBarn().copy(
            dekningsgrad = gradering
        )
        vedtakBarnRepository.save(vedtak)

        val resultat =
            grunnlagService.hentForeldrepenger(listOf(Stoenadstype.SVANGERSKAP), factory.fnr, LocalDate.now().minusYears(1), null)

        println(resultat)

        assertThat(resultat).hasSize(1)
        val fp = resultat[0]

        assertThat(fp.gradering?.toInt()).isEqualTo(gradering.toInt())
        assertThat(fp.vedtak).hasSize(1)
        assertThat(fp.arbeidsforhold).hasSize(1)
    }
}