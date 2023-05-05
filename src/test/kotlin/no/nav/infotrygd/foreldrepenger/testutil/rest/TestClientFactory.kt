package no.nav.infotrygd.foreldrepenger.testutil.rest

import no.nav.security.mock.oauth2.MockOAuth2Server
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.context.annotation.Profile
import org.springframework.http.HttpMethod
import org.springframework.http.HttpRequest
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.http.client.ClientHttpRequestExecution
import org.springframework.http.client.ClientHttpRequestInterceptor
import org.springframework.http.client.ClientHttpResponse
import org.springframework.stereotype.Component
import org.springframework.web.client.ResponseErrorHandler
import java.net.URI

class TestClientException(
    val status: HttpStatusCode,
    val remoteUrl: URI,
    val remoteMethod: HttpMethod,
    message: String
) : RuntimeException(message)

@Profile("test")
@Component
class TestClientFactory(
    private val restTemplateBuilder: RestTemplateBuilder,
    private val server: MockOAuth2Server
) {

    fun get(port: Int, sub: String): TestClient {
        val grupper = mutableListOf<String>()
        return TestClient(
            restTemplateBuilder(port)
                .additionalInterceptors(MockOAuth2ServerAccessTokenInterceptor(grupper, sub))
                .build()
        )
    }

    fun getNoAuth(port: Int): TestClient {
        return TestClient(restTemplateBuilder(port).build())
    }

    private fun restTemplateBuilder(port: Int) = restTemplateBuilder
        .errorHandler(TestErrorHandler())
        .rootUri("http://localhost:$port")
        .interceptors(ExceptionHandlerInterceptor())

    private inner class TestErrorHandler(): ResponseErrorHandler {
        override fun hasError(response: ClientHttpResponse): Boolean {
            val rawStatusCode = response.rawStatusCode
            val series = HttpStatus.Series.resolve(rawStatusCode)
            return series == HttpStatus.Series.CLIENT_ERROR || series == HttpStatus.Series.SERVER_ERROR
        }

        override fun handleError(response: ClientHttpResponse) {
            throw IllegalArgumentException("Not implemented")
        }

        override fun handleError(url: URI, method: HttpMethod, response: ClientHttpResponse) {
            val status = response.statusCode
            val contentType = response.headers.contentType

            val body = response.body.bufferedReader().use { it.readText() }

            throw TestClientException(
                status = status,
                remoteUrl = url,
                remoteMethod = method,
                message = "Feil ved REST-kall: $status $method - $url\n$body"
            )
        }
    }

    private inner class MockOAuth2ServerAccessTokenInterceptor(private val grupper: List<String>, val sub: String) : ClientHttpRequestInterceptor {
        override fun intercept(
            request: HttpRequest,
            body: ByteArray,
            execution: ClientHttpRequestExecution
        ): ClientHttpResponse {
            val token = server.issueToken(
                issuerId = "issuer1",
                audience = "aud-localhost",
                subject = sub,
                claims = mapOf("groups" to grupper)
            )
            request.headers.setBearerAuth(token.serialize())
            return execution.execute(request, body)
        }
    }
}