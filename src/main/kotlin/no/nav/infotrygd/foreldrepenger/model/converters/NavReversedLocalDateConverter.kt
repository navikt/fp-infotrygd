package no.nav.infotrygd.foreldrepenger.model.converters

import jakarta.persistence.Converter

@Converter
class NavReversedLocalDateConverter : AbstractNavLocalDateConverter("ddMMyyyy")