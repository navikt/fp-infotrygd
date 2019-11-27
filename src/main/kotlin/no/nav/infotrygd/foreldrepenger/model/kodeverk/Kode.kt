package no.nav.infotrygd.foreldrepenger.model.kodeverk

import no.nav.infotrygd.foreldrepenger.rest.dto.Kodeverdi

interface Kode {
    val kode: String
    val tekst: String

    fun toDto(): Kodeverdi =
        Kodeverdi(kode, tekst)
}