package no.nav.infotrygd.svangerskapspenger

import no.nav.infotrygd.svangerskapspenger.rest.dto.SakResult
import no.nav.infotrygd.svangerskapspenger.testutil.TestData
import no.nav.infotrygd.svangerskapspenger.testutil.svangerskapspengerClient
import no.nav.infotrygd.svangerskapspenger.testutil.svangerskapspengerNoAuthClient
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.web.reactive.function.client.WebClientResponseException

@RunWith(SpringRunner::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class SakTest {

    @LocalServerPort
    var port: Int = 0

    private val fnr = TestData.foedselsNr().asString

    @Test
    fun hentSaker() {
        val sak = svangerskapspengerClient(port)
            .get()
            .uri("/saker?fnr=$fnr")
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .bodyToMono(SakResult::class.java)
            .block()
        assertThat(sak).isNotNull
        System.err.println(sak)
    }

    @Test(expected = WebClientResponseException.Unauthorized::class)
    fun auth() {
        svangerskapspengerNoAuthClient(port)
            .get()
            .uri("/saker?fnr=$fnr")
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .bodyToMono(SakResult::class.java)
            .block()
    }
}