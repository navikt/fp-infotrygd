package no.nav.infotrygd.foreldrepenger.service

import no.nav.infotrygd.foreldrepenger.model.VedtakBarn
import no.nav.infotrygd.foreldrepenger.model.kodeverk.Tema
import no.nav.infotrygd.foreldrepenger.rest.dto.*
import java.math.RoundingMode

fun periodeToDetaljer(
    p: no.nav.infotrygd.foreldrepenger.model.Periode,
    vedtak: VedtakBarn?
): YtelseDetaljer {
    check(p.tema == Tema.FORELDREPENGER || p.tema == Tema.SYKEPENGER) { "Forventet ytelse == FORELDREPENGER eller SYKEPENGER" }

    if (p.tema != Tema.FORELDREPENGER) {
        return YtelseDetaljer(null, null, null, null)
    }

    return YtelseDetaljer(
        opprinneligIdentdato = p.arbufoerOpprinnelig,
        dekningsgrad = p.dekningsgrad,
        gradering = vedtak?.dekningsgrad?.setScale(0, RoundingMode.HALF_UP)?.toInt(),
        foedselsdatoBarn = p.foedselsdatoBarn
    )
}

fun periodeToGrunnlag(p: no.nav.infotrygd.foreldrepenger.model.Periode): GrunnlagGenerelt {
    val tema = p.tema
    val status = p.frisk.status?.let { Kodeverdi(it.kode, it.tekst) }

    val utbetaltFom = p.utbetaltFom
    val utbetaltTom = p.utbetaltTom

    val periode: Periode? = if (utbetaltFom != null && utbetaltTom != null)
        Periode(utbetaltFom, utbetaltTom) else null

    val kat = p.arbeidskategori
    val arbeidskategori: Kodeverdi? = if (kat == null) {
        null
    } else {
        Kodeverdi(kat.kode, kat.tekst)
    }

    return GrunnlagGenerelt(
        tema = Kodeverdi(tema.kode, tema.tekst),
        registrert = p.registrert,
        status = status,
        saksbehandlerId = p.brukerId,
        iverksatt = p.arbufoer,
        opphoerFom = p.opphoerFom,
        behandlingstema = p.stoenadstype!!.toDto(),
        identdato = p.arbufoer,
        periode = periode,
        arbeidskategori = arbeidskategori,
        arbeidsforhold = p.inntekter.map {
            Arbeidsforhold(
                inntektForPerioden = it.loenn,
                inntektsperiode = Kodeverdi(
                    it.periode.kode,
                    it.periode.tekst
                ),
                arbeidsgiverOrgnr = it.orgnummer,
                refusjon = it.refusjon,
                refusjonTom = it.refusjonTom
            )
        },
        vedtak = p.utbetalinger.map {
            Vedtak(
                utbetalingsgrad = it.grad ?: 100,
                periode = Periode(
                    it.utbetaltFom,
                    it.utbetaltTom
                ),
                arbeidsgiverOrgnr = it.orgnummer,
                erRefusjon = it.erRefusjon,
                dagsats = it.dagsats
            )
        }
    )
}