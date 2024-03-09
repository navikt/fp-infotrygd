package no.nav.infotrygd.foreldrepenger.model

import jakarta.persistence.*
import no.nav.commons.foedselsnummer.FoedselsNr
import no.nav.infotrygd.foreldrepenger.model.converters.NavReversedLocalDateConverter
import no.nav.infotrygd.foreldrepenger.model.converters.ReversedFoedselNrConverter
import no.nav.infotrygd.foreldrepenger.model.kodeverk.*
import java.io.Serializable
import java.math.BigInteger
import java.time.LocalDate

@Entity
@Table(name = "SA_SAK_10")
data class Sak(
    @Id
    @Column(name = "ID_SAK", columnDefinition = "DECIMAL", nullable = false)
    var id: BigInteger,

    @Column(name = "F_NR", columnDefinition = "CHAR")
    @Convert(converter = ReversedFoedselNrConverter::class)
    val fnr: FoedselsNr,

    @Column(name = "S05_SAKSBLOKK", columnDefinition = "CHAR")
    val saksblokk: String,

    @Column(name = "S10_SAKSNR", columnDefinition = "CHAR")
    val saksnummer: String,

    @Column(name = "S10_KAPITTELNR", columnDefinition = "CHAR")
    val kapittelNr: String,

    @Column(name = "S10_VALG", columnDefinition = "CHAR")
    val valg: SakValg,

    @Column(name = "S10_UNDERVALG", columnDefinition = "CHAR")
    val undervalg: SakUndervalg,

    @Column(name = "S10_TYPE", columnDefinition = "CHAR")
    val type: SakType,

    @Column(name = "S10_RESULTAT", columnDefinition = "CHAR")
    val resultat: SakResultat,

    @Column(name = "S10_NIVAA", columnDefinition = "CHAR")
    val nivaa: SakNivaa,

    /** INFO: NavReversedLocalDateConverter lar seg ikke sortere i databasen! */
    @Column(name = "S10_VEDTAKSDATO", columnDefinition = "DECIMAL")
    @Convert(converter = NavReversedLocalDateConverter::class)
    val vedtaksdato: LocalDate?,

    /** INFO: NavReversedLocalDateConverter lar seg ikke sortere i databasen! */
    @Column(name = "S10_IVERKSATTDATO", columnDefinition = "DECIMAL")
    @Convert(converter = NavReversedLocalDateConverter::class)
    val iverksattdato: LocalDate?,

    @Column(name = "S10_REG_DATO", columnDefinition = "DECIMAL")
    @Convert(converter = NavReversedLocalDateConverter::class)
    val registrert: LocalDate?,

    @Column(name = "S10_MOTTATTDATO", columnDefinition = "DECIMAL")
    @Convert(converter = NavReversedLocalDateConverter::class)
    val mottatt: LocalDate?
) : Serializable {
    fun innenforPeriode(fom: LocalDate, tom: LocalDate?): Boolean {
        if(tom != null) {
            require(fom == tom || fom.isBefore(tom)) { "Tom-dato kan ikke være før fom-dato." }
        }

        if(tom != null && tom.isBefore(registrert)) {
            return false
        }

        if(fom.isAfter(registrert)) {
            return false
        }

        return true
    }
}
