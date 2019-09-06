package no.nav.infotrygd.svangerskapspenger.service

import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.Timer
import no.nav.infotrygd.svangerskapspenger.repository.PeriodeRepository
import no.nav.infotrygd.svangerskapspenger.repository.SakRepository
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.Duration
import java.time.LocalDate
import java.util.concurrent.atomic.AtomicLong
import javax.annotation.PostConstruct

@Component
class MetricsComponent(
    private val registry: MeterRegistry,
    private val periodeRepository: PeriodeRepository,
    private val sakRepository: SakRepository
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    private val counts: MutableList<Count> = ArrayList()

    @PostConstruct
    fun tableMetrics() {
        count("num_saker_under_behandling") { sakRepository.countSvangerskapssakerByType(setOf("S ", "R ")) }
        count("num_klagesaker") { sakRepository.countSvangerskapssakerByType(setOf("K ")) }
        count("num_ankesaker") { sakRepository.countSvangerskapssakerByType(setOf("A ")) }
        count("num_opne_saker_med_lopende_utbetaling") { periodeRepository.countOpneSakerMedLopendeUtbetaling() }
        count("num_avsluttede_saker") { periodeRepository.countAvsluttedeSaker(LocalDate.now().minusYears(1)) }
    }

    @Scheduled(fixedRate = 60_000)
    fun update() {
        logger.info("Updating metrics")
        counts.forEach { it.update() }
    }

    private fun count(name: String, resolver: () -> Long) {
        val t = createTimer("time_$name")
        val c = Count(t, resolver)
        counts.add(c)
        registry.gauge(name, c.value)
    }

    private fun createTimer(name: String): Timer =
        Timer.builder(name)
            .publishPercentiles(0.5, 0.95)
            .minimumExpectedValue(Duration.ofMillis(1))
            .maximumExpectedValue(Duration.ofMinutes(10))
            .register(registry)

    class Count(private val timer: Timer, private val resolver: () -> Long) {
        val value: AtomicLong = AtomicLong(resolve())

        fun update() {
            value.set(resolve())
        }

        private fun resolve() = timer.recordCallable { resolver() }
    }
}