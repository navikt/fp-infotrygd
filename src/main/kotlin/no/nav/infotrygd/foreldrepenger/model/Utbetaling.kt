package no.nav.infotrygd.foreldrepenger.model

import no.nav.infotrygd.foreldrepenger.model.converters.NavLocalDateConverter
import no.nav.infotrygd.foreldrepenger.model.converters.UtbetalingsgradConverter
import java.time.LocalDate
import jakarta.persistence.*
import java.math.BigInteger

@Entity
@Table(name = "IS_UTBETALING_15")
data class Utbetaling(
    @Id
    @Column(name = "ID_UTBT", columnDefinition = "DECIMAL")
    val id: BigInteger,

    @Column(name = "REGION", columnDefinition = "CHAR")
    val region: String,

    @Column(name = "IS01_PERSONKEY", columnDefinition = "DECIMAL")
    val personKey: BigInteger,

    @Column(name = "IS10_ARBUFOER_SEQ", columnDefinition = "DECIMAL")
    val arbufoerSeq: BigInteger,

    @Column(name = "IS15_UTBETFOM", columnDefinition = "DECIMAL")
    @Convert(converter = NavLocalDateConverter::class)
    val utbetaltFom: LocalDate,

    @Column(name = "IS15_UTBETTOM", columnDefinition = "DECIMAL")
    @Convert(converter = NavLocalDateConverter::class)
    val utbetaltTom: LocalDate,

    @Column(name = "IS15_UTBETDATO", columnDefinition = "DECIMAL")
    @Convert(converter = NavLocalDateConverter::class)
    val utbetalingsdato: LocalDate?,

    @Column(name = "IS15_GRAD", columnDefinition = "CHAR")
    @Convert(converter = UtbetalingsgradConverter::class)
    val grad: Int?,

    @Column(name = "IS15_TYPE", columnDefinition = "CHAR")
    val type: String?,

    @Column(name = "IS15_KORR", columnDefinition = "CHAR")
    val korr: String?
)