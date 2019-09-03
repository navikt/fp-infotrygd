package no.nav.infotrygd.svangerskapspenger.repository

import no.nav.infotrygd.svangerskapspenger.model.Sak
import no.nav.infotrygd.svangerskapspenger.model.Status
import no.nav.infotrygd.svangerskapspenger.nextId
import no.nav.infotrygd.svangerskapspenger.values.FodselNr
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
    fun findSvangerskapssakerByFnrAndType() {
        lagSak(
            fnr = "10000000001",
            kapittelNr = "xx",
            valg = "xx",
            type = "xx")

        repository.findSvangerskapssakerByFnrAndType(FodselNr("10000000001"), setOf("xx")).also {
            assertThat(it.isEmpty())
        }

        lagSak(
            fnr = "10000000001",
            kapittelNr = "FA",
            valg = "SV",
            type = "S")


        repository.findSvangerskapssakerByFnrAndType(FodselNr("10000000001"), setOf("S")).also {
            assertThat(it).hasSize(1)
            assertThat(it[0].status).hasSize(1)
            assertThat(it[0].status[0].status).isEqualTo("A")
        }

        repository.findSvangerskapssakerByFnrAndType(FodselNr("10000000002"), setOf("S")).also {
            assertThat(it).isEmpty()
        }
    }

    @Test
    fun countSvangerskapssakerByType() {
        lagSak(
            fnr = "10000000001",
            kapittelNr = "FA",
            valg = "SV",
            type = "S")
        assertThat(repository.countSvangerskapssakerByType(setOf("S"))).isEqualTo(1)
        assertThat(repository.countSvangerskapssakerByType(setOf("X"))).isEqualTo(0)
    }

    private fun lagSak(fnr: String, kapittelNr: String, valg: String, type: String) {
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
            fnr = FodselNr(fnr),
            personKey = 123,
            saksblokk = "x",
            saksnummer = snr,
            kapittelNr = kapittelNr,
            valg = valg,
            type = type,
            resultat = "xx",
            vedtaksdato = LocalDate.now(),
            iverksattdato = LocalDate.now(),
            status = listOf(
                status
            )
        )
        repository.save(sak)
    }
}