package no.nav.commons.foedselsnummer.testutils

import no.nav.commons.foedselsnummer.FoedselsNr
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import java.time.LocalDate


internal class FoedselsnummerGeneratorTest {
    @Test
    fun medDato() {
        val generator = FoedselsnummerGenerator()
        val fnr = generator.foedselsnummer(LocalDate.of(2019, 1, 1))
        assertThat(fnr.asString).startsWith("010119")
    }

    @Test
    fun tomForFoedselsnummer() {
        val generator = FoedselsnummerGenerator()
        assertThrows(IllegalStateException::class.java) {
            for (i in 0 until 10000) {
                generator.foedselsnummer(LocalDate.of(2019, 1, 1))
            }
        }
    }

    @Test
    fun unikeFnr() {
        val generator = FoedselsnummerGenerator()
        val set = mutableSetOf<FoedselsNr>()

        for (i in 0 until 9998) {
            val fnr = generator.foedselsnummer()
            assertThat(set).doesNotContain(fnr)
            set.add(fnr)
        }
    }
}