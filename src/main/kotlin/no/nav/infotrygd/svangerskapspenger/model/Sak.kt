package no.nav.infotrygd.svangerskapspenger.model

import java.time.LocalDate
import javax.persistence.*

@Entity
@Table(name = "SA_SAK_10")
data class Sak(
    @Id
    @Column(name = "ID_SAK", columnDefinition = "DECIMAL", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(name = "F_NR", columnDefinition = "CHAR")
    val fnr: String,

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

    @Column(name = "S10_VEDTAKSDATO", columnDefinition = "DECIMAL")
    @Convert(converter = NavLocalDateConverter::class)
    val vedtaksdato: LocalDate,

    @Column(name = "S10_IVERKSATTDATO", columnDefinition = "DECIMAL")
    @Convert(converter = NavLocalDateConverter::class)
    val iverksattdato: LocalDate

    // todo: mangler i schema.sql / ligger i IS10 !!!
    // @Column(name = "IS10_ARBUFOER")
    //  val arbeidsufoer: LocalDate,

    // todo: mangler i schema.sql / ligger i IS10 !!!
    // @Column(name = "IS10_STOPPDATO")
    // val stoppdato: LocalDate
)

// todo: S15_STATUS