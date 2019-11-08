package no.nav.infotrygd.svangerskapspenger.rest.dto

import io.swagger.annotations.ApiModelProperty
import java.time.LocalDate

data class SakResult(
    @ApiModelProperty(notes = "Relevant tillegsinformasjon om resultatet.")
    val info: String?,

    val saker: List<SakDto>,

    @ApiModelProperty(notes = "Åpne saker med løpende utbetaling. Disse hentes fra Foreldrepenge-base, ytelse Svangerskapspenger.")
    val apneSakerMedLopendeUtbetaling: List<ApenSakMedLopendeUtbetaling>,

    @ApiModelProperty(notes = "Avsluttede saker. Disse hentes fra Foreldrepenge-base, ytelse Svangerskapspenger.")
    val avsluttedeSaker: AvsluttedeSaker // IS10_STONADS_TYPE='SV', IS10_FRISK='F', IS10_ARBUFOER >= i dag minus 1 år
)

// Saker fra Saksregister, ytelse Svangerskapspenger

data class SakDto(
    val sakId: SakId?,

    @ApiModelProperty(notes = """
        Type sak.
        
        Kolonne: S10_TYPE
    """)
    val type: String,

    @ApiModelProperty(notes = """
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
        allowableValues = "IP,UB,SG,UK,FB,FI,RF,RM,RT,ST,VD,VI,VT"
    )
    val status: String,         // S15_STATUS

    @ApiModelProperty(notes = """
        Resultatkode for saken.
        Kolonne: S10_RESULTAT
    """)
    val resultat: String,       // S10_RESULTAT

    @ApiModelProperty(notes = """
        Vedtaksdato.
        Kolonne: S10_VEDTAKSDATO
    """)
    val vedtatt: LocalDate?,     // S10_VEDTAKSDATO

    @ApiModelProperty(notes = """
        Iverksettelsesdato.
        Kolonne: S10_IVERKSATTDATO
    """)
    val iverksatt: LocalDate?    // S10_IVERKSATTDATO
)

data class SakId(
    @ApiModelProperty(notes = "Kolonne: S05_SAKSBLOKK")
    val blokk: String,  // S05_SAKSBLOKK

    @ApiModelProperty(notes = "Kolonne: S10_SAKSNR")
    val nr: Int         // S10_SAKSNR
)


// Saker fra Foreldrepenge-base, ytelse Svangerskapspenger

data class ApenSakMedLopendeUtbetaling(
    @ApiModelProperty(notes = """
        Iverksettelsesdato.
        Kolonne: IS10_ARBUFOER
    """)
    val iverksatt: LocalDate, // IS10_ARBUFOER

    val utbetalinger: List<UtbetalingDto>
)

data class AvsluttedeSaker(
    @ApiModelProperty(notes = """
        Listen 'saker' viser resultater fra og med denne datoen (tidsbegrenset søk).
    """)
    val fraOgMed: LocalDate,

    val saker: List<AvsluttetSak>
)

data class AvsluttetSak(
    @ApiModelProperty(notes = """
        Iverksettelsesdato.
        Kolonne: IS10_ARBUFOER
    """)
    val iverksatt: LocalDate,   // IS10_ARBUFOER

    @ApiModelProperty(notes = """
        Stoppdato.
        Kolonne: IS10_STOPPDATO
    """)
    val stoppdato: LocalDate,    // IS10_STOPPDATO

    val utbetalinger: List<UtbetalingDto>
)

data class UtbetalingDto(
    @ApiModelProperty(notes = """
        Utbetalt fra og med.
        Kolonne: IS15_UTBETFOM
    """)
    val utbetaltFom: LocalDate,

    @ApiModelProperty(notes = """
        Utbetalt til og med.
        Kolonne: IS15_UTBETTOM
    """)
    val utbetaltTom: LocalDate,

    @ApiModelProperty(notes = """
        Gradering
        Kolonne: IS15_GRAD
    """)
    val gradering: Int
)