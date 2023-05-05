package no.nav.infotrygd.foreldrepenger.testutil.rest

import no.nav.commons.foedselsnummer.FoedselsNr
import no.nav.infotrygd.foreldrepenger.rest.dto.YtelseGrunnlag
import no.nav.infotrygd.foreldrepenger.rest.dto.SakResult
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.getForObject
import java.time.LocalDate

class TestClient(private val restTemplate: RestTemplate) {
    fun hentSaker(fnr: FoedselsNr): SakResult {
        return restTemplate.getForObject("/saker?fnr=${fnr.asString}")
    }

    fun hentGrunnlag(fnr: FoedselsNr, fom: LocalDate): List<YtelseGrunnlag> {
        return restTemplate.getForObject("/grunnlag?fnr=${fnr.asString}&fom=${fom}")
    }
}