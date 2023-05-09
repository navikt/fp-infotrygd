package no.nav.infotrygd.foreldrepenger.rest.controller

import io.swagger.v3.oas.annotations.Parameter
import no.nav.commons.foedselsnummer.FoedselsNr
import no.nav.infotrygd.foreldrepenger.config.ApplicationUtil
import no.nav.infotrygd.foreldrepenger.model.kodeverk.Stoenadstype
import no.nav.infotrygd.foreldrepenger.rest.dto.SakResult
import no.nav.infotrygd.foreldrepenger.rest.dto.YtelseGrunnlag
import no.nav.infotrygd.foreldrepenger.service.ClientValidator
import no.nav.infotrygd.foreldrepenger.service.GrunnlagService
import no.nav.infotrygd.foreldrepenger.service.SakService
import no.nav.security.token.support.core.api.Unprotected
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate

@RestController
@RequestMapping
@Unprotected
class InfotrygdController(
    private val sakService: SakService,
    private val clientValidator: ClientValidator,
    private val grunnlagService: GrunnlagService,
    private val appUtil: ApplicationUtil
) {
    private val LOG: Logger = LoggerFactory.getLogger(javaClass)

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

        clientValidator.authorizeClient()

        val defaultFom = LocalDate.now().minusYears(1)
        LOG.info("Henter saker for SVP.")
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
        @Parameter(description = "Finn saker til og med denne datoen.", example = "2019-01-01")
        tom: LocalDate?
    ): List<YtelseGrunnlag> {

        clientValidator.authorizeClient()

        return if (appUtil.gjelderSvangerskapspenger()) {
            LOG.info("Henter grunnlag for SVP.")
            grunnlagService.hentYtelse(
                setOf(
                    Stoenadstype.SVANGERSKAP,
                    Stoenadstype.RISIKOFYLT_ARBMILJOE
                ), FoedselsNr(fnr), fom, tom
            )
        } else {
            LOG.info("Henter grunnlag for FP.")
            grunnlagService.hentYtelse(
                setOf(
                    Stoenadstype.ADOPSJON,
                    Stoenadstype.FOEDSEL
                ), FoedselsNr(fnr), fom, tom
            )
        }
    }
}