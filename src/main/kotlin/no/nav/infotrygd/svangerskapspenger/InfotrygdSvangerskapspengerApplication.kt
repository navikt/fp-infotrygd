package no.nav.infotrygd.svangerskapspenger

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableScheduling
class InfotrygdSvangerskapspengerApplication

fun main(args: Array<String>) {
    runApplication<InfotrygdSvangerskapspengerApplication>(*args)
}
