package no.nav.infotrygd.foreldrepenger.rest.controller

import io.swagger.v3.oas.annotations.Parameter
import no.nav.commons.foedselsnummer.FoedselsNr
import no.nav.infotrygd.foreldrepenger.rest.dto.PersonRequest
import no.nav.infotrygd.foreldrepenger.rest.dto.SakResult
import no.nav.infotrygd.foreldrepenger.rest.dto.YtelseGrunnlag
import no.nav.infotrygd.foreldrepenger.service.GrunnlagService
import no.nav.infotrygd.foreldrepenger.service.SakService
import no.nav.security.token.support.core.api.Unprotected
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.web.bind.annotation.*
import java.time.LocalDate

@RestController
@RequestMapping
@Unprotected
class InfotrygdController(
    private val sakService: SakService,
    private val grunnlagService: GrunnlagService
) {
    @GetMapping("/saker")
    fun saker(
        @RequestParam(required = true)
        fnr: String,

        @RequestParam(required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        @Parameter(
            description = "Finn saker fra og med denne datoen. Defualt: Ett år tilbake i tid.",
            example = "1970-01-01"
        )
        fom: LocalDate?,

        @RequestParam(required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        @Parameter(description = "Finn saker til og med denne datoen.", example = "2019-01-01")
        tom: LocalDate?
    ): SakResult {

        val defaultFom = LocalDate.now().minusYears(1)

        return sakService.findSakerByFnr(FoedselsNr(fnr), fom ?: defaultFom, tom)
    }

    @GetMapping(path = ["/grunnlag"])
    fun grunnlag(
        @RequestParam
        fnr: String,

        @RequestParam
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        @Parameter(description = "Finn grunnlag fra og med denne datoen.", example = "1970-01-01")
        fom: LocalDate,

        @RequestParam(required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        @Parameter(description = "Finn saker til og med denne datoen.", example = "2024-01-01")
        tom: LocalDate?
    ): List<YtelseGrunnlag> {

        return grunnlagService.hentYtelse(FoedselsNr(fnr), fom, tom)
    }

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
}