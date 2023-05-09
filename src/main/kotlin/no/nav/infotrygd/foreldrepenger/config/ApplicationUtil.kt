package no.nav.infotrygd.foreldrepenger.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class ApplicationUtil(@Value("\${spring.application.name}") private val applicationName: String) {
    enum class Application(private val appName: String) {
        INFOTRYGD_FORELDREPENGER("fp-infotrygd-foreldrepenger"),
        INFOTRYGD_SVANGERSKAPSPENGER("fp-infotrygd-svangerskapspenger")
        ;
    }

    fun gjelderForeldrepenger(): Boolean {
        return Application.INFOTRYGD_FORELDREPENGER.name == applicationName
    }

    fun gjelderSvangerskapspenger(): Boolean {
        return Application.INFOTRYGD_SVANGERSKAPSPENGER.name == applicationName
    }
}