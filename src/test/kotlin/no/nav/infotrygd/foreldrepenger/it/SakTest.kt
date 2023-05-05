package no.nav.infotrygd.foreldrepenger.it

import no.nav.infotrygd.foreldrepenger.testutil.TestData
import no.nav.infotrygd.foreldrepenger.testutil.rest.TestClient
import no.nav.infotrygd.foreldrepenger.testutil.rest.TestClientException
import no.nav.infotrygd.foreldrepenger.testutil.rest.TestClientFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.junit.jupiter.api.assertThrows
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpStatus
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit4.SpringRunner

@RunWith(SpringRunner::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class SakTest {

    @LocalServerPort
    var port: Int = 0

    @Autowired
    private lateinit var clientFactory: TestClientFactory

    private fun client(sub: String = "12345678910"): TestClient {
        return clientFactory.get(port, sub)
    }

    private fun clientNoAuth(): TestClient {
        return clientFactory.getNoAuth(port)
    }

    private val fnr = TestData.foedselsNr()

    @Test
    fun hentSaker() {
        val sak = client().hentSaker(fnr)

        System.err.println(sak)
    }

    @Test
    fun `tjenesten krever token for å få tilgang`() {
        val e = assertThrows<TestClientException> {
            clientNoAuth().hentSaker(fnr)
        }
        assertThat(e.status).isEqualTo(HttpStatus.UNAUTHORIZED)
    }

    @Test
    fun `tjenesten krever rett sub for å få tilgang`() {
        val e = assertThrows<TestClientException> {
            client(sub = "uautorisert").hentSaker(fnr)
        }
        assertThat(e.status).isEqualTo(HttpStatus.UNAUTHORIZED)
    }
}