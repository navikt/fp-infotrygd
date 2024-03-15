package no.nav.infotrygd.foreldrepenger.rest.dto

import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDate

data class SakResult(
    @get:Schema(description = "Relevant tillegsinformasjon om resultatet.")
    val info: String?,

    val saker: List<SakDto>,
)

// Saker fra Saksregister (kun hovedtabell). Alle kolonner er non-null

data class SakDto(
    val sakId: SakId,

    @get:Schema(description = """
        Type valg.
        
        AE: Adopsjon engangsstønad
        AP: Foreldrepenger adopsjon 
        FE: Fødsel engangsstønad
        FP: Foreldrepenger
        FU: Foreldrepenger utland
        FØ: Foreldrepenger fødsel
        SV: Svangerskapspenger
        
        Kolonne: S10_VALG
    """,
            allowableValues = ["AE", "AP", "FE", "FP", "FU", "FØ", "SV"])
    val valg: SakKodeverdi?,

    @get:Schema(description = """
        Type undervalg.
        
        FG: fedrekvote gradering
        FK: fedrekvote ordinær
        FU: fedrekvote utsettelse
        GA: gradering
        NA: engangsstønad nasjonal
        OS: ordinær
        UA: utsettelse fulltidsarbeid
        UF: utsettelse ferie
        UL: engangsstønad utland
        US: utsettelse sykdom
        
        Kolonne: S10_UNDERVALG
    """)
    val undervalg: SakKodeverdi,

    @get:Schema(description = """
        Type nivaa.
        
        AN: Ankenemda
        FFU: Utlandskontoret
        FTK: FTK
        HTF: Helsetrygdforvaltning
        KA: Klageinstansen
        KI: Klageinstansen
        RTV: Rikstrygdeverket
        TK: Trygdekontor
        TR: Trygderetten
        
        Kolonne: S10_NIVAA
    """)
    val nivaa: SakKodeverdi,

    @get:Schema(description = """
        Type sak.
        
        S: Søknad
        R: Revurdering
        K: Klage
        A: Anke
        DF: Dispensasjon foreldelse
        DI: Dokumentinnsyn
        EG: Etterlyse girokort
        FS: Forespørsel
        I: Informasjonssak
        J: Journalsak
        JP: Journalsak fra privatperson
        JT: Journalsak fra trygdekontor
        JU: Journalsak fra utenl trm
        KE: Klage ettergivelse
        KS: Kontrollsak
        KT: Klage tilbakebetaling
        SE: Søknad om ettergivelse
        SV: Strafferettslig vurdering
        T: Tilbakebetalingssak
        TE: Tilbakebetaling endring
        TK: Tidskonto
        TU: Tipsutredning
        UA: Utbetalt til annen
        
        Kolonne: S10_TYPE
    """)
    val type: SakKodeverdi,

    @get:Schema(description = """
        Resultatkode for saken.
        
        Dette er NOEN av kodene:
        
        ?: beslutningsstøtte Besl st
        A: Avslag
        AG: ukjent
        AK: avvist klage
        AV: advarsel
        DG: ukjent
        DI: delvis innvilget
        DT: delvis tilbakebetale
        FB: ferdigbehandlet
        FI: fortsatt innvilget
        GK: ukjent
        H: henlagt / trukket tilbake
        HB: henlagt / bortfalt
        I: Innvilget
        IB: ukjent
        IN: innvilget ny situasjon
        IS: ikke straffbart
        IT: ikke tilbakebetale
        MO: midlertidig opphørt
        MT: mottatt
        NB: ukjent
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
    val resultat: SakKodeverdi,

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
    val registrert: LocalDate?,   // S10_REG_DATO

    @get:Schema(description = """
        Mottatt dato for sak.
        Kolonne: S10_MOTTATTDATO
    """,
        example = "2019-01-01")   // S10_MOTTATTDATO
    val mottatt: LocalDate?
)

data class SakId(
    @get:Schema(description = "Kolonne: S05_SAKSBLOKK")
    val blokk: String,  // S05_SAKSBLOKK

    @get:Schema(description = "Kolonne: S10_SAKSNR")
    val nr: Int         // S10_SAKSNR
)

data class SakKodeverdi(val kode: String, val termnavn: String)
