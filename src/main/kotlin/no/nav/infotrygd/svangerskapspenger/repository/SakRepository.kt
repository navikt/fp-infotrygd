package no.nav.infotrygd.svangerskapspenger.repository

import no.nav.infotrygd.svangerskapspenger.model.Sak
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
              AND s.type IN :typer""")
    fun findSvangerskapssakerByFnrAndType(fnr: String, typer: Set<String>): List<Sak>
}