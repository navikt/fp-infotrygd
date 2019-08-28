package no.nav.infotrygd.svangerskapspenger.repository

import no.nav.infotrygd.svangerskapspenger.model.Periode
import no.nav.infotrygd.svangerskapspenger.values.FodselNr
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
interface PeriodeRepository : JpaRepository<Periode, Long> {

    @Query("""
        SELECT p FROM Periode p
         WHERE p.fnr = :fnr
           AND p.stoenadstype = 'SV'
           AND p.frisk = 'F'
           AND p.arbufoer >= :fom
    """)
    fun findAvsluttedeSakerByFnr(fnr: FodselNr, fom: LocalDate): List<Periode>

    @Query("""
        SELECT p FROM Periode p
         WHERE p.fnr = :fnr
           AND p.stoenadstype = 'SV'
           AND p.frisk = ' '
    """)
    fun findOpneSakerMedLopendeUtbetaling(fnr: FodselNr): List<Periode>
}