package no.nav.infotrygd.svangerskapspenger.service

import no.nav.infotrygd.svangerskapspenger.model.Sak
import no.nav.infotrygd.svangerskapspenger.model.Utbetaling
import no.nav.infotrygd.svangerskapspenger.repository.PeriodeRepository
import no.nav.infotrygd.svangerskapspenger.repository.SakRepository
import no.nav.infotrygd.svangerskapspenger.rest.dto.*
import no.nav.infotrygd.svangerskapspenger.values.FodselNr
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class SakService(
    private val sakRepository: SakRepository,
    private val periodeRepository: PeriodeRepository
) {
    fun count(): CountDto {
        return CountDto(
            periodeTabell =  periodeRepository.count(),
            sakTabell = sakRepository.count()
        )
    }

    fun findSakerByFnr(fnr: FodselNr): SakResult {
        return SakResult(
            info = null, //"Ingen klagesaker. Ingen ankesaker.",
            underBehandling = sakerByType(fnr, "S ", "R "),
            klagesaker = sakerByType(fnr, "K "),
            ankesaker = sakerByType(fnr, "A "),
            apneSakerMedLopendeUtbetaling = apneSakerMedLopendeUtbetaling(fnr),
            avsluttedeSaker = avsluttedeSaker(fnr)
        )
    }

    private fun sakerByType(fnr: FodselNr, vararg type: String): List<SakDto> {
        val saker = sakRepository.findSvangerskapssakerByFnrAndType(fnr, setOf(*type))
        return toDto(saker)
    }

    private fun apneSakerMedLopendeUtbetaling(fnr: FodselNr): List<ApenSakMedLopendeUtbetaling> {
        return periodeRepository.findOpneSakerMedLopendeUtbetalingByFnr(fnr).map { periode ->
            ApenSakMedLopendeUtbetaling(
                iverksatt = periode.arbufoer,
                utbetalinger = periode.utbetalinger.map { toDto(it) }
            )
        }
    }

    private fun toDto(it: Utbetaling): UtbetalingDto {
        return UtbetalingDto(
            utbetaltFom = it.utbetaltFom,
            utbetaltTom = it.utbetaltTom,
            gradering = it.grad
        )
    }

    private fun avsluttedeSaker(fnr: FodselNr): AvsluttedeSaker {
        val fom = LocalDate.now().minusYears(1)

        return AvsluttedeSaker(
            fraOgMed = fom,
            saker = periodeRepository.findAvsluttedeSakerByFnr(fnr, fom)
                .map { periode ->
                    AvsluttetSak(
                    iverksatt = periode.arbufoer,
                    stoppdato = periode.stoppdato
                        ?: throw IllegalStateException("Forsøkte å returnere en avsluttet sak uten stoppdato."),
                    utbetalinger = periode.utbetalinger.map { toDto(it) }
                ) }
        )
    }

    private fun toDto(saker: List<Sak>): List<SakDto> {
        return saker.map {
            SakDto(
                sakId = SakId(blokk = it.saksblokk, nr = it.saksnummer.toInt()),
                status = it.status.minBy { it.lopeNr }?.status ?: "UKJENT",
                resultat = it.resultat.trim(),
                vedtatt = it.vedtaksdato,
                iverksatt = it.iverksattdato
            )
        }
    }
}