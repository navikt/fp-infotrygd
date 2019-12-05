package no.nav.infotrygd.foreldrepenger.repository

import no.nav.commons.foedselsnummer.FoedselsNr
import no.nav.infotrygd.foreldrepenger.model.Sak
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface SakRepository : JpaRepository<Sak, Long> {
    @Query("""
        SELECT s FROM Sak s 
            WHERE s.fnr = :fnr 
              AND s.kapittelNr = 'FA'
              AND s.valg in ('AE', 'AP', 'FE', 'FP', 'FU', 'FØ')
              AND s.type IN ('S', 'R', 'K', 'A')""")
    fun findSakerByFnr(fnr: FoedselsNr): List<Sak>

    @Query("""
        SELECT COUNT(sak) FROM Sak sak
         INNER JOIN Status status
               ON (
                    sak.saksblokk = status.saksblokk
                    and sak.saksnummer = status.saksnummer
                    and sak.personKey = status.personKey
                )
         WHERE status.lopeNr in (
                 SELECT MIN (s.lopeNr) FROM Status s
                       WHERE s.saksblokk = status.saksblokk
                         and s.saksnummer = status.saksnummer
                         and s.personKey = status.personKey
                 )
           AND sak.kapittelNr = 'FA'
           AND sak.valg in ('AE', 'AP', 'FE', 'FP', 'FU', 'FØ')
           AND sak.type IN :typer
           AND status.status not in ('FB', '  ')
           AND sak.resultat in ('SB', '  ')
    """)
    fun countAapneSakerByType(typer: Set<String>): Long
}