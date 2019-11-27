package no.nav.infotrygd.foreldrepenger.repository

import no.nav.infotrygd.foreldrepenger.model.Status
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface StatusRepository : JpaRepository<Status, Long> {
}