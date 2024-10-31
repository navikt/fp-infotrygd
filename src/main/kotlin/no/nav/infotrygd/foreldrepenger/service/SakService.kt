package no.nav.infotrygd.foreldrepenger.service

import jakarta.transaction.Transactional
import no.nav.commons.foedselsnummer.FoedselsNr
import no.nav.infotrygd.foreldrepenger.model.Sak
import no.nav.infotrygd.foreldrepenger.model.kodeverk.SakResultat
import no.nav.infotrygd.foreldrepenger.model.kodeverk.SakValg
import no.nav.infotrygd.foreldrepenger.repository.SakRepository
import no.nav.infotrygd.foreldrepenger.rest.dto.RestanseDto
import no.nav.infotrygd.foreldrepenger.rest.dto.SakDto
import no.nav.infotrygd.foreldrepenger.rest.dto.SakId
import no.nav.infotrygd.foreldrepenger.rest.dto.SakKodeverdi
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
@Transactional
class SakService(
    private val sakRepository: SakRepository,
    private val appUtil: ApplicationUtil
) {

    private val LOG = LoggerFactory.getLogger(javaClass)

    fun findSakerByFnr(fnr: FoedselsNr, fom: LocalDate, tom: LocalDate?): List<SakDto> {
        val saker = sakRepository.findSakerByFnrAndValg(fnr, getValg())
            .filter { it.innenforPeriode(fom, tom) }

        LOG.info("Hentet {} saker.", saker.size)

        return toDto(saker)
    }

    fun findRestanse(): List<RestanseDto> {
        val restanse = sakRepository.findRestanse(setOf(SakResultat.SB, SakResultat.ÅPEN))

        LOG.info("Hentet {} saker.", restanse.size)

        return restanse.map {
            RestanseDto(
                    fnr = it.fnr.asString,
                    valg = it.valg.kode,
                    type = it.type.kode,
                    registrert = it.registrert,
                    mottatt = it.mottatt,
                    reellEnhet = it.reellEnhet
            )
        }
    }

    private fun toDto(saker: List<Sak>): List<SakDto> {
        return saker.map {
            SakDto(
                sakId = SakId(blokk = it.saksblokk, nr = it.saksnummer.toInt()),
                resultat = SakKodeverdi(it.resultat.kode, it.resultat.tekst),
                vedtatt = it.vedtaksdato,
                iverksatt = it.iverksattdato,
                type = SakKodeverdi(it.type.kode, it.type.tekst),
                valg = SakKodeverdi(it.valg.kode, it.valg.tekst),
                undervalg = SakKodeverdi(it.undervalg.kode, it.undervalg.tekst),
                nivaa = SakKodeverdi(it.nivaa.kode, it.nivaa.tekst),
                registrert = it.registrert,
                mottatt = it.mottatt
            )
        }
    }

    private fun getValg(): Set<SakValg> {
        return when(appUtil.getApplication()) {
            ApplicationUtil.Application.INFOTRYGD_FORELDREPENGER -> SakRepository.valgFp
            ApplicationUtil.Application.INFOTRYGD_SVANGERSKAPSPENGER -> SakRepository.valgSvp
            ApplicationUtil.Application.INFOTRYGD_SYKEPENGER -> SakRepository.valgSp
        }
    }
}