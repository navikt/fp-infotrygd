package no.nav.infotrygd.svangerskapspenger.rest.controller

import io.micrometer.core.annotation.Timed
import io.swagger.annotations.ApiParam
import no.nav.infotrygd.svangerskapspenger.rest.dto.CountDto
import no.nav.infotrygd.svangerskapspenger.rest.dto.SakResult
import no.nav.infotrygd.svangerskapspenger.service.ClientValidator
import no.nav.infotrygd.svangerskapspenger.service.SakService
import no.nav.security.oidc.api.Protected
import no.nav.infotrygd.svangerskapspenger.values.FodselNr
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate


@RestController
@RequestMapping
@Protected
class SakController(
    private val sakService: SakService,
    private val clientValidator: ClientValidator
) {
    @Protected
    @GetMapping("saker")
    @Timed(value = "time_sak_controller", percentiles = [0.5, 0.95])
    fun underBehandling(
            @RequestParam(required = true)
            fnr: String,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            @ApiParam("Finn avsluttede saker fra og med denne datoen. Defualt: Ett år tilbake i tid.", example = "1900-01-01")
            fom: LocalDate?): SakResult {

        clientValidator.authorizeClient()

        val defaultFom = LocalDate.now().minusYears(1)

        return sakService.findSakerByFnr(FodselNr(fnr), fom ?: defaultFom)
    }

    @GetMapping("count")
    fun count() : CountDto {
        return sakService.count()
    }
}