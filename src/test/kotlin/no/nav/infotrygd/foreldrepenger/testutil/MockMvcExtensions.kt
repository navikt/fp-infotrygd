package no.nav.infotrygd.foreldrepenger.testutil

import org.springframework.test.web.servlet.ResultActions
import tools.jackson.core.type.TypeReference
import tools.jackson.module.kotlin.jacksonObjectMapper

inline fun <reified T> ResultActions.body(): T =
    jacksonObjectMapper().readValue(andReturn().response.contentAsByteArray, object : TypeReference<T>() {})
