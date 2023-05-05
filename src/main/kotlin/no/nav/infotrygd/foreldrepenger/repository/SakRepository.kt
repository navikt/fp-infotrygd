package no.nav.infotrygd.foreldrepenger.repository

import no.nav.commons.foedselsnummer.FoedselsNr
import no.nav.infotrygd.foreldrepenger.model.Sak
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface SakRepository : JpaRepository<Sak, Long> {

    companion object {
        val valgFp: Set<String> = setOf("AE", "AP", "FE", "FP", "FU", "FØ")
        val valgSvp: Set<String> = setOf("SV")
    }
    @Query("""
        SELECT s FROM Sak s 
            WHERE s.fnr = :fnr 
              AND s.kapittelNr = 'FA'
              AND s.valg in :valg
              AND s.type IN ('S', 'R', 'K', 'A')""")
    fun findSakerByFnr(fnr: FoedselsNr, valg: Set<String>): List<Sak>
}