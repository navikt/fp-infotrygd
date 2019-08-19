package no.nav.infotrygd.svangerskapspenger.model

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.persistence.AttributeConverter
import javax.persistence.Converter

@Converter
class NavLocalDateConverter : AttributeConverter<LocalDate?, Int?> {
    private val formatter = DateTimeFormatter.ofPattern("yyyMMdd");

    override fun convertToDatabaseColumn(attribute: LocalDate?): Int? {
        return attribute?.format(formatter)?.toInt()
    }

    override fun convertToEntityAttribute(dbData: Int?): LocalDate? {
        if(dbData == null) {
            return null
        }
        return LocalDate.from(formatter.parse(dbData.toString()))
    }
}