package no.nav.infotrygd.foreldrepenger.service

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class ApplicationUtil(@Value("\${spring.application.name}") private val applicationName: String) {
    enum class Application(val appName: String) {
        INFOTRYGD_FORELDREPENGER("fp-infotrygd-foreldrepenger"),
        INFOTRYGD_SVANGERSKAPSPENGER("fp-infotrygd-svangerskapspenger"),
        INFOTRYGD_SYKEPENGER("fp-infotrygd-sykepenger")
        ;
    }

    fun getApplication(): Application {
        return when (applicationName) {
            Application.INFOTRYGD_FORELDREPENGER.appName -> Application.INFOTRYGD_FORELDREPENGER
            Application.INFOTRYGD_SVANGERSKAPSPENGER.appName -> Application.INFOTRYGD_SVANGERSKAPSPENGER
            Application.INFOTRYGD_SYKEPENGER.appName -> Application.INFOTRYGD_SYKEPENGER
            else -> { Application.INFOTRYGD_FORELDREPENGER }
        }
    }
}