package no.nav.infotrygd.foreldrepenger.testutil

import no.nav.commons.foedselsnummer.FoedselsNr
import no.nav.commons.foedselsnummer.Kjoenn
import no.nav.commons.foedselsnummer.testutils.FoedselsnummerGenerator
import no.nav.infotrygd.foreldrepenger.model.*
import no.nav.infotrygd.foreldrepenger.model.kodeverk.Frisk
import no.nav.infotrygd.foreldrepenger.model.kodeverk.Inntektsperiode
import no.nav.infotrygd.foreldrepenger.model.kodeverk.SakStatus
import no.nav.infotrygd.foreldrepenger.model.kodeverk.Stoenadstype
import no.nav.infotrygd.foreldrepenger.nextId
import java.time.LocalDate

object TestData {
    fun foedselsNr(
        foedselsdato: LocalDate? = null,
        kjoenn: Kjoenn = Kjoenn.MANN): FoedselsNr {

        return fnrGenerator.foedselsnummer(
            foedselsdato = foedselsdato,
            kjoenn = kjoenn
        )
    }

    fun periode(): Periode {
        return Periode(
            id = nextId(),
            region = "X",
            personKey = 1,
            arbufoerSeq = 1,
            fnr = foedselsNr(),
            stoenadstype = Stoenadstype.SVANGERSKAP,
            frisk = Frisk.LOPENDE,
            arbufoer = LocalDate.now(),
            stoppdato = null,
            utbetalingshistorikk = listOf(),
            inntekter = listOf(),
            utbetaltFom = null,
            utbetaltTom = null,
            arbufoerOpprinnelig = LocalDate.now(),
            dekningsgrad = null,
            foedselsdatoBarn = null,
            arbeidskategori = null,
            tkNr = "1000",
            barnFnr = foedselsNr(),
            stebarnsadopsjon = null,
            brukerId = "br.id",
            morFnr = foedselsNr(),
            registrert = LocalDate.now(),
            arbufoerTom = null,
            friskmeldtDato = null,
            maksdato = null
        )
    }

    fun utbetaling(): Utbetaling =
        Utbetaling(
            id = nextId(),
            region = "X",
            personKey = 1,
            arbufoerSeq = 1,
            utbetaltTom = LocalDate.now(),
            utbetaltFom = LocalDate.now(),
            utbetalingsdato = LocalDate.now(),
            grad = null,
            type = null,
            korr = null
        )

    fun inntekt(): Inntekt =
        Inntekt(
            id = nextId(),
            region = "X",
            personKey = 1,
            arbufoerSeq = 1,
            arbgiverNr = "12345678901",
            loenn = 1.toBigDecimal(),
            periode = Inntektsperiode.MAANEDLIG,
            refusjon = false
        )

    fun sak(fnr: FoedselsNr = foedselsNr()): Sak {
        val saksblokk = "X"
        val saksnummer = "11"
        val personKey = nextId()
        return Sak(
            id = nextId(),
            fnr = fnr,
            personKey = personKey,
            saksblokk = saksblokk,
            saksnummer = saksnummer,
            kapittelNr = "BS",
            valg = "PN",
            type = "A",
            resultat = "A",
            vedtaksdato = LocalDate.now(),
            iverksattdato = LocalDate.now(),
            registrert = LocalDate.now(),
            statushistorikk = listOf(
                Status(
                    id = nextId(),
                    personKey = personKey,
                    saksblokk = saksblokk,
                    saksnummer = saksnummer,
                    lopeNr = nextId() % 99,
                    status = SakStatus.IKKE_BEHANDLET
                )
            )
        )
    }

    data class PeriodeFactory(
        val personKey: Long = nextId(),
        val arbufoerSeq: Long = nextId(),
        val fnr: FoedselsNr = foedselsNr(),
        val barnFnr: FoedselsNr = foedselsNr(),
        val region: String = "X",
        val stoenadstype: Stoenadstype = Stoenadstype.SVANGERSKAP,
        val stebarnsadopsjon: String? = null) {

        fun periode(): Periode = TestData.periode().copy(
            region = region,
            personKey = personKey,
            arbufoerSeq = arbufoerSeq,
            stoenadstype = stoenadstype,
            stebarnsadopsjon = stebarnsadopsjon,
            fnr = fnr,
            barnFnr = barnFnr
        )

        fun utbetaling(): Utbetaling = TestData.utbetaling().copy(
            region = region,
            personKey = personKey,
            arbufoerSeq = arbufoerSeq
        )

        fun inntekt(): Inntekt = TestData.inntekt().copy(
            region = region,
            personKey = personKey,
            arbufoerSeq = arbufoerSeq
        )

        fun vedtakBarn(): VedtakBarn {
            val periode = periode()
            return VedtakBarn(
                id = nextId(),
                region = region,
                personKey = periode.barnPersonKey!!,
                arbufoerSeq = arbufoerSeq.toString(),
                kode = periode().barnKode,
                dekningsgrad = 100.toBigDecimal()
            )
        }
    }

    private val fnrGenerator = FoedselsnummerGenerator()
}