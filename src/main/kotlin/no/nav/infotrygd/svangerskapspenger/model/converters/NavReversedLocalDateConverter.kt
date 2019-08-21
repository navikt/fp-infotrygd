package no.nav.infotrygd.svangerskapspenger.model.converters

import javax.persistence.Converter

@Converter
class NavReversedLocalDateConverter : AbstractNavLocalDateConverter("ddMMyyyy")