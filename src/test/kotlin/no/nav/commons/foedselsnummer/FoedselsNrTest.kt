package no.nav.commons.foedselsnummer

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import java.time.LocalDate

class FoedselsNrTest {
    @Test
    fun kjoenn() {
        val mann = FoedselsNr("00000000191")
        val kvinne = FoedselsNr("00000000272")

        assertThat(mann.kjoenn).isEqualTo(Kjoenn.MANN)
        assertThat(kvinne.kjoenn).isEqualTo(Kjoenn.KVINNE)
    }
}