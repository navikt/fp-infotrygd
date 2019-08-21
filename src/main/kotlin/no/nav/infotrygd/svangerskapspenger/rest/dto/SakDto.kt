package no.nav.infotrygd.svangerskapspenger.rest.dto

import io.swagger.annotations.ApiModelProperty
import java.time.LocalDate

data class SakResult(
    @ApiModelProperty(notes = "Relevant tillegsinformasjon om resultatet.")
    val info: String?,

    @ApiModelProperty(notes = "Saker/søknader under behandling (ikke ferdigbehandlet). Disse hentes fra Sak-basen.")
    val underBehandling: List<SakDto>, // S10_KAPITTELNR='FA', S10_VALG='SV', S10_TYPE IN ('S,'R')

    @ApiModelProperty(notes = "Klagesaker. Disse hentes fra Sak-basen.")
    val klagesaker: List<SakDto>,       // samme som over, men S10_TYPE='K'

    @ApiModelProperty(notes = "Ankesaker. Disse hentes fra Sak-basen.")
    val ankesaker: List<SakDto>,        // samme som over, men S10_TYPE='A'

    @ApiModelProperty(notes = "Åpne saker med løpende utbetaling. Disse hentes fra Foreldrepenge-base, ytelse Svangerskapspenger.")
    val apneSakerMedLopendeUtbetaling: List<ApenSakMedLopendeUtbetaling>,

    @ApiModelProperty(notes = "Avsluttede saker. Disse hentes fra Foreldrepenge-base, ytelse Svangerskapspenger.")
    val avsluttedeSaker: AvsluttedeSaker // IS10_STONADS_TYPE='SV', IS10_FRISK='F', IS10_ARBUFOER >= i dag minus 1 år
)

// Saker fra Saksregister, ytelse Svangerskapspenger

data class SakDto(
    val sakId: SakId?,

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
    val vedtatt: LocalDate,     // S10_VEDTAKSDATO

    @ApiModelProperty(notes = """
        Iverksettelsesdato.
        Kolonne: S10_IVERKSATTDATO
    """)
    val iverksatt: LocalDate    // S10_IVERKSATTDATO
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
    val iverksatt: LocalDate // IS10_ARBUFOER
)

data class AvsluttedeSaker(
    @ApiModelProperty(notes = """
        Listen 'saker' viser resultater fra og med denne datoen (tidsbegrenset søk).
    """)
    val fraOgMed: LocalDate,

    val saker: List<AvsluttetSak>)

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
    val stoppdato: LocalDate    // IS10_STOPPDATO
)