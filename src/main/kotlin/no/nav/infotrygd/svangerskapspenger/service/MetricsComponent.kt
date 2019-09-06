package no.nav.infotrygd.svangerskapspenger.service

import io.micrometer.core.instrument.Gauge
import io.micrometer.core.instrument.MeterRegistry
import no.nav.infotrygd.svangerskapspenger.repository.PeriodeRepository
import no.nav.infotrygd.svangerskapspenger.repository.SakRepository
import org.slf4j.LoggerFactory
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

    @PostConstruct
    fun tableMetrics() {
        Gauge.builder("num_saker_under_behandling") {
            timerSakerUnderBehandling.recordCallable {
                sakRepository.countSvangerskapssakerByType(setOf("S ", "R "))
            }
        }.register(registry)

        Gauge.builder("num_klagesaker") {
            timerKlagesaker.recordCallable {
                sakRepository.countSvangerskapssakerByType(setOf("K "))
            }
        }.register(registry)

        Gauge.builder("num_ankesaker") {
            timerAnkesaker.recordCallable {
                sakRepository.countSvangerskapssakerByType(setOf("A "))
            }
        }.register(registry)

        Gauge.builder("num_opne_saker_med_lopende_utbetaling") {
            timerOpneSakerMedLopendeUtbetaling.recordCallable {
                periodeRepository.countOpneSakerMedLopendeUtbetaling()
            }
        }.register(registry)

        Gauge.builder("num_avsluttede_saker") {
            timerAvsluttedeSaker.recordCallable {
                periodeRepository.countAvsluttedeSaker(LocalDate.now().minusYears(1))
            }
        }.register(registry)
    }
}