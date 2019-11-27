package no.nav.infotrygd.foreldrepenger.testutil

import no.nav.commons.foedselsnummer.FoedselsNr
import no.nav.commons.foedselsnummer.Kjoenn
import no.nav.commons.foedselsnummer.testutils.FoedselsnummerGenerator
import no.nav.infotrygd.foreldrepenger.model.Inntekt
import no.nav.infotrygd.foreldrepenger.model.Periode
import no.nav.infotrygd.foreldrepenger.model.Utbetaling
import no.nav.infotrygd.foreldrepenger.model.VedtakBarn
import no.nav.infotrygd.foreldrepenger.model.kodeverk.Frisk
import no.nav.infotrygd.foreldrepenger.model.kodeverk.Inntektsperiode
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
            registrert = LocalDate.now()
        )
    }

    fun utbetaling(): Utbetaling =
        Utbetaling(
            id = nextId(),
            personKey = 1,
            arbufoerSeq = 1,
            utbetaltTom = LocalDate.now(),
            utbetaltFom = LocalDate.now(),
            grad = null,
            type = null,
            korr = null
        )

    fun inntekt(): Inntekt =
        Inntekt(
            id = nextId(),
            personKey = 1,
            arbufoerSeq = 1,
            arbgiverNr = "12345678901",
            loenn = 1.toBigDecimal(),
            periode = Inntektsperiode.MAANEDLIG,
            refusjon = false
        )

    data class PeriodeFactory(
        val personKey: Long = nextId(),
        val arbufoerSeq: Long = nextId(),
        val fnr: FoedselsNr = foedselsNr(),
        val barnFnr: FoedselsNr = foedselsNr(),
        val stebarnsadopsjon: String? = null) {

        fun periode(): Periode = TestData.periode().copy(
            personKey = personKey,
            arbufoerSeq = arbufoerSeq,
            stebarnsadopsjon = stebarnsadopsjon,
            fnr = fnr,
            barnFnr = barnFnr
        )

        fun utbetaling(): Utbetaling = TestData.utbetaling().copy(
            personKey = personKey,
            arbufoerSeq = arbufoerSeq
        )

        fun inntekt(): Inntekt = TestData.inntekt().copy(
            personKey = personKey,
            arbufoerSeq = arbufoerSeq
        )

        fun vedtakBarn(): VedtakBarn {
            val periode = periode()
            return VedtakBarn(
                id = nextId(),
                personKey = periode.barnPersonKey!!,
                arbufoerSeq = arbufoerSeq.toString(),
                kode = periode().barnKode,
                dekningsgrad = 100.toBigDecimal()
            )
        }
    }

    private val fnrGenerator = FoedselsnummerGenerator()
}