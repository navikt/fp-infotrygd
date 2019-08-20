package no.nav.infotrygd.svangerskapspenger.model

import no.nav.infotrygd.svangerskapspenger.model.converters.NavLocalDateConverter
import java.time.LocalDate
import javax.persistence.*

@Entity
@Table(name = "IS_PERIODE_10")
data class Periode(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_PERI10", nullable = false)
    val id: Long? = null,

    @Column(name = "F_NR", columnDefinition = "CHAR")
    val fnr: String,

    @Column(name = "IS10_STOENADS_TYPE", columnDefinition = "CHAR")
    val stoenadstype: String?,

    @Column(name = "IS10_FRISK", columnDefinition = "CHAR")
    val frisk: String?, // todo: er '' mappet til null?

    @Column(name = "IS10_ARBUFOER", columnDefinition = "DECIMAL")
    @Convert(converter = NavLocalDateConverter::class)
    val arbufoer: LocalDate,

    @Column(name = "IS10_STOPPDATO", columnDefinition = "DECIMAL")
    @Convert(converter = NavLocalDateConverter::class)
    val stoppdato: LocalDate?
)