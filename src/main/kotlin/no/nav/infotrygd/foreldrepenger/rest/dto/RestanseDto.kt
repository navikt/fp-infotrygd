package no.nav.infotrygd.foreldrepenger.rest.dto

import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDate


// Åpne saker fra Saksregister (kun hovedtabell). Alle kolonner er non-null
data class RestanseDto(
    val fnr: String,

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
    val valg: String?,

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
    val type: String,

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
    val mottatt: LocalDate?,

    @get:Schema(description = """
        Mottatt dato for sak.
        Kolonne: S10_VEDTAKSDATO
    """,
            example = "2019-01-01")   // S10_VEDTAKSDATO
    val vedtatt: LocalDate?,

    val reellEnhet: String?,

    val behandlendeEnhet: String?
)
