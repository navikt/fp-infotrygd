package no.nav.infotrygd.foreldrepenger.model

import no.nav.commons.foedselsnummer.Kjoenn
import no.nav.infotrygd.foreldrepenger.model.kodeverk.Stoenadstype
import no.nav.infotrygd.foreldrepenger.model.kodeverk.Tema
import no.nav.infotrygd.foreldrepenger.testutil.TestData
import no.nav.infotrygd.foreldrepenger.utils.reversert
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import java.time.LocalDate


class PeriodeTest {
    @Test
    fun getYtelse() {
        assertThat(periode(Stoenadstype.SYKEPENGER).tema).isEqualTo(Tema.SYKEPENGER)

        val foreldrepenger = listOf(
            Stoenadstype.FOEDSEL,
            Stoenadstype.ADOPSJON,
            Stoenadstype.RISIKOFYLT_ARBMILJOE,
            Stoenadstype.SVANGERSKAP)

        for(type in foreldrepenger) {
            assertThat(periode(type).tema).isEqualTo(Tema.FORELDREPENGER)
        }

        val paaroerendeSykdom = listOf(
            Stoenadstype.BARNS_SYKDOM,
            Stoenadstype.ALV_SYKT_BARN,
            Stoenadstype.KURS_KAP_3_23,
            Stoenadstype.PAS_DOEDSSYK,
            Stoenadstype.PLEIEPENGER_INSTOPPH,
            Stoenadstype.PLEIEPENGER_NY_ORDNING
        )

        for(type in paaroerendeSykdom) {
            assertThat(periode(type).tema).isEqualTo(Tema.PAAROERENDE_SYKDOM)
        }
    }

    @Test
    fun opphoerFom() {
        var periode = TestData.periode()
        assertThat(periode.opphoerFom).isNull()

        val stoppdato = LocalDate.of(2019, 1, 1)
        val friskmeldtDato = stoppdato.plusMonths(1)
        val arbufoerTom = friskmeldtDato.plusMonths(1)
        val maksdato = arbufoerTom.plusMonths(1)

        periode = periode.copy(maksdato = maksdato)
        assertThat(periode.opphoerFom).isEqualTo(maksdato)

        periode = periode.copy(arbufoerTom = arbufoerTom)
        assertThat(periode.opphoerFom).isEqualTo(arbufoerTom.plusDays(1))

        periode = periode.copy(friskmeldtDato = friskmeldtDato)
        assertThat(periode.opphoerFom).isEqualTo(friskmeldtDato)

        periode = periode.copy(stoppdato = stoppdato)
        assertThat(periode.opphoerFom).isEqualTo(stoppdato)
    }

    @Test
    fun innenforPeriode() {
        val start = LocalDate.of(2019, 1, 1)
        val stop = start.plusYears(1)

        val periode = TestData.periode().copy(
            arbufoer = start,
            stoppdato = stop
        )

        assertThat(periode.innenforPeriode(LocalDate.MIN, null)).isTrue()
        assertThat(periode.innenforPeriode(LocalDate.MIN, start.minusDays(1))).isFalse()
        assertThat(periode.innenforPeriode(LocalDate.MIN, start)).isTrue()

        assertThat(periode.innenforPeriode(start.minusDays(1), start.plusDays(1))).isTrue()

        assertThat(periode.innenforPeriode(start, stop)).isTrue()
        assertThat(periode.innenforPeriode(start.plusDays(1), stop.minusDays(1))).isTrue()

        assertThat(periode.innenforPeriode(stop.minusDays(1), null)).isTrue()
        assertThat(periode.innenforPeriode(stop.minusDays(1), stop.plusDays(1))).isTrue()

        assertThat(periode.innenforPeriode(stop, stop.plusDays(1))).isTrue()
        assertThat(periode.innenforPeriode(stop.plusDays(1), null)).isFalse()
        assertThat(periode.innenforPeriode(stop.plusDays(1), stop.plusDays(2))).isFalse()
    }

    @Test
    fun barnPersonKey() {
        val tkNr = "1000"
        val fnr = TestData.foedselsNr()

        val periode = TestData.periode().copy(
            tkNr = tkNr,
            barnFnr = fnr
        )

        assertThat(periode.barnPersonKey).isEqualTo("$tkNr${fnr.reversert}".toLong())
    }

    @Test
    fun barnKode() {
        for(adopsjon in listOf("A", "D")) {
            for(kjoenn in Kjoenn.values()) {
                val kode = beregnKode(adopsjon, kjoenn)
                assertThat(kode).isEqualTo("1")
            }
        }

        for(adopsjon in listOf("B", "C", "E")) {
            for(kjoenn in Kjoenn.values()) {
                val kode = beregnKode(adopsjon, kjoenn)
                assertThat(kode).isEqualTo("2")
            }
        }

        assertThat(beregnKode("x", Kjoenn.KVINNE)).isEqualTo("1")
        assertThat(beregnKode("x", Kjoenn.MANN)).isEqualTo("2")
    }

    private fun beregnKode(adopsjon: String, kjoenn: Kjoenn): String? {
        val fnr = TestData.foedselsNr(kjoenn = kjoenn)

        return TestData.periode().copy(
            stebarnsadopsjon = adopsjon,
            fnr = fnr
        ).barnKode
    }

    private fun periode(type: Stoenadstype): Periode {
        return TestData.periode().copy(
            stoenadstype = type
        )
    }
}