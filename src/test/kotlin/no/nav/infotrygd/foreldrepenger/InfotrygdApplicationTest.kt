package no.nav.infotrygd.foreldrepenger

import no.nav.infotrygd.foreldrepenger.testutil.restClientNoAuth
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpStatus
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit4.SpringRunner

@RunWith(SpringRunner::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class InfotrygdApplicationTest {

    @LocalServerPort
    var port: kotlin.Int = 0

    @Test
    fun contextLoads() {
    }

    @Test
    fun health() {
        val response = restClientNoAuth(port)
            .get()
            .uri("/actuator/health")
            .exchange()
            .block() !!

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK)
    }
}
