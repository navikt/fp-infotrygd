package no.nav.infotrygd.foreldrepenger

import no.nav.infotrygd.foreldrepenger.utils.NaisFileIntoSystemPropertyInitializer
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.builder.SpringApplicationBuilder
import org.springframework.boot.context.properties.ConfigurationPropertiesScan

@SpringBootApplication
@ConfigurationPropertiesScan("no.nav.infotrygd.foreldrepenger")
class InfotrygdApplication

fun main(args: Array<String>) {
    System.clearProperty("logback.configurationFile")
    System.setProperty("oracle.jdbc.fanEnabled", "false")

    val vaultMountPath = "/var/run/secrets/nais.io/"
    SpringApplicationBuilder(InfotrygdApplication::class.java)
        .initializers(
            NaisFileIntoSystemPropertyInitializer("defaultds.url", vaultMountPath + "defaultDSconfig/jdbc_url"),
            NaisFileIntoSystemPropertyInitializer("defaultds.username", vaultMountPath + "defaultDS/username"),
            NaisFileIntoSystemPropertyInitializer("defaultds.password", vaultMountPath + "defaultDS/password")
        )
        .run(*args)
}
