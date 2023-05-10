package no.nav.infotrygd.foreldrepenger.model

import jakarta.persistence.*
import no.nav.infotrygd.foreldrepenger.model.converters.RefusjonJaNeiConverter
import no.nav.infotrygd.foreldrepenger.model.kodeverk.Inntektsperiode
import java.math.BigDecimal
import java.math.BigInteger

@Entity
@Table(name = "IS_INNTEKT_13")
data class Inntekt(
    @Id
    @Column(name = "ID_INNT", columnDefinition = "DECIMAL")
    val id: BigInteger,

    @Column(name = "REGION", columnDefinition = "CHAR")
    val region: String,

    @Column(name = "IS01_PERSONKEY", columnDefinition = "DECIMAL")
    val personKey: BigInteger,

    @Column(name = "IS10_ARBUFOER_SEQ", columnDefinition = "DECIMAL")
    val arbufoerSeq: BigInteger,

    @Column(name = "IS13_ARBGIVNR", columnDefinition = "DECIMAL")
    val arbgiverNr: BigInteger,

    @Column(name = "IS13_LOENN", columnDefinition = "DECIMAL")
    val loenn: BigDecimal,

    @Column(name = "IS13_PERIODE", columnDefinition = "CHAR")
    val periode: Inntektsperiode,

    @Column(name = "IS13_REF", columnDefinition = "CHAR")
    @Convert(converter = RefusjonJaNeiConverter::class)
    val refusjon: Boolean
)