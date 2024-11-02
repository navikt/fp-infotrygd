package no.nav.infotrygd.foreldrepenger.repository

import no.nav.commons.foedselsnummer.FoedselsNr
import no.nav.infotrygd.foreldrepenger.model.Sak
import no.nav.infotrygd.foreldrepenger.model.kodeverk.SakResultat
import no.nav.infotrygd.foreldrepenger.model.kodeverk.SakType
import no.nav.infotrygd.foreldrepenger.model.kodeverk.SakValg
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface SakRepository : JpaRepository<Sak, Long> {

    companion object {
        val valgFp: Set<SakValg> = setOf(SakValg.ES_A, SakValg.ES_F, SakValg.FP, SakValg.FP_A, SakValg.FP_F, SakValg.FP_FU)
        val valgSvp: Set<SakValg> = setOf(SakValg.SVP)
        val valgSp: Set<SakValg> = setOf()
    }
    @Query("""
        SELECT s FROM Sak s 
            WHERE s.fnr = :fnr 
              AND s.kapittelNr = 'FA'
              AND s.valg in :valg""")
    fun findSakerByFnrAndValg(fnr: FoedselsNr, valg: Set<SakValg>): List<Sak>

    @Query("""
        SELECT s FROM Sak s 
            WHERE s.kapittelNr = 'FA'
              AND (s.resultat in :resultat or (s.type in :klager and s.vedtaksdato = 0))""")
    fun findRestanse(resultat: Set<SakResultat>, klager: Set<SakType>): List<Sak>


}