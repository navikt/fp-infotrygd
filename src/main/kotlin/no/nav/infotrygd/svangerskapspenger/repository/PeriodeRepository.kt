package no.nav.infotrygd.svangerskapspenger.repository

import no.nav.commons.foedselsnummer.FoedselsNr
import no.nav.infotrygd.svangerskapspenger.model.Periode
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
    fun findAvsluttedeSakerByFnr(fnr: FoedselsNr, fom: LocalDate): List<Periode>

    @Query("""
        SELECT COUNT(p) FROM Periode p
         WHERE p.stoenadstype = 'SV'
           AND p.frisk = 'F'
           AND p.arbufoer >= :fom
    """)
    fun countAvsluttedeSaker(fom: LocalDate): Long

    @Query("""
        SELECT p FROM Periode p
         WHERE p.fnr = :fnr
           AND p.stoenadstype = 'SV'
           AND p.frisk = ' '
    """)
    fun findOpneSakerMedLopendeUtbetalingByFnr(fnr: FoedselsNr): List<Periode>

    @Query("""
        SELECT COUNT(p) FROM Periode p
         WHERE p.stoenadstype = 'SV'
           AND p.frisk = ' '
    """)
    fun countOpneSakerMedLopendeUtbetaling(): Long

    @Query("""
        SELECT p FROM Periode p
         WHERE p.stoenadstype = 'SV'
           AND p.frisk = ' '
    """)
    fun findOpneSakerMedLopendeUtbetaling(): List<Periode>
}