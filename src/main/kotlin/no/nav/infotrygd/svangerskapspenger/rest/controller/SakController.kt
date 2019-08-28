package no.nav.infotrygd.svangerskapspenger.rest.controller

import no.nav.infotrygd.svangerskapspenger.rest.dto.CountDto
import no.nav.infotrygd.svangerskapspenger.rest.dto.SakResult
import no.nav.infotrygd.svangerskapspenger.service.SakService
import no.nav.infotrygd.svangerskapspenger.values.FodselNr
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping
class SakController(private val sakService: SakService) {

    @GetMapping("saker")
    fun underBehandling(@RequestParam(required = true) fnr: String): SakResult {
        return sakService.findSakerByFnr(FodselNr(fnr))
    }

    @GetMapping("count")
    fun count() : CountDto {
        return sakService.count()
    }
}