# fp-infotrygd

Service to read benefit data from Infotrygd replicas.

## Shared context

- Source of truth for shared domain, architecture, and conventions: `navikt/fp-context`
- Copilot Space: `navikt/TeamForeldrepenger`

## Repo-specific context

| Topic      | Details                                                                                                      |
|------------|--------------------------------------------------------------------------------------------------------------|
| Role       | Provides replicated Infotrygd data to FP services                                                            |
| Consumers  | `fp-sak`, `fp-abakus`                                                                                        |
| Tech stack | Kotlin with some Java; Spring Boot on Nais in FSS           |
| Security   | Entra ID client credentials. JWTs are validated by Spring Security  |
| Data       | Oracle read-only replicas of foreldrepenger, svangerskapspenger and sykepenger                              |

- Foreldrepenger and svangerskapspenger not updated. Used for looking up legacy cases in `fp-frontend` through `fp-sak`.
- Sykepenger is active and being updated. Used as data source for IAY in `fp-abakus`, and benefit overlap detection in `fp-sak`
- 3 separate deploys: one for each benefit/database.

## Entry points

- `InfotrygdController`: `postSak` only for `fp-sak` lookup, `postGrunnlag` for all benefits and consumers

## Verification

- Manual verification on deploy. `fp-autotest` is not relevant.
