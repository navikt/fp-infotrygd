package no.nav.infotrygd.foreldrepenger.model.converters

import jakarta.persistence.AttributeConverter

class RefusjonJaNeiConverter : AttributeConverter<Boolean, String?> {

    private object charConverter : AbstractCharConverter(1)

    override fun convertToDatabaseColumn(attribute: Boolean?): String? {
        if(attribute == true) {
            return charConverter.convertToDatabaseColumn("J")
        }
        return charConverter.convertToDatabaseColumn(null)
    }

    override fun convertToEntityAttribute(dbData: String?): Boolean {
        return charConverter.convertToEntityAttribute(dbData) != null
    }
}