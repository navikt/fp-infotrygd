package no.nav.infotrygd.foreldrepenger.rest.dto

import no.nav.commons.foedselsnummer.FoedselsNr
import java.time.LocalDate

data class PersonRequest(
        val fom: LocalDate,
        val tom: LocalDate?,
        val fnr: List<String>
)