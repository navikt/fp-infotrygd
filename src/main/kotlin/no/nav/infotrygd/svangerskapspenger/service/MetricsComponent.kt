package no.nav.infotrygd.svangerskapspenger.service

import io.micrometer.core.annotation.Timed
import io.micrometer.core.instrument.MeterRegistry
import no.nav.infotrygd.svangerskapspenger.repository.PeriodeRepository
import no.nav.infotrygd.svangerskapspenger.repository.SakRepository
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.LocalDate
import javax.annotation.PostConstruct

@Component
class MetricsComponent(
    private val registry: MeterRegistry,
    private val periodeRepository: PeriodeRepository,
    private val sakRepository: SakRepository
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    private val timerSakerUnderBehandling = registry.timer("time_num_saker_under_behandling")
    private val timerKlagesaker = registry.timer("time_num_klagesaker")
    private val timerAnkesaker = registry.timer("time_num_ankesaker")

    private val timerOpneSakerMedLopendeUtbetaling = registry.timer("time_num_opne_saker_med_lopende_utbetaling")
    private val timerAvsluttedeSaker = registry.timer("time_num_avsluttede_saker")

    @Timed(value = "table_metrics", description = "Tiden det tar å samle metrics for tabellene")
    @Scheduled(fixedRate = 60_000)
    @PostConstruct
    fun tableMetrics() {
        logger.info("Samler metrics om relevante data")

        val sakerUnderBehandling : Long = timerSakerUnderBehandling.recordCallable {
            sakRepository.countSvangerskapssakerByType(setOf("S ", "R "))
        }

        val klagesaker = timerKlagesaker.recordCallable {
            sakRepository.countSvangerskapssakerByType(setOf("K "))
        }

        val ankesaker = timerAnkesaker.recordCallable {
            sakRepository.countSvangerskapssakerByType(setOf("A "))
        }

        val opneSakerMedLopendeUtbetaling = timerOpneSakerMedLopendeUtbetaling.recordCallable {
            periodeRepository.countOpneSakerMedLopendeUtbetaling()
        }

        val avsluttedeSaker = timerAvsluttedeSaker.recordCallable {
            periodeRepository.countAvsluttedeSaker(LocalDate.now().minusYears(1))
        }

        registry.gauge("num_saker_under_behandling", sakerUnderBehandling)
        registry.gauge("num_klagesaker", klagesaker)
        registry.gauge("num_ankesaker", ankesaker)
        registry.gauge("num_opne_saker_med_lopende_utbetaling", opneSakerMedLopendeUtbetaling)
        registry.gauge("num_avsluttede_saker", avsluttedeSaker)
    }
}