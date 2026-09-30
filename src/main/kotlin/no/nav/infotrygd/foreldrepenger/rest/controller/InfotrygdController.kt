package no.nav.infotrygd.foreldrepenger.rest.controller

import no.nav.commons.foedselsnummer.FoedselsNr
import no.nav.infotrygd.foreldrepenger.sikkerhet.EntraCCRequired
import no.nav.infotrygd.foreldrepenger.rest.dto.PersonRequest
import no.nav.infotrygd.foreldrepenger.rest.dto.RestanseDto
import no.nav.infotrygd.foreldrepenger.rest.dto.SakDto
import no.nav.infotrygd.foreldrepenger.rest.dto.YtelseGrunnlag
import no.nav.infotrygd.foreldrepenger.service.GrunnlagService
import no.nav.infotrygd.foreldrepenger.service.SakService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping
class InfotrygdController(
    private val sakService: SakService,
    private val grunnlagService: GrunnlagService
) {

    @EntraCCRequired
    @PostMapping(path = [ "/grunnlag"])
    fun postGrunnlag(@RequestBody request: PersonRequest): List<YtelseGrunnlag> {
        val fnrList = request.fnr

        return fnrList.map { fnr: String ->
            grunnlagService.hentYtelse(
                    FoedselsNr(fnr),
                    request.fom,
                    request.tom
            )
        }.flatMap { it.toList() }
    }

    @EntraCCRequired
    @PostMapping(path = [ "/sak"])
    fun postSak(@RequestBody request: PersonRequest): List<SakDto> {
        val fnrList = request.fnr

        return fnrList.map { fnr: String ->
            sakService.findSakerByFnr(
                    FoedselsNr(fnr),
                    request.fom,
                    request.tom
            )
        }.flatMap { it.toList() }
    }

    @EntraCCRequired
    @GetMapping(path = [ "/restanse"])
    fun getRestanse(): List<RestanseDto> {
        return sakService.findRestanse()
    }
}