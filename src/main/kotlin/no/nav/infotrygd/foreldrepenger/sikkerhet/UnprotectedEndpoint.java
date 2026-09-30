package no.nav.infotrygd.foreldrepenger.sikkerhet;


import java.lang.annotation.*;

@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface UnprotectedEndpoint {
}
