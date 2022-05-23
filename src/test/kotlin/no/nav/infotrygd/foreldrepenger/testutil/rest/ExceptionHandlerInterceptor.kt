package no.nav.infotrygd.foreldrepenger.testutil.rest

import no.nav.infotrygd.foreldrepenger.testutil.rest.KildesystemException
import org.springframework.http.HttpRequest
import org.springframework.http.client.ClientHttpRequestExecution
import org.springframework.http.client.ClientHttpRequestInterceptor
import org.springframework.http.client.ClientHttpResponse

class ExceptionHandlerInterceptor() : ClientHttpRequestInterceptor {
    override fun intercept(
        request: HttpRequest,
        body: ByteArray,
        execution: ClientHttpRequestExecution
    ): ClientHttpResponse {
        try {
            return execution.execute(request, body)
        } catch (e: Exception) {
            throw KildesystemException(
                reason = "Feil ved REST-kall",
                remoteUrl = request.uri,
                remoteMethod = request.method,
                cause = e
            )
        }
    }
}