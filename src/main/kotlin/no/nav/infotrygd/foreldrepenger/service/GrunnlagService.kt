package no.nav.infotrygd.foreldrepenger.service

import no.nav.commons.foedselsnummer.FoedselsNr
import no.nav.infotrygd.foreldrepenger.rest.dto.YtelseGrunnlag
import no.nav.infotrygd.foreldrepenger.model.kodeverk.Stoenadstype
import no.nav.infotrygd.foreldrepenger.repository.PeriodeRepository
import no.nav.infotrygd.foreldrepenger.repository.VedtakBarnRepository
import org.springframework.stereotype.Service
import java.time.LocalDate
import jakarta.transaction.Transactional
import org.slf4j.Logger
import org.slf4j.LoggerFactory

@Service
@Transactional
class GrunnlagService(
    private val periodeRepository: PeriodeRepository,
    private val vedtakBarnRepository: VedtakBarnRepository
) {

    private val LOG = LoggerFactory.getLogger(javaClass)

    fun hentYtelse(stoenadstyper: List<Stoenadstype>, foedselsNr: FoedselsNr, fom: LocalDate, tom: LocalDate?): List<YtelseGrunnlag> {

        val result = periodeRepository.findByFnrAndStoenadstype(foedselsNr, stoenadstyper)
        LOG.info("Funnet {} resultater.", result.size)
        val riktigePerioder = result.filter { it.innenforPeriode(fom, tom) }
        LOG.info("Funnet {} resultater etter periode filtrering.", riktigePerioder.size)

        return riktigePerioder.map { periode ->
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
}