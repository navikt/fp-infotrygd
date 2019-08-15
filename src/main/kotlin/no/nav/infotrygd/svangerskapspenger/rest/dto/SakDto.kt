package no.nav.infotrygd.svangerskapspenger.rest.dto

import java.time.LocalDate

data class SakResult(val saker: List<SakDto>, val info: String?)

data class SakDto(
    val sakId: SakId?,
    val status: String,
    val resultat: String,
    val vedtatt: LocalDate,
    val iverksatt: LocalDate
)

data class SakId(val blokk: String, val nr: Int)