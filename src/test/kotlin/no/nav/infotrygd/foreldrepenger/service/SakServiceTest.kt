package no.nav.infotrygd.foreldrepenger.service

import no.nav.infotrygd.foreldrepenger.Profiler
import no.nav.infotrygd.foreldrepenger.model.Sak
import no.nav.infotrygd.foreldrepenger.model.kodeverk.*
import no.nav.infotrygd.foreldrepenger.nextId
import no.nav.infotrygd.foreldrepenger.repository.SakRepository
import no.nav.infotrygd.foreldrepenger.rest.dto.SakDto
import no.nav.infotrygd.foreldrepenger.rest.dto.SakId
import no.nav.infotrygd.foreldrepenger.rest.dto.SakKodeverdi
import no.nav.infotrygd.foreldrepenger.testutil.TestData
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles
import java.time.LocalDate

@DataJpaTest
@ActiveProfiles(Profiler.TEST)
@Import(SakService::class, ApplicationUtil::class)
internal class SakServiceTest {
    private val fnr = TestData.foedselsNr()

    @Autowired
    lateinit var sakRepository: SakRepository

    @Autowired
    lateinit var applicationUtil: ApplicationUtil

    val sakService: SakService
        get() = SakService(sakRepository, applicationUtil)

    @Test
    fun saker() {
        val sak = Sak(
            id = nextId(),
            fnr = fnr,
            saksblokk = "x",
            saksnummer = "11",
            kapittelNr = "FA",
            valg = SakValg.FP_F,
            undervalg = SakUndervalg.UKJENT,
            nivaa = SakNivaa.TK,
            type = SakType.S,
            resultat = SakResultat.I,
            vedtaksdato = LocalDate.now(),
            iverksattdato = LocalDate.now(),
            registrert = LocalDate.now(),
            mottatt = LocalDate.now(),
            reellEnhet = "4867",
            behandlendeEnhet = "4867"
        )

        sakRepository.save(sak)

        val res = sakService.findSakerByFnr(fnr, LocalDate.now().minusYears(1), LocalDate.now())

        val forventet = listOf(
            SakDto(
                sakId = SakId(sak.saksblokk, sak.saksnummer.toInt()),
                type = SakKodeverdi(SakType.S.kode, SakType.S.tekst),
                resultat = SakKodeverdi(SakResultat.I.kode, SakResultat.I.tekst),
                valg = SakKodeverdi(SakValg.FP_F.kode, SakValg.FP_F.tekst),
                undervalg = SakKodeverdi(SakUndervalg.UKJENT.kode, SakUndervalg.UKJENT.tekst),
                nivaa = SakKodeverdi(SakNivaa.TK.kode, SakNivaa.TK.tekst),
                vedtatt = LocalDate.now(),
                iverksatt = LocalDate.now(),
                registrert = LocalDate.now(),
                mottatt = LocalDate.now()
            )
        )

        assertThat(res).isEqualTo(forventet)
    }


}