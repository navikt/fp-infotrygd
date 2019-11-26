package no.nav.infotrygd.svangerskapspenger.rest.controller

import io.micrometer.core.annotation.Timed
import io.swagger.annotations.ApiParam
import no.nav.commons.foedselsnummer.FoedselsNr
import no.nav.infotrygd.svangerskapspenger.model.kodeverk.Stoenadstype
import no.nav.infotrygd.svangerskapspenger.rest.dto.Foreldrepenger
import no.nav.infotrygd.svangerskapspenger.rest.dto.SakResult
import no.nav.infotrygd.svangerskapspenger.service.ClientValidator
import no.nav.infotrygd.svangerskapspenger.service.GrunnlagService
import no.nav.infotrygd.svangerskapspenger.service.SakService
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
@Timed(value = "infotrygd_svangerskapspenger_controller", percentiles = [0.5, 0.95])
class InfotrygdController(
    private val sakService: SakService,
    private val clientValidator: ClientValidator,
    private val grunnlagService: GrunnlagService
) {
    @Protected
    @GetMapping("saker")
    fun saker(
            @RequestParam(required = true)
            fnr: String,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            @ApiParam("Finn avsluttede saker fra og med denne datoen. Defualt: Ett år tilbake i tid.", example = "1900-01-01")
            fom: LocalDate?): SakResult {

        clientValidator.authorizeClient()

        val defaultFom = LocalDate.now().minusYears(1)

        return sakService.findSakerByFnr(FoedselsNr(fnr), fom ?: defaultFom)
    }

    @GetMapping(path = ["/grunnlag"])
    fun grunnlag(@RequestParam
                    fodselNr: String,

                    @RequestParam
                    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                    fom: LocalDate,

                    @RequestParam(required = false)
                    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                    tom: LocalDate?) : List<Foreldrepenger> {

        clientValidator.authorizeClient()
        return grunnlagService.hentForeldrepenger(listOf(Stoenadstype.SVANGERSKAP, Stoenadstype.RISIKOFYLT_ARBMILJOE), FoedselsNr(fodselNr), fom, tom)
    }
}