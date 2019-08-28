package no.nav.infotrygd.svangerskapspenger

import no.nav.infotrygd.svangerskapspenger.rest.dto.SakResult
import no.nav.infotrygd.svangerskapspenger.testutil.svangerskapspengerClient
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit4.SpringRunner

@RunWith(SpringRunner::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class SakTest {

    @LocalServerPort
    var port: Int = 0

    @Test
    fun hentSaker() {
        val sak = svangerskapspengerClient(port)
            .get()
            .uri("/saker?fnr=10000000001")
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .bodyToMono(SakResult::class.java)
            .block()
        assertThat(sak).isNotNull
        System.err.println(sak)
    }
}