package no.nav.infotrygd.foreldrepenger.model.converters

import no.nav.infotrygd.foreldrepenger.exception.UkjentDatabaseverdiException
import no.nav.infotrygd.foreldrepenger.model.kodeverk.Kode
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class KodeConverterTest {

    private val converter = TestConverter()

    @Test
    fun convertToDatabaseColumn() {
        assertThat(converter.convertToDatabaseColumn(E.B)).isEqualTo("b ")
        assertThat(converter.convertToDatabaseColumn(null)).isNull()
    }

    @Test
    fun convertToEntityAttribute() {
        assertThat(converter.convertToEntityAttribute("b")).isEqualTo(E.B)
        assertThat(converter.convertToEntityAttribute("b ")).isEqualTo(E.B)
        assertThat(converter.convertToEntityAttribute(null)).isNull()
        assertThat(converter.convertToEntityAttribute("  ")).isNull()
    }

    @Test
    fun convertToEntityAttribute_Exception() {
        Assertions.assertThrows(UkjentDatabaseverdiException::class.java) { converter.convertToEntityAttribute("XX") }
    }

    @Test
    fun blankValue() {
        val bv = TestConverterWithBlankValue()
        assertThat(bv.convertToDatabaseColumn(F.A)).isEqualTo("  ")
        assertThat(bv.convertToEntityAttribute("  ")).isEqualTo(F.A)
    }

    private enum class E(override val kode: String, override val tekst: String) : Kode {
        A("a", "aaa"),
        B("b", "bbb"),
    }

    private class TestConverter : KodeConverter<E>(E.values().toList(), fieldSize = 2)


    private enum class F(override val kode: String, override val tekst: String) : Kode {
        A("", "aaa"),
        B("b", "bbb"),
    }

    private class TestConverterWithBlankValue : KodeConverter<F>(F.values().toList(), fieldSize = 2)
}