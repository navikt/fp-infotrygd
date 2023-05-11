package no.nav.commons.foedselsnummer.testutils.eksempel

import no.nav.commons.foedselsnummer.FoedselsNr
import no.nav.commons.foedselsnummer.Kjoenn
import no.nav.commons.foedselsnummer.testutils.FoedselsnummerGenerator
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.LocalDate

class EksempelTest {
    @Test
    fun foedselsnummer() {
        val fnr = FoedselsNr("01015450572")
        assertThat(fnr.kjoenn).isEqualTo(Kjoenn.MANN)
    }

    @Test
    fun genererUnikeFoedselsnummer() {
        val generator = FoedselsnummerGenerator()
        val a: FoedselsNr = generator.foedselsnummer()
        val b: FoedselsNr = generator.foedselsnummer()
        assertThat(a).isNotEqualTo(b)
    }

    @Test
    fun genererFodeselsnummerMedParametre() {
        val generator = FoedselsnummerGenerator()
        val fnr = generator.foedselsnummer(
            foedselsdato = LocalDate.of(2019, 1, 2),
            kjoenn = Kjoenn.KVINNE,
            dNummer = false)

        assertThat(fnr.asString).startsWith("020119")
        assertThat(fnr.kjoenn).isEqualTo(Kjoenn.KVINNE)
    }
}