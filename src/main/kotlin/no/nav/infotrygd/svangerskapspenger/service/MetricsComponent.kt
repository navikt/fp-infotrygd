package no.nav.infotrygd.svangerskapspenger.service

import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.Tag
import io.micrometer.core.instrument.Timer
import no.nav.infotrygd.svangerskapspenger.repository.PeriodeRepository
import no.nav.infotrygd.svangerskapspenger.repository.SakRepository
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.Duration
import java.time.LocalDate
import java.util.concurrent.atomic.AtomicLong

@Component
class MetricsComponent(
    private val registry: MeterRegistry,
    private val periodeRepository: PeriodeRepository,
    private val sakRepository: SakRepository
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    private val counts: MutableList<Count> = ArrayList()

    private var tom: List<LocalDate> = listOf()

    private var initialized = false



    @Scheduled(fixedRate = 60_000)
    fun update() {
        if(!initialized) {
            logger.info("Initializing metrics")
            initialize()
        }

        logger.debug("Updating metrics")
        counts.forEach { it.update() }

        updatePerioder()
    }

    private fun initialize() {
        count("num_saker_under_behandling") { sakRepository.countAapneSvangerskapssakerByType(setOf("S ", "R ")) }
        count("num_klagesaker") { sakRepository.countAapneSvangerskapssakerByType(setOf("K ")) }
        count("num_ankesaker") { sakRepository.countAapneSvangerskapssakerByType(setOf("A ")) }
        count("num_opne_saker_med_lopende_utbetaling") { periodeRepository.countOpneSakerMedLopendeUtbetaling() }
        count("num_avsluttede_saker") { periodeRepository.countAvsluttedeSaker(LocalDate.now().minusYears(1)) }

        for (i in 0 until 12) {
            count("num_opne_saker_stoppdato", Tag.of("month_diff", String.format("%02d", i))) {
                val now = LocalDate.now().plusMonths(i.toLong())
                tom.count { it.year == now.year && it.month == now.month }.toLong()
            }
        }

        initialized = true
    }

    private fun updatePerioder() {
        tom = periodeRepository.findOpneSakerMedLopendeUtbetaling().flatMap { periode ->
            periode.stoppdato?.let { listOf(it) } ?: listOf()
        }
    }

    private fun count(name: String, vararg tags: Tag, resolver: () -> Long) {
        val t = createTimer("time_$name")
        val c = Count(t, resolver)
        counts.add(c)
        registry.gauge(name, tags.toList(), c.value)
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