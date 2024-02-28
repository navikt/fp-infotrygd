package no.nav.infotrygd.foreldrepenger.service

import no.nav.commons.foedselsnummer.FoedselsNr
import no.nav.infotrygd.foreldrepenger.rest.dto.YtelseGrunnlag
import no.nav.infotrygd.foreldrepenger.model.kodeverk.Stoenadstype
import no.nav.infotrygd.foreldrepenger.repository.PeriodeRepository
import no.nav.infotrygd.foreldrepenger.repository.VedtakBarnRepository
import org.springframework.stereotype.Service
import java.time.LocalDate
import jakarta.transaction.Transactional
import org.slf4j.LoggerFactory

@Service
@Transactional
class GrunnlagService(
    private val periodeRepository: PeriodeRepository,
    private val vedtakBarnRepository: VedtakBarnRepository,
    private val appUtil: ApplicationUtil
) {
    private val LOG = LoggerFactory.getLogger(javaClass)

    fun hentYtelse(foedselsNr: FoedselsNr, fom: LocalDate, tom: LocalDate?): List<YtelseGrunnlag> {

        val result = periodeRepository.findByFnrAndStoenadstype(foedselsNr, getStønadstyper())
            .filter { it.innenforPeriode(fom, tom) }

        LOG.debug("Funnet {} resultater etter periode filtrering.", result.size)

        return result.map { periode ->
            val vedtak = periode.barnPersonKey?.let { barnPersonKey ->
                vedtakBarnRepository.findByPersonKeyAndArbufoerSeqAndKodeAndRegion(
                    personKey = barnPersonKey,
                    arbufoerSeq = periode.arbufoerSeq.toString(),
                    kode = periode.barnKode,
                    region = periode.region
                )
            }
            YtelseGrunnlag(
                generelt = periodeToGrunnlag(periode),
                ytelseDetaljer = periodeToForeldrepengerDetaljer(periode, vedtak)
            )
        }
    }

    private fun getStønadstyper(): Set<Stoenadstype> {
        return when(appUtil.getApplication()) {
            ApplicationUtil.Application.INFOTRYGD_FORELDREPENGER -> PeriodeRepository.stønadstypeFp
            ApplicationUtil.Application.INFOTRYGD_SVANGERSKAPSPENGER -> PeriodeRepository.stønadstypeSvp
            ApplicationUtil.Application.INFOTRYGD_SYKEPENGER -> PeriodeRepository.stønadstypeSp
        }
    }
}