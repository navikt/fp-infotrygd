package no.nav.infotrygd.foreldrepenger.repository

import no.nav.commons.foedselsnummer.FoedselsNr
import no.nav.infotrygd.foreldrepenger.model.Sak
import no.nav.infotrygd.foreldrepenger.model.Status
import no.nav.infotrygd.foreldrepenger.model.kodeverk.SakStatus
import no.nav.infotrygd.foreldrepenger.nextId
import no.nav.infotrygd.foreldrepenger.testutil.TestData
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.math.BigInteger
import java.time.LocalDate

@ExtendWith(SpringExtension::class)
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

    @BeforeEach
    fun setUp() {
        repository.deleteAll()
    }

    @Test
    fun findFpSakerByFnr() {

        for(valg in alleValg) {
            val fnr = TestData.foedselsNr()
            val status = SakStatus.FB
            val s = lagSak(
                fnr = fnr,
                kapittelNr = "FA",
                valg = valg,
                type = "S",
                sakStatus = status)


            repository.findSakerByFnrAndValg(fnr, SakRepository.valgFp).also {
                assertThat(it).hasSize(1)
                assertThat(it[0].statushistorikk).hasSize(1)
                assertThat(it[0].status).isEqualTo(status)
            }

            repository.findSakerByFnrAndValg(TestData.foedselsNr(), SakRepository.valgFp).also {
                assertThat(it).isEmpty()
            }

            repository.delete(s)
        }
    }

    @Test
    fun findSvpSakerByFnr() {
        val fnr = TestData.foedselsNr()
        val status = SakStatus.IP
        lagSak(
            fnr = fnr,
            kapittelNr = "FA",
            valg = "SV",
            type = "S",
            sakStatus = status)


        repository.findSakerByFnrAndValg(fnr, SakRepository.valgSvp).also {
            assertThat(it).hasSize(1)
            assertThat(it[0].statushistorikk).hasSize(1)
            assertThat(it[0].statushistorikk[0].status).isEqualTo(status)
        }

        repository.findSakerByFnrAndValg(TestData.foedselsNr(), SakRepository.valgSvp).also {
            assertThat(it).isEmpty()
        }
    }

    @Test
    fun relevanteTyperFp() {
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

        val res = repository.findSakerByFnrAndValg(fnr, SakRepository.valgFp)
        assertThat(res.map { it.type }.toSet()).isEqualTo(relevanteTyper)
    }

    @Test
    fun relevanteTyperSvp() {
        val fnr = TestData.foedselsNr()

        val relevanteTyper = setOf("S", "R", "K", "A")
        val urelevanteTyper = setOf("X", "Y", "Z")

        for(type in relevanteTyper + urelevanteTyper) {
            lagSak(
                fnr = fnr,
                kapittelNr = "FA",
                valg = "SV",
                type = type)
        }

        val res = repository.findSakerByFnrAndValg(fnr, SakRepository.valgSvp)
        assertThat(res.map { it.type }.toSet()).isEqualTo(relevanteTyper)
    }

    private fun lagSak(fnr: FoedselsNr, kapittelNr: String, valg: String, type: String, resultat: String? = null, sakStatus: SakStatus = SakStatus.IP): Sak {
        val snr = sakNr++.toString()

        val status = Status(
            id = nextId(),
            personKey = BigInteger.valueOf(123),
            saksblokk = "x",
            saksnummer = snr,
            status = sakStatus,
            lopeNr = BigInteger.ONE
        )
        statusRepository.save(status)

        val sak = Sak(
            id = nextId(),
            fnr = fnr,
            personKey = BigInteger.valueOf(123),
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