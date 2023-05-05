package no.nav.infotrygd.foreldrepenger.repository

import no.nav.commons.foedselsnummer.FoedselsNr
import no.nav.infotrygd.foreldrepenger.model.Sak
import no.nav.infotrygd.foreldrepenger.model.Status
import no.nav.infotrygd.foreldrepenger.model.kodeverk.SakStatus
import no.nav.infotrygd.foreldrepenger.nextId
import no.nav.infotrygd.foreldrepenger.testutil.TestData
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit4.SpringRunner
import java.time.LocalDate

@RunWith(SpringRunner::class)
@DataJpaTest
@ActiveProfiles("test")
class SakRepositoryTest {
    @Autowired
    lateinit var repository: SakRepository

    @Autowired
    lateinit var statusRepository: StatusRepository

    var sakNr = 1

    private val alleValg: List<String>
        get() {
            return listOf("AE", "AP", "FE", "FP", "FU", "FØ")
        }

    @Before
    fun setUp() {
        repository.deleteAll()
    }

    @Test
    fun findSakerByFnr() {

        for(valg in alleValg) {
            val fnr = TestData.foedselsNr()
            val status = SakStatus.FB
            val s = lagSak(
                fnr = fnr,
                kapittelNr = "FA",
                valg = valg,
                type = "S",
                sakStatus = status)


            repository.findSakerByFnr(fnr).also {
                assertThat(it).hasSize(1)
                assertThat(it[0].statushistorikk).hasSize(1)
                assertThat(it[0].status).isEqualTo(status)
            }

            repository.findSakerByFnr(TestData.foedselsNr()).also {
                assertThat(it).isEmpty()
            }

            repository.delete(s)
        }
    }

    @Test
    fun relevanteTyper() {
        val fnr = TestData.foedselsNr()

        val relevanteTyper = setOf("S", "R", "K", "A")
        val urelevanteTyper = setOf("X", "Y", "Z")

        for(type in relevanteTyper + urelevanteTyper) {
            lagSak(
                fnr = fnr,
                kapittelNr = "FA",
                valg = "AE",
                type = type)
        }

        val res = repository.findSakerByFnr(fnr)
        assertThat(res.map { it.type }.toSet()).isEqualTo(relevanteTyper)
    }

    private fun lagSak(fnr: FoedselsNr, kapittelNr: String, valg: String, type: String, resultat: String? = null, sakStatus: SakStatus = SakStatus.IP): Sak {
        val snr = sakNr++.toString()

        val status = Status(
            id = nextId(),
            personKey = 123,
            saksblokk = "x",
            saksnummer = snr,
            status = sakStatus,
            lopeNr = 1
        )
        statusRepository.save(status)

        val sak = Sak(
            id = nextId(),
            fnr = fnr,
            personKey = 123,
            saksblokk = "x",
            saksnummer = snr,
            kapittelNr = kapittelNr,
            valg = valg,
            type = type,
            resultat = resultat,
            vedtaksdato = LocalDate.now(),
            iverksattdato = LocalDate.now(),
            statushistorikk = listOf(
                status
            ),
            registrert = LocalDate.now()
        )
        return repository.save(sak)
    }
}