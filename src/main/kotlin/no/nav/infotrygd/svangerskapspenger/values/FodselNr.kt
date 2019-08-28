package no.nav.infotrygd.svangerskapspenger.values

import com.fasterxml.jackson.annotation.JsonValue
import java.lang.IllegalArgumentException

data class FodselNr(@JsonValue val asString: String) {
    init {
        if(!"""\d{11}""".toRegex().matches(asString)) {
            throw IllegalArgumentException("Ikke et gyldig fødselsnummer: $asString")
        }
    }
}