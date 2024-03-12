package no.nav.infotrygd.foreldrepenger.repository

import no.nav.commons.foedselsnummer.FoedselsNr
import no.nav.infotrygd.foreldrepenger.model.Periode
import no.nav.infotrygd.foreldrepenger.model.kodeverk.Stoenadstype
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface PeriodeRepository : JpaRepository<Periode, Long> {
    companion object {
        val stønadstypeFp: Set<Stoenadstype> = setOf(Stoenadstype.ADOPSJON, Stoenadstype.FOEDSEL)
        val stønadstypeSvp: Set<Stoenadstype> = setOf(Stoenadstype.SVANGERSKAP, Stoenadstype.RISIKOFYLT_ARBMILJOE)
        val stønadstypeSp: Set<Stoenadstype> = setOf(Stoenadstype.SYKEPENGER)
    }


    @Query("""
        SELECT p FROM Periode p
         WHERE p.fnr = :fnr
           AND p.stoenadstype IN :stoenadstype
           AND p.frisk != 'H'
           AND p.arbufoer is not null
    """)
    fun findByFnrAndStoenadstype(fnr: FoedselsNr, stoenadstype: Set<Stoenadstype>): List<Periode>
}