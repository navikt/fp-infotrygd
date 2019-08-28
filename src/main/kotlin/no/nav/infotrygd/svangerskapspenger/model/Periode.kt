package no.nav.infotrygd.svangerskapspenger.model

import no.nav.infotrygd.svangerskapspenger.model.converters.NavLocalDateConverter
import no.nav.infotrygd.svangerskapspenger.model.converters.ReversedFodselNrConverter
import no.nav.infotrygd.svangerskapspenger.values.FodselNr
import java.time.LocalDate
import javax.persistence.*

@Entity
@Table(name = "IS_PERIODE_10")
data class Periode(
    @Id
    @Column(name = "ID_PERI10", nullable = false, columnDefinition = "DECIMAL")
    val id: Long,

    @Column(name = "F_NR", columnDefinition = "CHAR")
    @Convert(converter = ReversedFodselNrConverter::class)
    val fnr: FodselNr,

    @Column(name = "IS10_STOENADS_TYPE", columnDefinition = "CHAR")
    val stoenadstype: String?,

    @Column(name = "IS10_FRISK", columnDefinition = "CHAR")
    val frisk: String?,

    @Column(name = "IS10_ARBUFOER", columnDefinition = "DECIMAL")
    @Convert(converter = NavLocalDateConverter::class)
    val arbufoer: LocalDate,

    @Column(name = "IS10_STOPPDATO", columnDefinition = "DECIMAL")
    @Convert(converter = NavLocalDateConverter::class)
    val stoppdato: LocalDate?
)