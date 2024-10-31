package no.nav.infotrygd.foreldrepenger.repository

import no.nav.commons.foedselsnummer.FoedselsNr
import no.nav.infotrygd.foreldrepenger.model.Sak
import no.nav.infotrygd.foreldrepenger.model.kodeverk.*
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
import java.time.LocalDate

@ExtendWith(SpringExtension::class)
@DataJpaTest
@ActiveProfiles("test")
class SakRepositoryTest {
    @Autowired
    lateinit var repository: SakRepository

    var sakNr = 1

    @BeforeEach
    fun setUp() {
        repository.deleteAll()
    }

    @Test
    fun findFpSakerByFnr() {

        for(valg in SakValg.entries - listOf(SakValg.SVP)) {
            val fnr = TestData.foedselsNr()
            val s = lagSak(
                    fnr = fnr,
                    kapittelNr = "FA",
                    valg = valg,
                    type = SakType.S)


            repository.findSakerByFnrAndValg(fnr, SakRepository.valgFp).also {
                assertThat(it).hasSize(1)
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
        lagSak(
                fnr = fnr,
                kapittelNr = "FA",
                valg = SakValg.SVP,
                type = SakType.S)


        repository.findSakerByFnrAndValg(fnr, SakRepository.valgSvp).also {
            assertThat(it).hasSize(1)
        }

        repository.findSakerByFnrAndValg(TestData.foedselsNr(), SakRepository.valgSvp).also {
            assertThat(it).isEmpty()
        }
    }

    @Test
    fun relevanteTyperFp() {
        val fnr = TestData.foedselsNr()

        val relevanteTyper = setOf(SakType.S, SakType.R, SakType.K, SakType.A)

        for(type in relevanteTyper) {
            lagSak(
                    fnr = fnr,
                    kapittelNr = "FA",
                    valg = SakValg.ES_A,
                    type = type)
        }

        val res = repository.findSakerByFnrAndValg(fnr, SakRepository.valgFp)
        assertThat(res.map { it.type }.toSet()).isEqualTo(relevanteTyper)
    }

    @Test
    fun relevanteTyperSvp() {
        val fnr = TestData.foedselsNr()

        val relevanteTyper = setOf(SakType.S, SakType.R, SakType.K, SakType.A)

        for(type in relevanteTyper) {
            lagSak(
                    fnr = fnr,
                    kapittelNr = "FA",
                    valg = SakValg.SVP,
                    type = type)
        }

        val res = repository.findSakerByFnrAndValg(fnr, SakRepository.valgSvp)
        assertThat(res.map { it.type }.toSet()).isEqualTo(relevanteTyper)
    }

    private fun lagSak(fnr: FoedselsNr, kapittelNr: String, valg: SakValg, type: SakType, resultat: SakResultat? = SakResultat.ÅPEN): Sak {
        val snr = sakNr++.toString()

        val sak = Sak(
            id = nextId(),
            fnr = fnr,
            saksblokk = "x",
            saksnummer = snr,
            kapittelNr = kapittelNr,
            valg = valg,
            undervalg = SakUndervalg.UKJENT,
            nivaa = SakNivaa.TK,
            type = type,
            resultat = resultat?: SakResultat.ÅPEN,
            vedtaksdato = LocalDate.now(),
            iverksattdato = LocalDate.now(),
            registrert = LocalDate.now(),
            mottatt = LocalDate.now(),
            reellEnhet = "4867"
        )
        return repository.save(sak)
    }
}