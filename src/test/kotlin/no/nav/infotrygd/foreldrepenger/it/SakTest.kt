package no.nav.infotrygd.foreldrepenger.it

import no.nav.infotrygd.foreldrepenger.testutil.TestData
import no.nav.infotrygd.foreldrepenger.testutil.rest.TestClient
import no.nav.infotrygd.foreldrepenger.testutil.rest.TestClientFactory
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
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

}