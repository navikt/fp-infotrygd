package no.nav.commons.foedselsnummer.testutils

import no.nav.commons.foedselsnummer.Kjoenn
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import java.time.LocalDate

internal class KonkretFoedselsnummerGeneratorTest {

    @Test
    fun generer() {
        val dato = LocalDate.of(2000, 10, 15)

        val mann = KonkretFoedselsnummerGenerator(kjoenn = Kjoenn.MANN, foedselsdato = dato, dNummer = false).next()
        val kvinne = KonkretFoedselsnummerGenerator(kjoenn = Kjoenn.KVINNE, foedselsdato = dato, dNummer = false).next()

        assertThat(mann.kjoenn).isEqualTo(Kjoenn.MANN)
        assertThat(kvinne.kjoenn).isEqualTo(Kjoenn.KVINNE)
    }

    @Test
    fun ulikeNummer() {
        val generator = KonkretFoedselsnummerGenerator(kjoenn = Kjoenn.MANN, foedselsdato = LocalDate.now(), dNummer = false)
        val a = generator.next()
        val b = generator.next()

        assertThat(a).isNotEqualTo(b)
    }
}