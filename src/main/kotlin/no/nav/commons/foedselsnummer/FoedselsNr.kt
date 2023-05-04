package no.nav.commons.foedselsnummer

import com.fasterxml.jackson.annotation.JsonValue

data class FoedselsNr(@JsonValue val asString: String) {
    companion object {}

    init {
        require("""\d{11}""".toRegex().matches(asString)) { "Ikke et gyldig fødselsnummer: $asString" }
    }

    val kjoenn: Kjoenn
        get() {
            val kjoenn = asString[8].code
            return if(kjoenn % 2 == 0) Kjoenn.KVINNE else Kjoenn.MANN
        }
}