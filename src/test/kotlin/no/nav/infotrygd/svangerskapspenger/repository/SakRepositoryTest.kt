package no.nav.infotrygd.svangerskapspenger.repository

import no.nav.commons.foedselsnummer.FoedselsNr
import no.nav.infotrygd.svangerskapspenger.model.Sak
import no.nav.infotrygd.svangerskapspenger.model.Status
import no.nav.infotrygd.svangerskapspenger.nextId
import no.nav.infotrygd.svangerskapspenger.testutil.TestData
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

    @Before
    fun setUp() {
        repository.deleteAll()
    }

    @Test
    fun findSvangerskapssakerByFnr() {
        val fnr = TestData.foedselsNr()

        lagSak(
            fnr = fnr,
            kapittelNr = "FA",
            valg = "SV",
            type = "S")


        repository.findSvangerskapssakerByFnr(fnr).also {
            assertThat(it).hasSize(1)
            assertThat(it[0].status).hasSize(1)
            assertThat(it[0].status[0].status).isEqualTo("A")
        }

        repository.findSvangerskapssakerByFnr(TestData.foedselsNr()).also {
            assertThat(it).isEmpty()
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
                valg = "SV",
                type = type)
        }

        val res = repository.findSvangerskapssakerByFnr(fnr)
        assertThat(res.map { it.type }.toSet()).isEqualTo(relevanteTyper)
    }

    @Test
    fun countSvangerskapssakerByType() {
        lagSak(
            fnr = TestData.foedselsNr(),
            kapittelNr = "FA",
            valg = "SV",
            type = "S",
            resultat = "SB")
        assertThat(repository.countAapneSvangerskapssakerByType(setOf("S"))).isEqualTo(1)
        assertThat(repository.countAapneSvangerskapssakerByType(setOf("X"))).isEqualTo(0)
    }

    private fun lagSak(fnr: FoedselsNr, kapittelNr: String, valg: String, type: String, resultat: String = "") {
        val snr = sakNr++.toString()

        val status = Status(
            id = nextId(),
            personKey = 123,
            saksblokk = "x",
            saksnummer = snr,
            status = "A",
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
            status = listOf(
                status
            ),
            registrert = LocalDate.now()
        )
        repository.save(sak)
    }
}