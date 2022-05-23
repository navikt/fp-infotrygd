package no.nav.commons.foedselsnummer.testutils

import no.nav.commons.foedselsnummer.FoedselsNr
import no.nav.commons.foedselsnummer.Kjoenn
import java.time.LocalDate

class FoedselsnummerGenerator {

    private val generators: MutableMap<Params, KonkretFoedselsnummerGenerator> = mutableMapOf()

    private var date: LocalDate = LocalDate.of(1854, 1, 1)

    fun foedselsnummer(
        foedselsdato: LocalDate? = null,
        kjoenn: Kjoenn = Kjoenn.MANN,
        dNummer: Boolean = false
    ): FoedselsNr {
        return generator(foedselsdato ?: date, kjoenn, dNummer).next()
    }

    private fun generator(foedselsdato: LocalDate,
                          kjoenn: Kjoenn,
                          dNummer: Boolean): KonkretFoedselsnummerGenerator {
        val params = Params(foedselsdato, kjoenn, dNummer)
        val generator = generators.getOrElse(params) {
            KonkretFoedselsnummerGenerator(foedselsdato, kjoenn, dNummer)
        }
        generators[params] = generator
        return generator
    }

    private data class Params(val foedselsdato: LocalDate, val kjoenn: Kjoenn, val dNummer: Boolean)
}