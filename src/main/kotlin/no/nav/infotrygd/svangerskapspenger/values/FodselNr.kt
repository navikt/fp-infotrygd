package no.nav.infotrygd.svangerskapspenger.values

import com.fasterxml.jackson.annotation.JsonValue

data class FodselNr(@JsonValue val asString: String) {
    init {
        require("""\d{11}""".toRegex().matches(asString)) { "Ikke et gyldig fødselsnummer: $asString" }
    }
}