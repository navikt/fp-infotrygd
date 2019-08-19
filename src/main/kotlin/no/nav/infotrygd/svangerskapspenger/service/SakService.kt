package no.nav.infotrygd.svangerskapspenger.service

import no.nav.infotrygd.svangerskapspenger.model.Sak
import no.nav.infotrygd.svangerskapspenger.repository.SakRepository
import no.nav.infotrygd.svangerskapspenger.rest.dto.*
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class SakService(private val sakRepository: SakRepository) {
    fun findSakerByFnr(fnr: String): SakResult {

        return SakResult(
            info = null, //"Ingen klagesaker. Ingen ankesaker.",
            underBehandling = sakerByType(fnr, "S", "R"),
            klagesaker = sakerByType(fnr, "K"),
            ankesaker = sakerByType(fnr, "A"),


            // todo: implementer resten

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

    private fun sakerByType(fnr: String, vararg status: String): List<SakDto> {
        val saker = sakRepository.findSvangerskapssakerByFnrAndType(fnr, setOf(*status))
        return toDto(saker)
    }

    private fun toDto(saker: List<Sak>): List<SakDto> {
        return saker.map {
            SakDto(
                sakId = SakId(blokk = it.saksblokk, nr = it.saksnummer.toInt()),
                status = it.status.maxBy { it.lopeNr }?.status ?: "UKJENT",
                resultat = it.resultat,
                vedtatt = it.vedtaksdato,
                iverksatt = it.iverksattdato
            )
        }
    }
}