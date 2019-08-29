package no.nav.infotrygd.svangerskapspenger.model

import no.nav.infotrygd.svangerskapspenger.model.converters.NavReversedLocalDateConverter
import no.nav.infotrygd.svangerskapspenger.model.converters.ReversedFodselNrConverter
import no.nav.infotrygd.svangerskapspenger.values.FodselNr
import org.hibernate.annotations.Cascade
import org.hibernate.annotations.CascadeType
import java.io.Serializable
import java.time.LocalDate
import javax.persistence.*

@Entity
@Table(name = "SA_SAK_10")
data class Sak(
    @Id
    @Column(name = "ID_SAK", columnDefinition = "DECIMAL", nullable = false)
    var id: Long,

    @Column(name = "F_NR", columnDefinition = "CHAR")
    @Convert(converter = ReversedFodselNrConverter::class)
    val fnr: FodselNr,

    @Column(name = "S01_PERSONKEY", columnDefinition = "DECIMAL")
    val personKey: Long,

    @Column(name = "S05_SAKSBLOKK", columnDefinition = "CHAR")
    val saksblokk: String,

    @Column(name = "S10_SAKSNR", columnDefinition = "CHAR")
    val saksnummer: String,

    @Column(name = "S10_KAPITTELNR", columnDefinition = "CHAR")
    val kapittelNr: String,

    @Column(name = "S10_VALG", columnDefinition = "CHAR")
    val valg: String,

    @Column(name = "S10_TYPE", columnDefinition = "CHAR")
    val type: String,

    @Column(name = "S10_RESULTAT", columnDefinition = "CHAR")
    val resultat: String,

    /** INFO: NavReversedLocalDateConverter lar seg ikke sortere i databasen! */
    @Column(name = "S10_VEDTAKSDATO", columnDefinition = "DECIMAL")
    @Convert(converter = NavReversedLocalDateConverter::class)
    val vedtaksdato: LocalDate?,

    /** INFO: NavReversedLocalDateConverter lar seg ikke sortere i databasen! */
    @Column(name = "S10_IVERKSATTDATO", columnDefinition = "DECIMAL")
    @Convert(converter = NavReversedLocalDateConverter::class)
    val iverksattdato: LocalDate?,

    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumns(value = [
        JoinColumn(name = "S01_PERSONKEY", referencedColumnName = "S01_PERSONKEY"),
        JoinColumn(name = "S05_SAKSBLOKK", referencedColumnName = "S05_SAKSBLOKK"),
        JoinColumn(name = "S10_SAKSNR", referencedColumnName = "S10_SAKSNR")
    ])
    @Cascade(value = [CascadeType.ALL])
    val status: List<Status>
) : Serializable
