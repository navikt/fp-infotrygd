package no.nav.infotrygd.svangerskapspenger.model.kodeverk

enum class Status(override val kode: String, override val tekst: String) : Kode {
    LOEPENDE("L", "Løpende"),
    AVSLUTTET("A", "Avsluttet"),
    IKKE_STARTET("I", "Ikke startet")
}