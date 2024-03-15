package no.nav.infotrygd.foreldrepenger.model.kodeverk

enum class SakUndervalg(override val kode: String, override val tekst: String) : Kode {
    FG("FG", "fedrekvote gradering"),
    FK("FK", "fedrekvote ordinær"),
    FU("FU", "fedrekvote utsettelse"),
    GA("GA", "gradering"),
    NA("NA", "nasjonal"), // Kun engangsstønad
    OS("OS", "ordinær"),
    UA("UA", "utsettelse fulltidsarbeid"),
    UF("UF", "utsettelse ferie"),
    UL("UL", "utland"),  // Kun engangsstønad
    US("US", "utsettelse sykdom"),
    UKJENT("  ", "annet")
}