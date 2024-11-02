package no.nav.infotrygd.foreldrepenger.testutil

import no.nav.commons.foedselsnummer.FoedselsNr
import no.nav.commons.foedselsnummer.Kjoenn
import no.nav.commons.foedselsnummer.testutils.FoedselsnummerGenerator
import no.nav.infotrygd.foreldrepenger.model.*
import no.nav.infotrygd.foreldrepenger.model.kodeverk.*
import no.nav.infotrygd.foreldrepenger.nextId
import java.math.BigDecimal
import java.math.BigInteger
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
            personKey = BigInteger.ONE,
            arbufoerSeq = BigInteger.ONE,
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
            personKey = BigInteger.ONE,
            arbufoerSeq = BigInteger.ONE,
            utbetaltTom = LocalDate.now(),
            utbetaltFom = LocalDate.now(),
            utbetalingsdato = LocalDate.now(),
            grad = null,
            type = null,
            korr = null,
            arbgiverNr = BigInteger("12345678901"),
            dagsats = BigDecimal.valueOf(1000)
        )

    fun inntekt(): Inntekt =
        Inntekt(
            id = nextId(),
            region = "X",
            personKey = BigInteger.ONE,
            arbufoerSeq = BigInteger.ONE,
            arbgiverNr = BigInteger.valueOf(12345678901),
            loenn = 1.toBigDecimal(),
            periode = Inntektsperiode.MAANEDLIG,
            refusjon = false,
            refusjonTom = null
        )

    fun sak(fnr: FoedselsNr = foedselsNr()): Sak {
        val saksblokk = "X"
        val saksnummer = "11"
        return Sak(
            id = nextId(),
            fnr = fnr,
            saksblokk = saksblokk,
            saksnummer = saksnummer,
            kapittelNr = "FA",
            valg = SakValg.FP_A,
            undervalg = SakUndervalg.UKJENT,
            nivaa = SakNivaa.TK,
            type = SakType.A,
            resultat = SakResultat.A,
            vedtaksdato = LocalDate.now(),
            iverksattdato = LocalDate.now(),
            registrert = LocalDate.now(),
            mottatt = LocalDate.now(),
            reellEnhet = "4867",
            behandlendeEnhet = "4867"
        )
    }

    data class PeriodeFactory(
        val personKey: BigInteger = nextId(),
        val arbufoerSeq: BigInteger = nextId(),
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