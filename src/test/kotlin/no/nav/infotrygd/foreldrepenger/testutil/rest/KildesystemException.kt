package no.nav.infotrygd.foreldrepenger.testutil.rest

import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException
import java.net.URI

class KildesystemException(
    reason: String,
    status: HttpStatus? = null,
    val remoteUrl: URI? = null,
    val remoteMethod: HttpMethod? = null,
    cause: Throwable? = null,
    private val additionalResponseHeaders: Map<String, List<String>> = mapOf()
) : ResponseStatusException(status ?: HttpStatus.INTERNAL_SERVER_ERROR, genererMelding(reason, remoteMethod, remoteUrl), cause) {
    override fun getResponseHeaders(): HttpHeaders {
        val headers = additionalResponseHeaders
        if (headers.isEmpty()) {
            return HttpHeaders.EMPTY
        }
        val result = HttpHeaders()
        headers.forEach { (headerName: String, headerValues: List<String>) ->
            result.addAll(
                headerName, headerValues
            )
        }
        return result
    }
}

private fun genererMelding(
    reason: String,
    remoteMethod: HttpMethod?,
    remoteUrl: URI?
): String {
    return "[$reason ${remoteMethod ?: ""} ${remoteUrl?.scheme ?: ""}://${remoteUrl?.host ?: ""}:${remoteUrl?.port ?: ""}${remoteUrl?.path ?: ""}"
}
