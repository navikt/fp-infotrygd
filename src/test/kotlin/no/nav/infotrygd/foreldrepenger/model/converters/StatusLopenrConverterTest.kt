package no.nav.infotrygd.foreldrepenger.model.converters

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.math.BigInteger

class StatusLopenrConverterTest {
    val converter: StatusLopenrConverter =
        StatusLopenrConverter()

    @Test
    fun convertToDatabaseColumn() {
        val result = converter.convertToDatabaseColumn(BigInteger.valueOf(2))
        assertThat(result).isEqualTo("02")

        assertThat(converter.convertToDatabaseColumn(null)).isNull()
    }

    @Test
    fun convertToEntityAttribute() {
        val result = converter.convertToEntityAttribute("02")
        assertThat(result).isEqualTo(2)

        assertThat(converter.convertToEntityAttribute(null)).isNull()
    }
}