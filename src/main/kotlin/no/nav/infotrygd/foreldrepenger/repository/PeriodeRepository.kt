package no.nav.infotrygd.foreldrepenger.repository

import no.nav.commons.foedselsnummer.FoedselsNr
import no.nav.infotrygd.foreldrepenger.model.Periode
import no.nav.infotrygd.foreldrepenger.model.kodeverk.Stoenadstype
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
interface PeriodeRepository : JpaRepository<Periode, Long> {

    @Query("""
        SELECT p FROM Periode p
         WHERE p.fnr = :fnr
           AND p.stoenadstype IN ('AP', 'FP')
           AND p.frisk = 'F'
    """)
    fun findAvsluttedeSakerByFnr(fnr: FoedselsNr): List<Periode>

    @Query("""
        SELECT COUNT(p) FROM Periode p
         WHERE p.stoenadstype IN ('AP', 'FP')
           AND p.frisk = 'F'
           AND p.arbufoer >= :fom
    """)
    fun countAvsluttedeSaker(fom: LocalDate): Long

    @Query("""
        SELECT p FROM Periode p
         WHERE p.fnr = :fnr
           AND p.stoenadstype IN ('AP', 'FP')
           AND p.frisk = ' '
    """)
    fun findOpneSakerMedLopendeUtbetalingByFnr(fnr: FoedselsNr): List<Periode>

    @Query("""
        SELECT COUNT(p) FROM Periode p
         WHERE p.stoenadstype IN ('AP', 'FP')
           AND p.frisk = ' '
    """)
    fun countOpneSakerMedLopendeUtbetaling(): Long

    @Query("""
        SELECT p FROM Periode p
         WHERE p.stoenadstype IN ('AP', 'FP')
           AND p.frisk = ' '
    """)
    fun findOpneSakerMedLopendeUtbetaling(): List<Periode>

    @Query("""
        SELECT p FROM Periode p
         WHERE p.fnr = :fnr
           AND p.stoenadstype IN :stoenadstyper
    """)
    fun findByFnrAndStoenadstype(fnr: FoedselsNr, stoenadstyper: List<Stoenadstype>): List<Periode>
}