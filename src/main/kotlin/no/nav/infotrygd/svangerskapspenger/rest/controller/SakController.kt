package no.nav.infotrygd.svangerskapspenger.rest.controller

import no.nav.infotrygd.svangerskapspenger.rest.dto.*
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate

@RestController
@RequestMapping("saker")
class SakController {

    @GetMapping("underBehandling")
    fun underBehandling(): SakResult {
        val eksempelsak = SakDto(
            sakId = SakId(blokk = "X", nr = 42),
            status = "XY",
            resultat = "XY",
            vedtatt = LocalDate.now(),
            iverksatt = LocalDate.now()
        )

        return SakResult(
            info = "Ingen klagesaker. Ingen ankesaker.",
            underBehandling = listOf(eksempelsak),
            klagesaker = listOf(),
            ankesaker = listOf(),

            apneSakerMedLopendeUtbetaling = listOf(
                ApenSakMedLopendeUtbetaling(iverksatt = LocalDate.now())
            ),
            avsluttedeSaker = AvsluttedeSaker(
                fraOgMed = LocalDate.now().minusYears(1),
                saker = listOf(
                    AvsluttetSak(
                        iverksatt = LocalDate.now(),
                        stoppdato = LocalDate.now().plusDays(1))
                )
            )
        )
    }

}