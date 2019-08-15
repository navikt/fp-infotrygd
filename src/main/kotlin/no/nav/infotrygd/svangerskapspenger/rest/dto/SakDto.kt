package no.nav.infotrygd.svangerskapspenger.rest.dto

import java.time.LocalDate

data class SakResult(
    val info: String?,
    val underBehandling: List<SakDto>,
    val klagesaker: List<SakDto>,
    val ankesaker: List<SakDto>,
    val apneSakerMedLopendeUtbetaling: List<ApenSakMedLopendeUtbetaling>,
    val avsluttedeSaker: AvsluttedeSaker
)

// Saker fra Saksregister, ytelse Svangerskapspenger

data class SakDto(
    val sakId: SakId?,
    val status: String,
    val resultat: String,
    val vedtatt: LocalDate,
    val iverksatt: LocalDate
)

data class SakId(val blokk: String, val nr: Int)


// Saker fra Foreldrepenge-base, ytelse Svangerskapspenger

data class ApenSakMedLopendeUtbetaling(val iverksatt: LocalDate)

data class AvsluttedeSaker(val fraOgMed: LocalDate, val saker: List<AvsluttetSak>)
data class AvsluttetSak(val iverksatt: LocalDate, val stoppdato: LocalDate)