package no.nav.infotrygd.foreldrepenger.model.converters

import jakarta.persistence.Converter

@Converter
class NavLocalDateConverter : AbstractNavLocalDateConverter("yyyyMMdd")