package no.nav.infotrygd.foreldrepenger.service

import jakarta.transaction.Transactional
import no.nav.commons.foedselsnummer.FoedselsNr
import no.nav.infotrygd.foreldrepenger.model.kodeverk.Stoenadstype
import no.nav.infotrygd.foreldrepenger.repository.PeriodeRepository
import no.nav.infotrygd.foreldrepenger.repository.VedtakBarnRepository
import no.nav.infotrygd.foreldrepenger.rest.dto.YtelseGrunnlag
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.LocalDate

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

        val resultat =  result.map { periode ->
            val vedtak = periode.barnPersonKey?.let { barnPersonKey ->
                vedtakBarnRepository.findByPersonKeyAndArbufoerSeqAndKodeAndRegion(
                    personKey = barnPersonKey,
                    arbufoerSeq = periode.arbufoerSeq.toString(),
                    kode = periode.barnKode,
                    region = periode.region
                )
            }
            periodeToGrunnlag(periode, vedtak)
        }
        return resultat
    }

    private fun getStønadstyper(): Set<Stoenadstype> {
        return when(appUtil.getApplication()) {
            ApplicationUtil.Application.INFOTRYGD_FORELDREPENGER -> PeriodeRepository.stønadstypeFp
            ApplicationUtil.Application.INFOTRYGD_SVANGERSKAPSPENGER -> PeriodeRepository.stønadstypeSvp
            ApplicationUtil.Application.INFOTRYGD_SYKEPENGER -> PeriodeRepository.stønadstypeSp
        }
    }

}