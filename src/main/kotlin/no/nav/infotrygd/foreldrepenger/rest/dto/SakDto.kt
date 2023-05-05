package no.nav.infotrygd.foreldrepenger.rest.dto

import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDate

data class SakResult(
    @get:Schema(description = "Relevant tillegsinformasjon om resultatet.")
    val info: String?,

    val saker: List<SakDto>,

    @get:Schema(description = "Åpne saker med løpende utbetaling. Disse hentes fra Foreldrepenge-base, ytelse Svangerskapspenger.")
    val apneSakerMedLopendeUtbetaling: List<ApenSakMedLopendeUtbetaling>,

    @get:Schema(description = "Avsluttede saker. Disse hentes fra Foreldrepenge-base, ytelse Svangerskapspenger.")
    val avsluttedeSaker: AvsluttedeSaker, // IS10_STONADS_TYPE='SV', IS10_FRISK='F', IS10_ARBUFOER >= i dag minus 1 år

    @get:Schema(description = "Saker som ikke har startet enda.")
    val ikkeStartet: List<IkkeStartet>
)

// Saker fra Saksregister, ytelse Svangerskapspenger

data class SakDto(
    val sakId: SakId?,

    @get:Schema(description = """
        Type sak.
        
        S: Søknad
        R: Revurdering
        K: Klage
        A: Anke
        
        Kolonne: S10_TYPE
    """,
        allowableValues = ["S", "R", "K", "A"])

    val type: String,

    @get:Schema(description = """
        IP: - Saksbehandlingen kan starte med Statuskode IP (Ikke påbegynt). Da er det kun registrert en sakslinje uten at vedtaksbehandling er startet.
        UB: - Saksbehandling startet - når sak med status UB - Under Behandling - lagres, rapporteres hendelsen BehandlingOpprettet
        SG: - Saksbehandler 1 har fullført og sendt til saksbehandler 2 for godkjenning
        UK: - Underkjent av saksbehandler 2 med retur til saksbehandler 1
        FB: - FerdigBehandlet
        FI: - ferdig iverksatt
        RF: - returnert feilsendt
        RM: - returnert midlertidig
        RT: - returnert til
        ST: - sendt til
        VD: - videresendt Direktoratet
        VI: - venter på iverksetting
        VT: - videresendt Trygderetten
        
        Kolonne: S15_STATUS.
    """,
        allowableValues = ["IP", "UB", "SG", "UK", "FB", "FI", "RF", "RM", "RT", "ST", "VD", "VI", "VT"]
    )
    val status: String,         // S15_STATUS

    @get:Schema(description = """
        Resultatkode for saken.
        
        Dette er NOEN av kodene:
        
        ?: beslutningsstøtte Besl st
        A: Avslag
        AK: avvist klage
        AV: advarsel
        DI: delvis innvilget
        DT: delvis tilbakebetale
        FB: ferdigbehandlet
        FI: fortsatt innvilget
        H: henlagt / trukket tilbake
        HB: henlagt / bortfalt
        I: Innvilget
        IN: innvilget ny situasjon
        IS: ikke straffbart
        IT: ikke tilbakebetale
        MO: midlertidig opphørt
        MT: mottatt
        O: opphørt
        PA: politianmeldelse
        R: redusert
        SB: sak i bero
        TB: tilbakebetale
        TH: tips henlagt
        TO: tips oppfølging
        Ø: økning
        
        Kolonne: S10_RESULTAT
    """)
    val resultat: String?,       // S10_RESULTAT

    @get:Schema(description = """
        Vedtaksdato.
        Kolonne: S10_VEDTAKSDATO
    """,
        example = "2019-01-01")
    val vedtatt: LocalDate?,     // S10_VEDTAKSDATO

    @get:Schema(description = """
        Iverksettelsesdato.
        Kolonne: S10_IVERKSATTDATO
    """,
        example = "2019-01-01")
    val iverksatt: LocalDate?,    // S10_IVERKSATTDATO

    @get:Schema(description = """
        Registreringsdato for sak.
        Kolonne: S10_REG_DATO
    """,
        example = "2019-01-01")
    val registrert: LocalDate?
)

data class SakId(
    @get:Schema(description = "Kolonne: S05_SAKSBLOKK")
    val blokk: String,  // S05_SAKSBLOKK

    @get:Schema(description = "Kolonne: S10_SAKSNR")
    val nr: Int         // S10_SAKSNR
)


// Saker fra Foreldrepenge-base, ytelse Svangerskapspenger

data class ApenSakMedLopendeUtbetaling(
    @get:Schema(description = """
        Iverksettelsesdato.
        Kolonne: IS10_ARBUFOER
    """,
        example = "2019-01-01")
    val iverksatt: LocalDate, // IS10_ARBUFOER

    @get:Schema(description = """
        Registreringsdato for sak.
        Kolonne: IS10_REG_DATO
    """,
        example = "2019-01-01")
    val registrert: LocalDate?,

    val utbetalinger: List<UtbetalingDto>
)

data class AvsluttedeSaker(
    @get:Schema(description = """
        Listen 'saker' viser resultater fra og med denne datoen (tidsbegrenset søk).
    """,
        example = "2019-01-01")
    val fraOgMed: LocalDate,

    val saker: List<AvsluttetSak>
)

data class AvsluttetSak(
    @get:Schema(description = """
        Iverksettelsesdato.
        Kolonne: IS10_ARBUFOER
    """,
        example = "2019-01-01")
    val iverksatt: LocalDate,   // IS10_ARBUFOER

    @get:Schema(description = """
        Stoppdato.
        
        Dette feltet ble innført i 2016-HL4.
        
        Kolonne: IS10_STOPPDATO
    """,
        example = "2019-01-01")
    val stoppdato: LocalDate?,    // IS10_STOPPDATO

    @get:Schema(description = """
        Registreringsdato for sak.
        Kolonne: IS10_REG_DATO
    """,
        example = "2019-01-01")
    val registrert: LocalDate?,

    val utbetalinger: List<UtbetalingDto>
)

data class UtbetalingDto(
    @get:Schema(description = """
        Utbetalt fra og med.
        Kolonne: IS15_UTBETFOM
    """,
        example = "2019-01-01")
    val utbetaltFom: LocalDate,

    @get:Schema(description = """
        Utbetalt til og med.
        Kolonne: IS15_UTBETTOM
    """,
        example = "2019-01-01")
    val utbetaltTom: LocalDate,

    @get:Schema(description = """
        Gradering
        Kolonne: IS15_GRAD
    """)
    val gradering: Int
)

data class IkkeStartet(
    @get:Schema(description = """
        Iverksettelsesdato.
        Kolonne: IS10_ARBUFOER
    """)
    val iverksatt: LocalDate?,

    @get:Schema(description = """
        Registreringsdato for sak.
        Kolonne: IS10_REG_DATO
    """)
    val registrert: LocalDate?
)
