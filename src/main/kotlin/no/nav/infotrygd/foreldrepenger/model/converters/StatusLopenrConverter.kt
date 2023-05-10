package no.nav.infotrygd.foreldrepenger.model.converters

import jakarta.persistence.AttributeConverter
import java.math.BigInteger

class StatusLopenrConverter : AttributeConverter<BigInteger?, String?> {
    override fun convertToDatabaseColumn(attribute: BigInteger?): String? {
        return attribute?.let { String.format("%02d", it) }
    }

    override fun convertToEntityAttribute(dbData: String?): BigInteger? {
        return dbData?.toLong()?.let { BigInteger.valueOf(it) }
    }
}