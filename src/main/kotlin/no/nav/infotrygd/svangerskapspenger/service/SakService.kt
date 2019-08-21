package no.nav.infotrygd.svangerskapspenger.service

import no.nav.infotrygd.svangerskapspenger.model.Sak
import no.nav.infotrygd.svangerskapspenger.repository.PeriodeRepository
import no.nav.infotrygd.svangerskapspenger.repository.SakRepository
import no.nav.infotrygd.svangerskapspenger.rest.dto.*
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class SakService(
    private val sakRepository: SakRepository,
    private val periodeRepository: PeriodeRepository
) {
    fun findSakerByFnr(fnr: String): SakResult {
        return SakResult(
            info = null, //"Ingen klagesaker. Ingen ankesaker.",
            underBehandling = sakerByType(fnr, "S", "R"),
            klagesaker = sakerByType(fnr, "K"),
            ankesaker = sakerByType(fnr, "A"),
            apneSakerMedLopendeUtbetaling = apneSakerMedLopendeUtbetaling(fnr),
            avsluttedeSaker = avsluttedeSaker(fnr)
        )
    }

    private fun sakerByType(fnr: String, vararg status: String): List<SakDto> {
        val saker = sakRepository.findSvangerskapssakerByFnrAndType(fnr, setOf(*status))
        return toDto(saker)
    }

    private fun apneSakerMedLopendeUtbetaling(fnr: String): List<ApenSakMedLopendeUtbetaling> {
        return periodeRepository.findOpneSakerMedLopendeUtbetaling(fnr).map {
            ApenSakMedLopendeUtbetaling(iverksatt = it.arbufoer)
        }
    }

    private fun avsluttedeSaker(fnr: String): AvsluttedeSaker {
        val fom = LocalDate.now().minusYears(1)

        return AvsluttedeSaker(
            fraOgMed = fom,
            saker = periodeRepository.findAvsluttedeSakerByFnr(fnr, fom)
                .map { AvsluttetSak(
                    iverksatt = it.arbufoer,
                    stoppdato = it.stoppdato
                        ?: throw IllegalStateException("Forsøkte å returnere en avsluttet sak uten stoppdato.")
                ) }
        )
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