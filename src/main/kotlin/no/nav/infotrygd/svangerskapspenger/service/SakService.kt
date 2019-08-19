package no.nav.infotrygd.svangerskapspenger.service

import no.nav.infotrygd.svangerskapspenger.repository.SakRepository
import no.nav.infotrygd.svangerskapspenger.rest.dto.*
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class SakService(private val sakRepository: SakRepository) {
    fun findSakerByFnr(fnr: String): SakResult {
        val eksempelsak = SakDto(
            sakId = SakId(blokk = "X", nr = 42),
            status = "XY",
            resultat = "XY",
            vedtatt = LocalDate.now(),
            iverksatt = LocalDate.now()
        )

        val underBehandling = sakRepository.findSvangerskapssakerByFnrAndType(fnr, setOf("S", "R"))
            .map { SakDto(
                sakId = SakId(blokk = it.saksblokk, nr = it.saksnummer.toInt()), // todo: trygt?
                status = "TODO: IMPLEMENT",
                resultat = it.resultat,
                vedtatt = it.vedtaksdato,
                iverksatt = it.iverksattdato
            ) }

        return SakResult(
            info = "Ingen klagesaker. Ingen ankesaker.",
            underBehandling = underBehandling,
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