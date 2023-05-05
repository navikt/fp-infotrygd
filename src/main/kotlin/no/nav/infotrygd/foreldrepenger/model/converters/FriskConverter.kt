package no.nav.infotrygd.foreldrepenger.model.converters

import no.nav.infotrygd.foreldrepenger.model.kodeverk.Frisk
import java.lang.IllegalStateException
import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter

@Converter
class FriskConverter : AttributeConverter<Frisk, String> {
    override fun convertToDatabaseColumn(attribute: Frisk): String {
        return attribute.kode
    }

    override fun convertToEntityAttribute(dbData: String): Frisk {
        return Frisk.values().find { it.kode.trimEnd() == dbData.trimEnd() } ?: throw IllegalStateException("Ukjent databaseverdi: '$dbData'")
    }
}