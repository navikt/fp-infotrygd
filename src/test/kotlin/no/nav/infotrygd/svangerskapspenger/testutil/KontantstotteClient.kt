package no.nav.infotrygd.svangerskapspenger.testutil

import org.springframework.web.reactive.function.client.WebClient

fun kontantstotteClient(port: Int): WebClient {
    return WebClient.builder()
        .baseUrl("http://localhost:$port")
        .build()
}