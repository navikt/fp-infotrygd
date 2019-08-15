package no.nav.infotrygd.svangerskapspenger.rest.controller

import no.nav.infotrygd.svangerskapspenger.rest.dto.SakResult
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("saker")
class SakController {

    @GetMapping("underBehandling")
    fun underBehandling(): SakResult {
        return SakResult(
            saker = listOf(),
            info = null
        )
    }

}