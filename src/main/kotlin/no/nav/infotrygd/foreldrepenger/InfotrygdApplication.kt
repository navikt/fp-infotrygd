package no.nav.infotrygd.foreldrepenger

import no.nav.infotrygd.foreldrepenger.utils.NaisFileIntoSystemPropertyInitializer
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.builder.SpringApplicationBuilder
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableScheduling
class InfotrygdApplication

fun main(args: Array<String>) {
    System.setProperty("oracle.jdbc.fanEnabled", "false")

    val vaultMountPath = "/var/run/secrets/nais.io/"
    SpringApplicationBuilder(InfotrygdApplication::class.java)
        .initializers(
            NaisFileIntoSystemPropertyInitializer("DEFAULTDS_URL", vaultMountPath + "defaultDSconfig/jdbc_url"),
            NaisFileIntoSystemPropertyInitializer("DEFAULTDS_USERNAME", vaultMountPath + "defaultDS/password"),
            NaisFileIntoSystemPropertyInitializer("DEFAULTDS_PASSWORD", vaultMountPath + "defaultDS/username")
        )
        .run(*args)
}
