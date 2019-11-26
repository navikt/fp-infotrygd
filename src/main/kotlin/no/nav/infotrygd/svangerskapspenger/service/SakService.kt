package no.nav.infotrygd.svangerskapspenger.service

import no.nav.commons.foedselsnummer.FoedselsNr
import no.nav.infotrygd.svangerskapspenger.model.Sak
import no.nav.infotrygd.svangerskapspenger.model.Utbetaling
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
    fun count(): CountDto {
        return CountDto(
            periodeTabell =  periodeRepository.count(),
            sakTabell = sakRepository.count()
        )
    }

    fun findSakerByFnr(fnr: FoedselsNr, fom: LocalDate): SakResult {
        return SakResult(
            info = null, //"Ingen klagesaker. Ingen ankesaker.",
            saker = toDto(sakRepository.findSvangerskapssakerByFnr(fnr)),
            apneSakerMedLopendeUtbetaling = apneSakerMedLopendeUtbetaling(fnr),
            avsluttedeSaker = avsluttedeSaker(fnr, fom)
        )
    }

    private fun apneSakerMedLopendeUtbetaling(fnr: FoedselsNr): List<ApenSakMedLopendeUtbetaling> {
        return periodeRepository.findOpneSakerMedLopendeUtbetalingByFnr(fnr).map { periode ->
            ApenSakMedLopendeUtbetaling(
                iverksatt = periode.arbufoer,
                utbetalinger = periode.utbetalinger.map { toDto(it) },
                registrert = periode.registrert
            )
        }
    }

    private fun toDto(it: Utbetaling): UtbetalingDto {
        return UtbetalingDto(
            utbetaltFom = it.utbetaltFom,
            utbetaltTom = it.utbetaltTom,
            gradering = it.grad ?: 100
        )
    }

    private fun avsluttedeSaker(fnr: FoedselsNr, fom: LocalDate): AvsluttedeSaker {

        return AvsluttedeSaker(
            fraOgMed = fom,
            saker = periodeRepository.findAvsluttedeSakerByFnr(fnr, fom)
                .map { periode ->
                    AvsluttetSak(
                    iverksatt = periode.arbufoer,
                    stoppdato = periode.stoppdato,
                    registrert = periode.registrert,
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
                iverksatt = it.iverksattdato,
                type = it.type.trim(),
                registrert = it.registrert
            )
        }
    }
}