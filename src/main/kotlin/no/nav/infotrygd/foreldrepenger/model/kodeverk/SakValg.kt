package no.nav.infotrygd.foreldrepenger.model.kodeverk

enum class SakValg(override val kode: String, override val tekst: String) : Kode {
    ES_A("AE", "Engangsstønad adopsjon "),
    FP_A("AP", "Foreldrepenger adopsjon "),
    ES_F("FE", "Engangsstønad fødsel "),
    FP("FP", "Foreldrepenger"),
    FP_FU("FU", "Foreldrepenger fødsel, utland"),
    FP_F("FØ", "Foreldrepenger fødsel"),
    SVP("SV", "Svangerskapspenger")
}