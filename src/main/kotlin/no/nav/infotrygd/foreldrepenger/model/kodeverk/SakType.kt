package no.nav.infotrygd.foreldrepenger.model.kodeverk

enum class SakType(override val kode: String, override val tekst: String) : Kode {
    S("S", "Søknad"),
    R("R", "Revurdering"),
    K("K", "Klage"),
    A("A", "Anke"),
    DF("DF", "Dispensasjon foreldelse"),
    DI("DI", "Dokumentinnsyn"),
    EG("EG", "Etterlyse girokort"),
    FS("FS", "Forespørsel"),
    I("I", "Informasjonssak"),
    J("J", "Journalsak"),
    JP("JP", "Journalsak fra privatperson"),
    JT("JT", "Journalsak fra trygdekontor"),
    JU("JU", "Journalsak fra utenl trm"),
    KE("KE", "Klage ettergivelse"),
    KS("KS", "Kontrollsak"),
    KT("KT", "Klage tilbakebetaling"),
    SE("SE", "Søknad om ettergivelse"),
    SV("SV", "Strafferettslig vurdering"),
    T("T", "Tilbakebetalingssak"),
    TE("TE", "Tilbakebetaling endring"),
    TK("TK", "Tidskonto"),
    TU("TU", "Tipsutredning"),
    UA("UA", "Utbetalt til annen")
}