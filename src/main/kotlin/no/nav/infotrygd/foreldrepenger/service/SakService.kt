package no.nav.infotrygd.foreldrepenger.service

import no.nav.commons.foedselsnummer.FoedselsNr
import no.nav.infotrygd.foreldrepenger.model.Sak
import no.nav.infotrygd.foreldrepenger.model.Utbetaling
import no.nav.infotrygd.foreldrepenger.repository.PeriodeRepository
import no.nav.infotrygd.foreldrepenger.repository.SakRepository
import no.nav.infotrygd.foreldrepenger.rest.dto.*
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class SakService(
    private val sakRepository: SakRepository,
    private val periodeRepository: PeriodeRepository
) {
    fun findSakerByFnr(fnr: FoedselsNr, fom: LocalDate, tom: LocalDate?): SakResult {
        val saker = sakRepository.findSakerByFnr(fnr)
            .filter { it.innenforPeriode(fom, tom) }

        return SakResult(
            info = null, //"Ingen klagesaker. Ingen ankesaker.",
            saker = toDto(saker),
            apneSakerMedLopendeUtbetaling = apneSakerMedLopendeUtbetaling(fnr, fom, tom),
            avsluttedeSaker = avsluttedeSaker(fnr, fom, tom),
            ikkeStartet = ikkeStartetSaker(fnr, fom, tom)
        )
    }

    private fun apneSakerMedLopendeUtbetaling(fnr: FoedselsNr, fom: LocalDate, tom: LocalDate?): List<ApenSakMedLopendeUtbetaling> {
        return periodeRepository.findByFnrAndFrisk(fnr, PeriodeRepository.lopende)
            .filter { it.innenforPeriode(fom, tom) }
            .map { periode ->
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

    private fun avsluttedeSaker(fnr: FoedselsNr, fom: LocalDate, tom: LocalDate?): AvsluttedeSaker {
        return AvsluttedeSaker(
            fraOgMed = fom,
            saker = periodeRepository.findByFnrAndFrisk(fnr, PeriodeRepository.avsluttede)
                .filter { it.innenforPeriode(fom, tom) }
                .map { periode ->
                    AvsluttetSak(
                        iverksatt = periode.arbufoer,
                        stoppdato = periode.opphoerFom,
                        registrert = periode.registrert,
                        utbetalinger = periode.utbetalinger.map { toDto(it) }
                    ) }
        )
    }

    private fun ikkeStartetSaker(fnr: FoedselsNr, fom: LocalDate, tom: LocalDate?): List<IkkeStartet> {
        return periodeRepository.findByFnrAndFrisk(fnr, PeriodeRepository.ikkeStartet)
            .filter { it.innenforPeriode(fom, tom) }
            .map { IkkeStartet(
                iverksatt = it.arbufoer,
                registrert = it.registrert
            ) }
    }

    private fun toDto(saker: List<Sak>): List<SakDto> {
        return saker.map {
            SakDto(
                sakId = SakId(blokk = it.saksblokk, nr = it.saksnummer.toInt()),
                status = it.status.kode,
                resultat = it.resultat,
                vedtatt = it.vedtaksdato,
                iverksatt = it.iverksattdato,
                type = it.type.trim(),
                registrert = it.registrert
            )
        }
    }
}