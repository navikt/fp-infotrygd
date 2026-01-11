package no.nav.infotrygd.foreldrepenger.testutil.rest

import no.nav.commons.foedselsnummer.FoedselsNr
import no.nav.infotrygd.foreldrepenger.rest.dto.PersonRequest
import no.nav.infotrygd.foreldrepenger.rest.dto.SakDto
import no.nav.infotrygd.foreldrepenger.rest.dto.YtelseGrunnlag
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.web.client.RestTemplate
import tools.jackson.module.kotlin.jacksonObjectMapper
import java.time.LocalDate

class TestClient(private val restTemplate: RestTemplate) {

    private val jacksonObjectMapper = jacksonObjectMapper()


    fun hentSaker(fnr: FoedselsNr, fom: LocalDate): List<SakDto> {
        val request = PersonRequest(fom, null, listOf( fnr.asString));
        val requestJson = jacksonObjectMapper.writeValueAsString(request)
        val entity = HttpEntity(requestJson, headers())

        val respons = restTemplate.exchange("/sak", HttpMethod.POST, entity, String::class.java).body
        val arr = jacksonObjectMapper.readValue(respons, Array<SakDto>::class.java)
        return arr.toList()
    }

    fun hentGrunnlag(fnr: FoedselsNr, fom: LocalDate): List<YtelseGrunnlag> {
        val request = PersonRequest(fom, null, listOf( fnr.asString));
        val requestJson = jacksonObjectMapper.writeValueAsString(request)
        val entity = HttpEntity(requestJson, headers())

        val respons =  restTemplate.exchange("/grunnlag", HttpMethod.POST, entity, String::class.java).body
        val arr = jacksonObjectMapper.readValue(respons, Array<YtelseGrunnlag>::class.java)
        return arr.toList()
    }

    fun headers(): org.springframework.http.HttpHeaders {
        return org.springframework.http.HttpHeaders().apply {
            contentType = MediaType.APPLICATION_JSON
            accept = listOf(MediaType.APPLICATION_JSON)
        }

    }
}