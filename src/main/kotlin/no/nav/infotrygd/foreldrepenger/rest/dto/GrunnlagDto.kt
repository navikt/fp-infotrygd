package no.nav.infotrygd.foreldrepenger.rest.dto

import io.swagger.v3.oas.annotations.media.Schema
import java.math.BigDecimal
import java.math.BigInteger
import java.time.LocalDate

interface Grunnlag {
    @get:Schema(description = """
        Tema:
        SP  Sykepenger
        FA  Foreldrepenger
        BS  Barns sykdom
    """)
    val tema: Kodeverdi?
    val registrert: LocalDate?

    @get:Schema(description = """
        L   Løpende
        A   Avsluttet
        I   Ikke startet
    """)
    val status: Kodeverdi?
    val saksbehandlerId: String?
    val iverksatt: LocalDate?
    val opphoerFom: LocalDate?

    @get:Schema(description = """
        Behandlingstema:
        - Sykepenger
        SP  Sykepenger
        
        - Foreldrepenger
        FØ  Foreldrepenger m/ fødsel
        AP  Foreldrepenger m/ adopsjon
        SV  Svangerskapspenger
        
        - Pårørendesykdom
        OM  Omsorgspenger
        PB  Pleiepenger sykt barn (identdato før 1.10.2017)
        OP  Opplæringspenger
        PP  Pleiepenger pårørende
        PI  Pleiepenger (identdato før 1.10.2017)
        PN  Pleiepenger, ny ordning (identdato etter 1.10.2017)
    """)
    val behandlingstema: Kodeverdi
    val identdato: LocalDate
    val periode: Periode?

    @get:Schema(description = """
        Arbeidskategori:
        00   Fisker
        01   Arbeidstaker
        02   Selvstendig næringsdrivende
        03   Kombinert arb.taker/selvs
        04   Sjømann
        05   Jordbruker
        06   Arbeidsledig
        07   Inaktiv
        08   Militær (befal)
        09   Vernepliktige
        10   Arbtaker m/sjøm.
        11   Inaktive m/sjøm.
        12   Svalbardarbeidere
        13   Kombinert arb.taker/jordbruker
        14   Yrkesskade
        15   Ambassadepersonell
        16   Utøvere av reindrift
        17   Fisker/arbeidstaker
        18   IKKE-I-BRUK
        19   Frilanser m/forsikring for tilleggssykepenger
        20   Kombinert arb.taker/frilanser m/forsikring for tilleggssykepenger
        21   FFU-21
        22   FFU-22
        23   Arbtaker/A-løyse
        24   Frilanser uten forsikring
        25   Kombinert frilanser uten forsikring/arbeidstaker
        26   Selvstendig dagmamma/dagpappa
        27   Fisker m/hyre (brukes ved kun hyre)
        99   Inntektsopplysninger mangler
    """)
    val arbeidskategori: Kodeverdi?
    val arbeidsforhold: List<Arbeidsforhold>
    val vedtak: List<Vedtak>
}

data class Vedtak(
    val utbetalingsgrad: Int,
    val periode: Periode
)

data class Periode(
    val fom: LocalDate,
    val tom: LocalDate
)

data class Arbeidsforhold(
    val inntektForPerioden: BigDecimal?,
    @get:Schema(description = """
        Inntektsperiode:
        D   Daglig
        U   Ukentlig
        F   14-daglig
        M   Månedlig
        Å   Årlig
        X   Inntekt fastsatt etter 25% avvik
        Y   Premiegrunnlag oppdragstaker (gjelder de 2 første ukene)
    """)
    val inntektsperiode: Kodeverdi,
    val arbeidsgiverOrgnr: BigInteger,
    val refusjon: Boolean
)

data class Kodeverdi(val kode: String, val termnavn: String)

data class GrunnlagGenerelt(
    override val tema: Kodeverdi?,
    override val registrert: LocalDate?,
    override val status: Kodeverdi?,
    override val saksbehandlerId: String?,
    override val iverksatt: LocalDate?,
    override val opphoerFom: LocalDate?,
    override val behandlingstema: Kodeverdi,
    override val identdato: LocalDate,
    override val periode: Periode?,
    override val arbeidskategori: Kodeverdi?,
    override val arbeidsforhold: List<Arbeidsforhold>,
    override val vedtak: List<Vedtak>
) : Grunnlag


// ======================================== Ytelser ========================================


// --- Foreldrepenger ---

interface YtelseFelt {
    val opprinneligIdentdato: LocalDate?
    val dekningsgrad: Int?
    val gradering: BigDecimal?
    val foedselsdatoBarn: LocalDate?
}

data class YtelseDetaljer(
    override val opprinneligIdentdato: LocalDate?,
    override val dekningsgrad: Int?,
    override val gradering: BigDecimal?,
    override val foedselsdatoBarn: LocalDate?
) : YtelseFelt

data class YtelseGrunnlag(
    private val generelt: GrunnlagGenerelt,
    private val ytelseDetaljer: YtelseDetaljer
) : Grunnlag by generelt,
    YtelseFelt by ytelseDetaljer
