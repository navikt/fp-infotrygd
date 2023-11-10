package no.nav.infotrygd.foreldrepenger.model

import jakarta.persistence.*
import no.nav.commons.foedselsnummer.FoedselsNr
import no.nav.commons.foedselsnummer.Kjoenn
import no.nav.infotrygd.foreldrepenger.model.converters.*
import no.nav.infotrygd.foreldrepenger.model.kodeverk.Arbeidskategori
import no.nav.infotrygd.foreldrepenger.model.kodeverk.Frisk
import no.nav.infotrygd.foreldrepenger.model.kodeverk.Stoenadstype
import no.nav.infotrygd.foreldrepenger.model.kodeverk.Tema
import no.nav.infotrygd.foreldrepenger.utils.reversert
import org.hibernate.annotations.Cascade
import org.hibernate.annotations.CascadeType
import java.io.Serializable
import java.math.BigInteger
import java.time.LocalDate

@Entity
@Table(name = "IS_PERIODE_10")
data class Periode(
    @Id
    @Column(name = "ID_PERI10", nullable = false, columnDefinition = "DECIMAL")
    val id: BigInteger,

    @Column(name = "REGION", columnDefinition = "CHAR")
    val region: String,

    @Column(name = "IS01_PERSONKEY", columnDefinition = "DECIMAL")
    val personKey: BigInteger,

    @Column(name = "IS10_ARBUFOER_SEQ", columnDefinition = "DECIMAL")
    val arbufoerSeq: BigInteger,

    @Column(name = "IS10_STOENADS_TYPE", columnDefinition = "CHAR")
    val stoenadstype: Stoenadstype?,

    @Column(name = "F_NR", columnDefinition = "CHAR")
    @Convert(converter = ReversedFoedselNrConverter::class)
    val fnr: FoedselsNr,

    @Column(name = "IS10_FRISK", columnDefinition = "CHAR")
    @Convert(converter = FriskConverter::class)
    val frisk: Frisk,

    @Column(name = "IS10_UTBET_FOM", columnDefinition = "DECIMAL")
    @Convert(converter = NavLocalDateConverter::class)
    val utbetaltFom: LocalDate?,

    @Column(name = "IS10_UTBET_TOM", columnDefinition = "DECIMAL")
    @Convert(converter = NavLocalDateConverter::class)
    val utbetaltTom: LocalDate?,

    @Column(name = "IS10_ARBUFOER", columnDefinition = "DECIMAL")
    @Convert(converter = NavLocalDateConverter::class)
    val arbufoer: LocalDate,

    @Column(name = "IS10_ARBUFOER_TOM", columnDefinition = "DECIMAL")
    @Convert(converter = NavLocalDateConverter::class)
    val arbufoerTom: LocalDate?,

    @Column(name = "IS10_ARBUFOER_OPPR", columnDefinition = "DECIMAL")
    @Convert(converter = NavLocalDateConverter::class)
    val arbufoerOpprinnelig: LocalDate,

    @Column(name = "IS10_DEKNINGSGRAD", columnDefinition = "DECIMAL")
    val dekningsgrad: Int?,

    @Column(name = "IS10_FDATO", columnDefinition = "DECIMAL")
    @Convert(converter = NavLocalDateConverter::class)
    val foedselsdatoBarn: LocalDate?,

    @Column(name = "IS10_TIDSK_BARNFNR", columnDefinition = "CHAR")
    @Convert(converter = ReversedFoedselNrConverter::class)
    val barnFnr: FoedselsNr?,

    @Column(name = "IS10_MORFNR", columnDefinition = "DECIMAL")
    @Convert(converter = ReversedLongFoedselNrConverter::class)
    val morFnr: FoedselsNr?,

    @Column(name = "IS10_STEBARNSADOPSJON", columnDefinition = "CHAR")
    val stebarnsadopsjon: String?,

    @Column(name = "IS10_ARBKAT", columnDefinition = "CHAR")
    val arbeidskategori: Arbeidskategori?,

    @Column(name = "IS10_BRUKERID", columnDefinition = "CHAR")
    @Convert(converter = BrukerIdConverter::class)
    val brukerId: String?,

    @Column(name = "IS10_FRISKM_DATO", columnDefinition = "DECIMAL")
    @Convert(converter = NavLocalDateConverter::class)
    val friskmeldtDato: LocalDate?,

    @Column(name = "IS10_MAX", columnDefinition = "DECIMAL")
    @Convert(converter = NavLocalDateConverter::class)
    val maksdato: LocalDate?,

    @Column(name = "IS10_STOPPDATO", columnDefinition = "DECIMAL")
    @Convert(converter = NavLocalDateConverter::class)
    val stoppdato: LocalDate?,

    @Column(name = "IS10_REG_DATO", columnDefinition = "CHAR")
    @Convert(converter = NavCharDateConverter::class)
    val registrert: LocalDate?,

    @Column(name = "TK_NR", columnDefinition = "CHAR")
    val tkNr: String,

    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumns(value = [
        JoinColumn(name = "REGION", referencedColumnName = "REGION"),
        JoinColumn(name = "IS01_PERSONKEY", referencedColumnName = "IS01_PERSONKEY"),
        JoinColumn(name = "IS10_ARBUFOER_SEQ", referencedColumnName = "IS10_ARBUFOER_SEQ")
    ])
    @Cascade(value = [CascadeType.ALL])
    val utbetalingshistorikk: List<Utbetaling>,

    @OneToMany
    @JoinColumns(value = [
        JoinColumn(name = "REGION", referencedColumnName = "REGION"),
        JoinColumn(name = "IS01_PERSONKEY", referencedColumnName = "IS01_PERSONKEY"),
        JoinColumn(name = "IS10_ARBUFOER_SEQ", referencedColumnName = "IS10_ARBUFOER_SEQ")
    ])
    @Cascade(value = [CascadeType.ALL])
    val inntekter: List<Inntekt>
) : Serializable {
    val utbetalinger: List<Utbetaling>
        get() {
            // Fra https://confluence.adeo.no/display/INFOTRYGD/Tjeneste+finnGrunnlag+-+Informasjonsmodell
            // IS15-perioder som er tilbakeført eller korrigert skal ikke tas med i uttrekk, dvs. IS15-TYPE = '7' eller IS15-KORR not = space.
            // Utbetalinger som har utbetalingsdago = null er ikke reelle utbetalinger.
            return utbetalingshistorikk.filter { it.type != "7" && it.korr.isNullOrBlank() && it.utbetalingsdato != null }
        }

    val tema: Tema
        get() {
            val tema: Tema? = stoenadstype?.tema

            if(tema != null) {
                return tema
            }

            return Tema.UKJENT
        }

    val opphoerFom: LocalDate?
        get() = stoppdato
            ?: friskmeldtDato
            ?: arbufoerTom?.plusDays(1)
            ?: maksdato?.plusDays(1)

    fun innenforPeriode(fom: LocalDate, tom: LocalDate?): Boolean {
        if(tom != null) {
            require(fom == tom || fom.isBefore(tom)) { "Tom-dato kan ikke være før fom-dato." }
        }

        if(tom != null && tom.isBefore(arbufoer)) {
            return false
        }

        if(opphoerFom != null && fom.isAfter(opphoerFom)) {
            return false
        }

        return true
    }

    val barnPersonKey: Long?
        get() {
            return barnFnr?.let { "$tkNr${it.reversert}".toLong() }
        }

    val barnKode: String
        get() {
            val sammeKjoennMor = stebarnsadopsjon in setOf("A", "D")
            if (sammeKjoennMor) {
                return "1"
            }

            val sammeKjoennFar = stebarnsadopsjon in setOf("B", "C", "E")
            if (sammeKjoennFar) {
                return "2"
            }

            return when (fnr.kjoenn) {
                Kjoenn.KVINNE -> "1"
                Kjoenn.MANN -> "2"
            }
        }

}