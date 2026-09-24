package no.nav.infotrygd.foreldrepenger.rest.controller

import no.nav.infotrygd.foreldrepenger.integration.TableIntegrator
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import jakarta.transaction.Transactional
import no.nav.infotrygd.foreldrepenger.config.SecurityConfiguration.TABLES_DEFINITIONS_ENDPOINT

@RestController
@Transactional
class TableController(private val tableIntegrator: TableIntegrator) {

    @GetMapping(path = [TABLES_DEFINITIONS_ENDPOINT])
    fun get(): Map<String, List<String>> {
        return tableIntegrator.tables
    }

}