package no.nav.infotrygd.foreldrepenger.repository

import no.nav.infotrygd.foreldrepenger.model.VedtakBarn
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface VedtakBarnRepository : JpaRepository<VedtakBarn, Long> {
    fun findByPersonKeyAndArbufoerSeqAndKodeAndRegion(personKey: Long, arbufoerSeq: String, kode: String, region: String): VedtakBarn?
}