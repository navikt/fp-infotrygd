package no.nav.infotrygd.svangerskapspenger.rest.filter

import org.slf4j.LoggerFactory
import org.slf4j.MDC
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.Ordered.LOWEST_PRECEDENCE
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import org.springframework.web.filter.GenericFilterBean
import java.util.*
import javax.inject.Inject
import javax.servlet.FilterChain
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletRequest


@Component
@Order(LOWEST_PRECEDENCE)
class HeadersToMDCFilterBean @Inject
constructor(
    @param:Value("\${spring.application.name}") private val applicationName: String
) : GenericFilterBean() {
    private val log = LoggerFactory.getLogger(javaClass)
    private val consumerIdHeader = "Nav-Consumer-Id"
    private val callIdHeader = "Nav-CallId"

    override fun doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain) {
        putValues(HttpServletRequest::class.java.cast(request))
        try {
            chain.doFilter(request, response)
        } finally {
            MDC.clear()
        }
    }

    private fun putValues(request: HttpServletRequest) {
        try {
            toMDC(consumerIdHeader, request.getHeader(consumerIdHeader) ?: applicationName)
            toMDC(callIdHeader, request.getHeader(callIdHeader) ?: UUID.randomUUID().toString())
        } catch (e: Exception) {
            log.warn("Noe gikk galt ved setting av MDC-verdier for request {}, MDC-verdier er inkomplette", request.requestURI, e)
        }
    }

    override fun toString(): String {
        return "${javaClass.simpleName} [applicationName=$applicationName]"
    }

    fun toMDC(key: String, value: String) {
        MDC.put(key, value)
    }
}