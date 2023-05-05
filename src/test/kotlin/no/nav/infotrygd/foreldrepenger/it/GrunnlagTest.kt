package no.nav.infotrygd.foreldrepenger.it

import no.nav.infotrygd.foreldrepenger.model.kodeverk.Stoenadstype
import no.nav.infotrygd.foreldrepenger.repository.PeriodeRepository
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
import org.springframework.context.annotation.Import
import org.springframework.http.HttpStatus
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit4.SpringRunner
import java.time.LocalDate

@RunWith(SpringRunner::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Import(TestClientFactory::class)
class GrunnlagTest {

    @LocalServerPort
    private var port: Int = 0

    @Autowired
    private lateinit var periodeRepository: PeriodeRepository

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
    fun grunnlag() {

        val pf = TestData.PeriodeFactory(fnr = fnr)

        val periode = pf.periode().copy(
            stoenadstype = Stoenadstype.ADOPSJON,
            foedselsdatoBarn = LocalDate.now().minusYears(1)
        )
        periodeRepository.save(periode)

        val result = client().hentGrunnlag(fnr, fom)

        assertThat(result).hasSize(1)
    }

    @Test
    fun `tjenesten krever token for å få tilgang`() {
        val e = assertThrows<TestClientException> {
            clientNoAuth().hentGrunnlag(fnr, fom)
        }
        assertThat(e.status).isEqualTo(HttpStatus.UNAUTHORIZED)
    }

    @Test
    fun `tjenesten krever rett sub for å få tilgang`() {
        val e = assertThrows<TestClientException> {
            client(sub = "uautorisert").hentGrunnlag(fnr, fom)
        }
        assertThat(e.status).isEqualTo(HttpStatus.UNAUTHORIZED)
    }
}