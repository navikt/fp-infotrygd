package no.nav.infotrygd.foreldrepenger.sikkerhet

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.config.BeanDefinition
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider
import org.springframework.core.annotation.AnnotatedElementUtils
import org.springframework.core.type.filter.AnnotationTypeFilter
import org.springframework.http.server.PathContainer
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.util.pattern.PathPatternParser
import java.lang.reflect.Method

/**
 * Sikrer at alle endepunkter eksplisitt tar stilling til autorisasjon, og at endepunkter
 * markert med [UnprotectedEndpoint] faktisk er åpnet i [SecurityConfiguration.UNPROTECTED_ENDPOINTS].
 * Uten dette vil et nytt endepunkt uten annotasjon stille kunne bli liggende ubeskyttet.
 */
class EndpointAuthorizationAnnotationTest {

    @Test
    fun alleEndepunkterErAnnotertMedEntraCCRequiredEllerNoAuthRequired() {
        val uannoterte = endpoints()
            .filter { !it.entraCCRequired && !it.unprotectedEndpoint }
            .map { it.beskrivelse }

        assertThat(uannoterte)
            .describedAs("Endepunkter må annoteres med @EntraCCRequired eller @UnprotectedEndpoint")
            .isEmpty()
    }

    @Test
    fun ingenEndepunkterErAnnotertMedBeggeAnnotasjonene() {
        val begge = endpoints()
            .filter { it.entraCCRequired && it.unprotectedEndpoint }
            .map { it.beskrivelse }

        assertThat(begge)
            .describedAs("Endepunkter kan ikke ha både @EntraCCRequired og @UnprotectedEndpoint")
            .isEmpty()
    }

    @Test
    fun unprotectedEndpointTillattISecurityConfiguration() {
        val ikkeTillatt = endpoints()
            .filter { it.unprotectedEndpoint }
            .filterNot { erUnntatt(it.path) }
            .map { it.beskrivelse }

        assertThat(ikkeTillatt)
            .describedAs("Endepunkt annotert med @UnprotectedEndpoint må finnes i SecurityConfiguration.UNPROTECTED_ENDPOINTS")
            .isEmpty()
    }

    @Test
    fun entraCCRequiredEndepunkterErIkkeTillattISecurityConfiguration() {
        val feilaktigTillatt = endpoints()
            .filter { it.entraCCRequired }
            .filter { erUnntatt(it.path) }
            .map { it.beskrivelse }

        assertThat(feilaktigTillatt)
            .describedAs("Endepunkt annotert med @EntraCCRequired kan ikke stå i SecurityConfiguration.UNPROTECTED_ENDPOINTS")
            .isEmpty()
    }

    @Test
    fun finnerFaktiskEndepunkteneSomSkalSjekkes() {
        // Uten denne ville testene over passert stilltiende dersom skanningen sluttet å finne endepunkter
        assertThat(endpoints().map { it.path })
            .contains("/grunnlag", "/sak", "/restanse", SecurityConfiguration.TABLES_INFO_ENDPOINT)
    }

    private data class Endpoint(
        val path: String,
        val entraCCRequired: Boolean,
        val unprotectedEndpoint: Boolean,
        val beskrivelse: String
    )

    private fun endpoints(): List<Endpoint> = controllerClasses().flatMap { controller ->
        val prefikser = AnnotatedElementUtils.findMergedAnnotation(controller, RequestMapping::class.java)
            ?.path?.takeIf { it.isNotEmpty() } ?: arrayOf("")

        controller.declaredMethods.flatMap { method ->
            paths(method).flatMap { path -> prefikser.map { prefiks -> prefiks + path } }
                .map { fullPath ->
                    Endpoint(
                        path = fullPath,
                        entraCCRequired = harAnnotasjon(method, controller, EntraCCRequired::class.java),
                        unprotectedEndpoint = harAnnotasjon(method, controller, UnprotectedEndpoint::class.java),
                        beskrivelse = "${controller.simpleName}.${method.name} [$fullPath]"
                    )
                }
        }
    }

    private fun paths(method: Method): List<String> =
        AnnotatedElementUtils.findMergedAnnotation(method, RequestMapping::class.java)
            ?.path?.toList().orEmpty()

    private fun <A : Annotation> harAnnotasjon(method: Method, controller: Class<*>, type: Class<A>): Boolean =
        AnnotatedElementUtils.hasAnnotation(method, type) || AnnotatedElementUtils.hasAnnotation(controller, type)

    private fun erUnntatt(path: String): Boolean {
        val container = PathContainer.parsePath(path)
        return SecurityConfiguration.UNPROTECTED_ENDPOINTS
            .any { PathPatternParser.defaultInstance.parse(it).matches(container) }
    }

    private fun controllerClasses(): List<Class<*>> {
        val scanner = ClassPathScanningCandidateComponentProvider(false)
        scanner.addIncludeFilter(AnnotationTypeFilter(Controller::class.java))
        return scanner.findCandidateComponents(BASE_PACKAGE)
            .mapNotNull(BeanDefinition::getBeanClassName)
            .map { Class.forName(it) }
    }

    companion object {
        private const val BASE_PACKAGE = "no.nav.infotrygd.foreldrepenger"
    }
}
