package no.nav.infotrygd.foreldrepenger.rest.controller

import no.nav.infotrygd.foreldrepenger.integration.TableIntegrator
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import jakarta.transaction.Transactional
import no.nav.infotrygd.foreldrepenger.sikkerhet.UnprotectedEndpoint
import no.nav.infotrygd.foreldrepenger.sikkerhet.SecurityConfiguration.TABLES_INFO_ENDPOINT

@RestController
@Transactional
class TableController(private val tableIntegrator: TableIntegrator) {

    @UnprotectedEndpoint
    @GetMapping(path = [TABLES_INFO_ENDPOINT])
    fun get(): Map<String, List<String>> {
        return tableIntegrator.tables
    }

}