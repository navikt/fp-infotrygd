package no.nav.infotrygd.svangerskapspenger.service

import no.nav.commons.foedselsnummer.FoedselsNr
import no.nav.infotrygd.svangerskapspenger.rest.dto.Foreldrepenger
import no.nav.infotrygd.svangerskapspenger.model.kodeverk.Stoenadstype
import no.nav.infotrygd.svangerskapspenger.repository.PeriodeRepository
import no.nav.infotrygd.svangerskapspenger.repository.VedtakBarnRepository
import org.springframework.stereotype.Service
import java.time.LocalDate
import javax.transaction.Transactional

@Service
@Transactional
class GrunnlagService(
    private val periodeRepository: PeriodeRepository,
    private val vedtakBarnRepository: VedtakBarnRepository
) {
    fun hentForeldrepenger(stoenadstyper: List<Stoenadstype>, foedselsNr: FoedselsNr, fom: LocalDate, tom: LocalDate?): List<Foreldrepenger> {

        val result = if(tom != null) {
            periodeRepository.findByFnrAndStoenadstypeAndDates(foedselsNr, stoenadstyper, fom, tom)
        } else {
            periodeRepository.findByFnrAndStoenadstypeAndDates(foedselsNr, stoenadstyper, fom)
        }

        return result.map { periode ->
            val vedtak = periode.barnPersonKey?.let { barnPersonKey ->
                vedtakBarnRepository.findByPersonKeyAndArbufoerSeqAndKode(
                    personKey = barnPersonKey,
                    arbufoerSeq = periode.arbufoerSeq.toString(),
                    kode = periode.barnKode
                )
            }
            Foreldrepenger(
                generelt = periodeToGrunnlag(periode),
                foreldrepengerDetaljer = periodeToForeldrepengerDetaljer(periode, vedtak)
            )
        }
    }
}