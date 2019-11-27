package no.nav.infotrygd.foreldrepenger.model.converters

import javax.persistence.Converter

@Converter
class NavLocalDateConverter : AbstractNavLocalDateConverter("yyyyMMdd")