package no.nav.infotrygd.svangerskapspenger.repository

import no.nav.infotrygd.svangerskapspenger.model.Sak
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.TestPropertySource
import org.springframework.test.context.junit4.SpringRunner
import java.time.LocalDate

@RunWith(SpringRunner::class)
@DataJpaTest
@TestPropertySource(properties = [
    "spring.jpa.hibernate.ddl-auto=validate",
    "spring.datasource.initialization-mode=always",
    "spring.datasource.platform=h2",
    "spring.datasource.url=jdbc:h2:mem:testdb:MODE=Oracle",
    "spring.jpa.properties.hibernate.default_schema=INFOTRYGD_Q0"
])
class SakRepositoryTest {
    @Autowired
    lateinit var repository: SakRepository

    @Test
    fun findSvangerskapssakerByFnrAndType() {
        lagSak(
            fnr = "123",
            kapittelNr = "xx",
            valg = "xx",
            type = "xx")

        repository.findSvangerskapssakerByFnrAndType("123", setOf("xx")).also {
            assertThat(it.isEmpty())
        }

        lagSak(
            fnr = "123",
            kapittelNr = "FA",
            valg = "SV",
            type = "S")

        repository.findSvangerskapssakerByFnrAndType("123", setOf("S")).also {
            assertThat(it).hasSize(1)
        }

        repository.findSvangerskapssakerByFnrAndType("987", setOf("S")).also {
            assertThat(it).isEmpty()
        }
    }

    private fun lagSak(fnr: String, kapittelNr: String, valg: String, type: String) {
        val sak = Sak(
            fnr = fnr,
            personKey = 123,
            saksblokk = "x",
            saksnummer = "xx",
            kapittelNr = kapittelNr,
            valg = valg,
            type = type,
            resultat = "xx",
            vedtaksdato = LocalDate.now(),
            iverksattdato = LocalDate.now()
        )
        repository.save(sak)
    }
}