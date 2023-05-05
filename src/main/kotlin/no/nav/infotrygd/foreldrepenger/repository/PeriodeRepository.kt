package no.nav.infotrygd.foreldrepenger.repository

import no.nav.commons.foedselsnummer.FoedselsNr
import no.nav.infotrygd.foreldrepenger.model.Periode
import no.nav.infotrygd.foreldrepenger.model.kodeverk.Frisk
import no.nav.infotrygd.foreldrepenger.model.kodeverk.Stoenadstype
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface PeriodeRepository : JpaRepository<Periode, Long> {
    companion object {
        val lopende: Set<Frisk> = setOf(Frisk.LOPENDE)
        val avsluttede: Set<Frisk> = setOf(Frisk.FRISKMELDT, Frisk.TILBAKEKJOERT)
        val ikkeStartet: Set<Frisk> = setOf(Frisk.PASSIV, Frisk.AVVIST)
        val stoenadstypeFp: Set<String> = setOf("AP", "FP")
        val stoenadstypeSvp: Set<String> = setOf("SV")
    }

    @Query("""
        SELECT p FROM Periode p
         WHERE p.fnr = :fnr
           AND p.stoenadstype IN :stoenadstype
           AND p.frisk IN :frisk
           AND p.arbufoer != 0
    """)
    fun findByFnrAndFrisk(fnr: FoedselsNr, frisk: Set<Frisk>, stoenadstype: Set<String>): List<Periode>

    @Query("""
        SELECT p FROM Periode p
         WHERE p.fnr = :fnr
           AND p.stoenadstype IN :stoenadstyper
           AND p.frisk != 'H'
           AND p.arbufoer != 0
    """)
    fun findByFnrAndStoenadstype(fnr: FoedselsNr, stoenadstyper: List<Stoenadstype>): List<Periode>
}