package no.nav.infotrygd.foreldrepenger.model.kodeverk

enum class SakNivaa(override val kode: String, override val tekst: String) : Kode {
    AN("AN", "Ankenemda"),
    FFU("FFU", "Utlandskontoret"),
    FTK("FTK", "FTK"),
    HTF("HTF", "Helsetrygdforvaltning"),
    KA("KA", "Klageinstansen"),
    KI("KI", "Klageinstansen"),
    RTV("RTV", "Rikstrygdeverket"),
    TK("TK", "Trygdekontor"),
    TR("TR", "Trygderetten"),
    UKJENT("   ", "Ukjent")
}