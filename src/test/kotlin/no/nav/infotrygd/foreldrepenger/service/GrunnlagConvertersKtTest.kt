package no.nav.infotrygd.foreldrepenger.service

import no.nav.infotrygd.foreldrepenger.model.kodeverk.Arbeidskategori
import no.nav.infotrygd.foreldrepenger.model.kodeverk.Frisk
import no.nav.infotrygd.foreldrepenger.model.kodeverk.Inntektsperiode
import no.nav.infotrygd.foreldrepenger.model.kodeverk.Stoenadstype
import no.nav.infotrygd.foreldrepenger.rest.dto.*
import no.nav.infotrygd.foreldrepenger.testutil.TestData
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.math.BigInteger
import java.time.LocalDate

class GrunnlagConvertersKtTest {

    @Test
    fun periodeToGrunnlag() {
        val registrert = LocalDate.now().minusMonths(6)
        val saksbehandlerId = "XX123"
        val opphoerFom = registrert.plusDays(50)

        val utbetaltTom = LocalDate.now()
        val utbetaltFom = utbetaltTom.minusMonths(1)
        val stoenadstype = Stoenadstype.FOEDSEL
        val tema = stoenadstype.tema
        val inntektsperiode = Inntektsperiode.MAANEDLIG
        val arbeidsgiverOrgnr = "12345678900"
        val inntektForPerioden = 1000.toBigDecimal()
        val arbeidskategori = Arbeidskategori.AMBASSADEPERSONELL
        val utbetalingsgrad = 100

        val frisk = Frisk.BARN
        val status = frisk.status!!

        val refusjon = true
        val refusjonTom = utbetaltTom.minusDays(2)

        val pf = TestData.PeriodeFactory()

        val inntekt = pf.inntekt().copy(
            periode = inntektsperiode,
            arbgiverNr = BigInteger(arbeidsgiverOrgnr),
            loenn = inntektForPerioden,
            refusjon = refusjon,
            refusjonTom = refusjonTom
        )

        val utbetaling = pf.utbetaling().copy(
            utbetaltFom = utbetaltFom,
            utbetaltTom = utbetaltTom,
            grad = utbetalingsgrad,
            type = "5",
            dagsats = BigDecimal(1000),
            arbgiverNr = BigInteger(arbeidsgiverOrgnr)
        )

        val periode = pf.periode().copy(
            registrert = registrert,
            frisk = frisk,
            brukerId = saksbehandlerId,
            arbufoer = utbetaltFom,
            stoppdato = opphoerFom,
            stoenadstype = stoenadstype,
            utbetaltFom = utbetaltFom,
            utbetaltTom = utbetaltTom,
            arbeidskategori = arbeidskategori,
            inntekter = listOf(inntekt),
            utbetalingshistorikk = listOf(utbetaling)
        )

        val dto = periodeToGrunnlag(periode, null)
        val forventet = YtelseGrunnlag(
            tema = Kodeverdi(tema.kode, tema.tekst),
            registrert = registrert,
            status = Kodeverdi(status.kode, status.tekst),
            saksbehandlerId = saksbehandlerId,
            iverksatt = utbetaltFom,
            opphoerFom = opphoerFom,
            behandlingstema = stoenadstype.toDto(),
            identdato = utbetaltFom,
            periode = Periode(utbetaltFom, utbetaltTom),
            arbeidskategori = Kodeverdi(
                arbeidskategori.kode,
                arbeidskategori.tekst
            ),
            arbeidsforhold = listOf(
                Arbeidsforhold(
                    inntektForPerioden = inntektForPerioden,
                    inntektsperiode = Kodeverdi(
                        inntektsperiode.kode,
                        inntektsperiode.tekst
                    ),
                    arbeidsgiverOrgnr = arbeidsgiverOrgnr,
                    refusjon = refusjon,
                    refusjonTom = refusjonTom
                )
            ),
            vedtak = listOf(
                Vedtak(
                    utbetalingsgrad = utbetalingsgrad,
                    periode = Periode(utbetaltFom, utbetaltTom),
                    erRefusjon = true,
                    arbeidsgiverOrgnr = arbeidsgiverOrgnr,
                    dagsats = BigDecimal.valueOf(1000)
                )
            ),
            opprinneligIdentdato = null,
            dekningsgrad = null,
            gradering = null,
            foedselsdatoBarn = null
        )

        assertThat(dto).isEqualTo(forventet)
    }

    @Test
    fun periodeToForeldrepengerDetaljer() {
        val stoenadstype = Stoenadstype.FOEDSEL // ytelse = foreldrepenger
        val opprinneligIdentdato = LocalDate.now()
        val dekningsgrad = 75
        val gradering = 50
        val foedselsdatoBarn = LocalDate.now().minusYears(1)

        val pf = TestData.PeriodeFactory()

        val vedtak = pf.vedtakBarn().copy(
            dekningsgrad = BigDecimal(gradering).add(BigDecimal("0.4"))
        )

        val periode = pf.periode().copy(
            stoenadstype = stoenadstype,
            arbufoerOpprinnelig = opprinneligIdentdato,
            dekningsgrad = dekningsgrad,
            foedselsdatoBarn = foedselsdatoBarn
        )

        val dto = periodeToDetaljer(periode, vedtak)

        val expected = YtelseDetaljer(
            opprinneligIdentdato = opprinneligIdentdato,
            dekningsgrad = dekningsgrad,
            gradering = gradering,
            foedselsdatoBarn = foedselsdatoBarn
        )

        assertThat(dto).isEqualTo(expected)
    }

