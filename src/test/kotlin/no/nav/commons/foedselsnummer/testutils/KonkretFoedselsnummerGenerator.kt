package no.nav.commons.foedselsnummer.testutils

import no.nav.commons.foedselsnummer.FoedselsNr
import no.nav.commons.foedselsnummer.Kjoenn
import java.time.LocalDate

internal class KonkretFoedselsnummerGenerator(
    private val foedselsdato: LocalDate,
    private val kjoenn: Kjoenn,
    private val dNummer: Boolean
) {
    private var lopeNr: Int = 1

    fun next(): FoedselsNr {
        var day = foedselsdato.dayOfMonth
        if(dNummer) day += 40

        val individnummer = personnummer()
        val fnr: String = String.format("%02d%02d%s%05d",
            day, foedselsdato.monthValue, foedselsdato.year.toString().takeLast(2), individnummer
        )

        val resultat = FoedselsNr(fnr)
        assert(resultat.kjoenn == kjoenn)
        return resultat
    }

    private fun personnummer(): Int {
        lopeNr += 1

        if (lopeNr > 9999) {
            throw IllegalStateException("Tom for fødselsnummer")
        }

        val kjoenn = when(this.kjoenn) {
            Kjoenn.MANN -> 1
            Kjoenn.KVINNE -> 2
        }

        val s = lopeNr.toString().padStart(4, '0')

        return (s.slice(0 until 2) + kjoenn.toString() + s.slice(2 until 4)).toInt()
    }
}
