package no.nav.infotrygd.foreldrepenger.rest.controller

import io.micrometer.core.annotation.Timed
import io.swagger.annotations.ApiParam
import no.nav.commons.foedselsnummer.FoedselsNr
import no.nav.infotrygd.foreldrepenger.model.kodeverk.Stoenadstype
import no.nav.infotrygd.foreldrepenger.rest.dto.Foreldrepenger
import no.nav.infotrygd.foreldrepenger.rest.dto.SakResult
import no.nav.infotrygd.foreldrepenger.service.ClientValidator
import no.nav.infotrygd.foreldrepenger.service.GrunnlagService
import no.nav.infotrygd.foreldrepenger.service.SakService
import no.nav.security.oidc.api.Protected
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate

@RestController
@RequestMapping
@Protected
@Timed(value = "infotrygd_foreldrepenger_controller", percentiles = [0.5, 0.95])
class InfotrygdController(
    private val sakService: SakService,
    private val clientValidator: ClientValidator,
    private val grunnlagService: GrunnlagService
) {
    @Protected
    @GetMapping("/saker")
    fun saker(
        @RequestParam(required = true)
        fnr: String,

        @RequestParam(required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        @ApiParam("Finn saker fra og med denne datoen. Defualt: Ett år tilbake i tid.", example = "1900-01-01")
        fom: LocalDate?,

        @RequestParam(required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        @ApiParam("Finn saker til og med denne datoen.", example = "2019-01-01")
        tom: LocalDate?): SakResult {

        clientValidator.authorizeClient()

        val defaultFom = LocalDate.now().minusYears(1)

        return sakService.findSakerByFnr(FoedselsNr(fnr), fom ?: defaultFom, tom)
    }

    @GetMapping(path = ["/grunnlag"])
    fun grunnlag(@RequestParam
                 fnr: String,

                 @RequestParam
                 @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                 fom: LocalDate,

                 @RequestParam(required = false)
                 @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                 tom: LocalDate?) : List<Foreldrepenger> {

        clientValidator.authorizeClient()
        return grunnlagService.hentForeldrepenger(listOf(Stoenadstype.ADOPSJON, Stoenadstype.FOEDSEL), FoedselsNr(fnr), fom, tom)
    }
}