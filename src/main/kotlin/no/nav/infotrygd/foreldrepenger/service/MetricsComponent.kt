package no.nav.infotrygd.foreldrepenger.service

import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.Tag
import io.micrometer.core.instrument.Timer
import no.nav.infotrygd.foreldrepenger.repository.PeriodeRepository
import no.nav.infotrygd.foreldrepenger.repository.SakRepository
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
        count("infotrygd_foreldrepenger_num_saker_under_behandling") { sakRepository.countAapneSakerByType(setOf("S ", "R ")) }
        count("infotrygd_foreldrepenger_num_klagesaker") { sakRepository.countAapneSakerByType(setOf("K ")) }
        count("infotrygd_foreldrepenger_num_ankesaker") { sakRepository.countAapneSakerByType(setOf("A ")) }
        count("infotrygd_foreldrepenger_num_opne_saker_med_lopende_utbetaling") { periodeRepository.countByFrisk(PeriodeRepository.lopende) }
        count("infotrygd_foreldrepenger_num_avsluttede_saker") { periodeRepository.countByFrisk(PeriodeRepository.avsluttede) }

        for (i in 0 until 12) {
            count("infotrygd_foreldrepenger_num_opne_saker_stoppdato", Tag.of("month_diff", String.format("%02d", i))) {
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