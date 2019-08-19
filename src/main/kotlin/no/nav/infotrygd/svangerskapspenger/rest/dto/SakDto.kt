package no.nav.infotrygd.svangerskapspenger.rest.dto

import java.time.LocalDate

data class SakResult(
    val info: String?,
    val underBehandling: List<SakDto>, // S10_KAPITTELNR='FA', S10_VALG='SV', S10_TYPE IN ('S,'R')
    val klagesaker: List<SakDto>,       // samme som over, men S10_TYPE='K'
    val ankesaker: List<SakDto>,        // samme som over, men S10_TYPE='A'
    val apneSakerMedLopendeUtbetaling: List<ApenSakMedLopendeUtbetaling>,
    val avsluttedeSaker: AvsluttedeSaker // IS10_STONADS_TYPE='SV', IS10_FRISK='F', IS10_ARBUFOER >= i dag minus 1 år
)

// Saker fra Saksregister, ytelse Svangerskapspenger

data class SakDto(
    val sakId: SakId?,
    val status: String,         // S15_STATUS
    val resultat: String,       // S10_RESULTAT
    val vedtatt: LocalDate,     // S10_VEDTAKSDATO
    val iverksatt: LocalDate    // S10_IVERKSATTDATO
)

data class SakId(
    val blokk: String,  // S05_SAKSBLOKK
    val nr: Int         // S10_SAKSNR
)


// Saker fra Foreldrepenge-base, ytelse Svangerskapspenger

data class ApenSakMedLopendeUtbetaling(
    val iverksatt: LocalDate // IS10_ARBUFOER
)

data class AvsluttedeSaker(val fraOgMed: LocalDate, val saker: List<AvsluttetSak>)

data class AvsluttetSak(
    val iverksatt: LocalDate,   // IS10_ARBUFOER
    val stoppdato: LocalDate    // IS10_STOPPDATO
)