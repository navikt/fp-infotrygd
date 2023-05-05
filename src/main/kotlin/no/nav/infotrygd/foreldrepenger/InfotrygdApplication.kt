package no.nav.infotrygd.foreldrepenger

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableScheduling
class InfotrygdApplication

fun main(args: Array<String>) {
    System.setProperty("oracle.jdbc.fanEnabled", "false")
    System.setProperty("spring.devtools.restart.enabled", "false")
    runApplication<InfotrygdApplication>(*args)
}
