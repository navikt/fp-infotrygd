package no.nav.infotrygd.svangerskapspenger.repository

import no.nav.infotrygd.svangerskapspenger.model.Sak
import no.nav.infotrygd.svangerskapspenger.values.FodselNr
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface SakRepository : JpaRepository<Sak, Long> {
    @Query("""
        SELECT s FROM Sak s 
            WHERE s.fnr = :fnr 
              AND s.kapittelNr = 'FA' 
              AND s.valg = 'SV'
              AND s.type IN ('S', 'R', 'K', 'A')""")
    fun findSvangerskapssakerByFnr(fnr: FodselNr): List<Sak>

    @Query("""
        SELECT COUNT(s) FROM Sak s
            WHERE s.kapittelNr = 'FA'
              AND s.valg = 'SV'
              AND s.type IN :typer""")
    fun countSvangerskapssakerByType(typer: Set<String>): Long
}