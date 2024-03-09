package no.nav.infotrygd.foreldrepenger.it

import no.nav.infotrygd.foreldrepenger.model.kodeverk.SakType
import no.nav.infotrygd.foreldrepenger.repository.PeriodeRepository
import no.nav.infotrygd.foreldrepenger.repository.SakRepository
import no.nav.infotrygd.foreldrepenger.testutil.TestData
import no.nav.infotrygd.foreldrepenger.testutil.rest.TestClient
import no.nav.infotrygd.foreldrepenger.testutil.rest.TestClientFactory
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.time.LocalDate

@ExtendWith(SpringExtension::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class SakTest {

    @LocalServerPort
    private var port: Int = 0

    @Autowired
    private lateinit var periodeRepository: PeriodeRepository

    @Autowired
    private lateinit var sakRepository: SakRepository

    @Autowired
    private lateinit var clientFactory: TestClientFactory


    private fun client(sub: String = "12345678910"): TestClient {
        return clientFactory.get(port, sub)
    }

    private fun clientNoAuth(): TestClient {
        return clientFactory.getNoAuth(port)
    }

    private val fnr = TestData.foedselsNr()
    private val fom = LocalDate.parse("2020-01-01")



    @Test
    fun sak() {
        val sak = TestData.sak(fnr)
        sakRepository.save(sak)

        val result = client().hentSaker(fnr, fom)

        Assertions.assertThat(result).hasSize(1)
        Assertions.assertThat(result.get(0).type.kode?: "").isEqualTo(SakType.A.kode)
    }

}