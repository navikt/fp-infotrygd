package no.nav.infotrygd.foreldrepenger.model.converters

import javax.persistence.Converter

@Converter
class NavReversedLocalDateConverter : AbstractNavLocalDateConverter("ddMMyyyy")