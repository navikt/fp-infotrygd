package no.nav.infotrygd.svangerskapspenger.model.kodeverk

import no.nav.infotrygd.svangerskapspenger.rest.dto.Kodeverdi

interface Kode {
    val kode: String
    val tekst: String

    fun toDto(): Kodeverdi =
        Kodeverdi(kode, tekst)
}