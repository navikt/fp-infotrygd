package no.nav.infotrygd.foreldrepenger.service

import no.nav.infotrygd.foreldrepenger.model.kodeverk.Stoenadstype
import no.nav.infotrygd.foreldrepenger.repository.PeriodeRepository
import no.nav.infotrygd.foreldrepenger.repository.VedtakBarnRepository
import no.nav.infotrygd.foreldrepenger.testutil.TestData
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.time.LocalDate

@SpringBootTest
@ExtendWith(SpringExtension::class)
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
        val startdato = LocalDate.of(2019, 1, 1)
        val stoppdato = startdato.plusDays(1)

        val gradering = 10.toBigDecimal()

        val factory = TestData.PeriodeFactory()

        val inntekt = factory.inntekt()
        val utbetaling = factory.utbetaling()

        val periode = factory.periode().copy(
            arbufoer = startdato,
            stoppdato = stoppdato,
            stoenadstype = Stoenadstype.FOEDSEL,
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
            grunnlagService.hentYtelse(factory.fnr, startdato.minusYears(1), null)

        assertThat(resultat).hasSize(1)
        val fp = resultat[0]

        assertThat(fp.gradering).isEqualTo(gradering.toInt())
        assertThat(fp.vedtak).hasSize(1)
        assertThat(fp.arbeidsforhold).hasSize(1)

        assertThat(grunnlagService.hentYtelse(factory.fnr, stoppdato.plusDays(1), null)).isEmpty()
    }

    @Test
    fun hentGrunnlag_med_refusjon_og_opphør_av_refusjon() {
        val startdato = LocalDate.of(2019, 1, 1)
        val stoppdato = startdato.plusDays(10)
        val refusjonTom = startdato.plusDays(5)

        val factory = TestData.PeriodeFactory()

        val inntekt = factory.inntekt().copy(
            refusjon = true,
            refusjonTom = refusjonTom
        )
        val utbetaling = factory.utbetaling()

        val periode = factory.periode().copy(
            arbufoer = startdato,
            stoppdato = stoppdato,
            stoenadstype = Stoenadstype.FOEDSEL,
            inntekter = listOf(inntekt),
            utbetalingshistorikk = listOf(utbetaling)
        )

        periodeRepository.save(periode)

        val resultat =
            grunnlagService.hentYtelse(factory.fnr, startdato.minusYears(1), null)

        assertThat(resultat).hasSize(1)
        val grunnlag = resultat[0]

        assertThat(grunnlag.vedtak).hasSize(1)
        assertThat(grunnlag.arbeidsforhold).hasSize(1)
        assertThat(grunnlag.arbeidsforhold.get(0).refusjon).isEqualTo(true)
        assertThat(grunnlag.arbeidsforhold.get(0).refusjonTom).isEqualTo(refusjonTom)

        assertThat(grunnlagService.hentYtelse(factory.fnr, stoppdato.plusDays(1), null)).isEmpty()
    }
}