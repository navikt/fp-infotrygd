package no.nav.infotrygd.svangerskapspenger.service

import no.nav.infotrygd.svangerskapspenger.repository.SakRepository
import no.nav.infotrygd.svangerskapspenger.rest.dto.*
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class SakService(private val sakRepository: SakRepository) {
    fun findSakerByFnr(fnr: String): SakResult {

        return SakResult(
            info = null, //"Ingen klagesaker. Ingen ankesaker.",
            underBehandling = sakerUnderBehandling(fnr),

            // todo: implementer resten

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

    private fun sakerUnderBehandling(fnr: String): List<SakDto> {
        val underBehandling = sakRepository.findSvangerskapssakerByFnrAndType(fnr, setOf("S", "R"))
            .map {
                SakDto(
                    sakId = SakId(blokk = it.saksblokk, nr = it.saksnummer.toInt()),
                    status = it.status.maxBy { it.lopeNr }?.status ?: "UKJENT",
                    resultat = it.resultat,
                    vedtatt = it.vedtaksdato,
                    iverksatt = it.iverksattdato
                )
            }
        return underBehandling
    }
}