    @Test
    fun periodeToSykepengerDetaljer() {
        val stoenadstype = Stoenadstype.SYKEPENGER // ytelse = sykepenger
        val opprinneligIdentdato = LocalDate.now()
        val dekningsgrad = 75
        val gradering = 50
        val foedselsdatoBarn = LocalDate.now().minusYears(1)

        val pf = TestData.PeriodeFactory()

        val vedtak = pf.vedtakBarn().copy(
            dekningsgrad = BigDecimal(gradering).add(BigDecimal("0.4"))
        )

        val periode = pf.periode().copy(
            stoenadstype = stoenadstype,
            arbufoerOpprinnelig = opprinneligIdentdato,
            dekningsgrad = dekningsgrad,
            foedselsdatoBarn = foedselsdatoBarn
        )

        val dto = periodeToDetaljer(periode, vedtak)

        val expected = YtelseDetaljer(
            opprinneligIdentdato = null,
            dekningsgrad = null,
            gradering = null,
            foedselsdatoBarn = null
        )

        assertThat(dto).isEqualTo(expected)
    }

    @Test
    fun periodeToGrunnlagUtbetalingTilBruker() {
        val registrert = LocalDate.now().minusMonths(6)
        val saksbehandlerId = "XX123"
        val opphoerFom = registrert.plusDays(50)

        val utbetaltTom = LocalDate.now()
        val utbetaltFom = utbetaltTom.minusMonths(1)
        val stoenadstype = Stoenadstype.FOEDSEL
        val tema = stoenadstype.tema
        val inntektsperiode = Inntektsperiode.MAANEDLIG
        val arbeidsgiverOrgnr = "12345678900"
        val inntektForPerioden = 1000.toBigDecimal()
        val arbeidskategori = Arbeidskategori.AMBASSADEPERSONELL
        val utbetalingsgrad = 100

        val frisk = Frisk.BARN
        val status = frisk.status!!

        val refusjon = false
        val refusjonTom = null


        val pf = TestData.PeriodeFactory()

        val inntekt = pf.inntekt().copy(
            periode = inntektsperiode,
            arbgiverNr = BigInteger(arbeidsgiverOrgnr),
            loenn = inntektForPerioden,
            refusjon = refusjon,
            refusjonTom = refusjonTom
        )

        val utbetaling = pf.utbetaling().copy(
            utbetaltFom = utbetaltFom,
            utbetaltTom = utbetaltTom,
            grad = utbetalingsgrad,
            type = "0",
            dagsats = BigDecimal(1000),
            arbgiverNr = BigInteger(arbeidsgiverOrgnr)
        )

        val periode = pf.periode().copy(
            registrert = registrert,
            frisk = frisk,
            brukerId = saksbehandlerId,
            arbufoer = utbetaltFom,
            stoppdato = opphoerFom,
            stoenadstype = stoenadstype,
            utbetaltFom = utbetaltFom,
            utbetaltTom = utbetaltTom,
            arbeidskategori = arbeidskategori,
            inntekter = listOf(inntekt),
            utbetalingshistorikk = listOf(utbetaling)
        )

        val dto = periodeToGrunnlag(periode, null)
        val forventet = YtelseGrunnlag(
            tema = Kodeverdi(tema.kode, tema.tekst),
            registrert = registrert,
            status = Kodeverdi(status.kode, status.tekst),
            saksbehandlerId = saksbehandlerId,
            iverksatt = utbetaltFom,
            opphoerFom = opphoerFom,
            behandlingstema = stoenadstype.toDto(),
            identdato = utbetaltFom,
            periode = Periode(utbetaltFom, utbetaltTom),
            arbeidskategori = Kodeverdi(
                arbeidskategori.kode,
                arbeidskategori.tekst
            ),
            arbeidsforhold = listOf(
                Arbeidsforhold(
                    inntektForPerioden = inntektForPerioden,
                    inntektsperiode = Kodeverdi(
                        inntektsperiode.kode,
                        inntektsperiode.tekst
                    ),
                    arbeidsgiverOrgnr = arbeidsgiverOrgnr,
                    refusjon = refusjon,
                    refusjonTom = refusjonTom
                )
            ),
            vedtak = listOf(
                Vedtak(
                    utbetalingsgrad = utbetalingsgrad,
                    periode = Periode(utbetaltFom, utbetaltTom),
                    erRefusjon = false,
                    arbeidsgiverOrgnr = arbeidsgiverOrgnr,
                    dagsats = BigDecimal.valueOf(1000)
                )
            ),
            opprinneligIdentdato = null,
            dekningsgrad = null,
            gradering = null,
            foedselsdatoBarn = null
        )

        assertThat(dto).isEqualTo(forventet)
    }
}