# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project

Unofficial Android client for Pi-hole®. Single-module app (`:app`); the `wear/` directory is a placeholder.

## Common commands

```
./gradlew :app:assembleDebug              # build debug APK
./gradlew :app:assembleRelease            # release build (minify + R8)
./gradlew test                            # JVM unit tests
./gradlew :app:testDebugUnitTest --tests <FQN>   # single test
./gradlew connectedAndroidTest            # instrumented tests (needs device/emulator)
./gradlew spotlessApply                   # format Kotlin (ktfmt) — CI runs spotlessCheck
./gradlew :app:openApiGenerate            # regenerate API client from remote spec
bundle exec fastlane screenshots          # screengrab via the androidTest screenshot suite
bundle exec rubocop                       # lints fastlane/Ruby
```

CI (`.github/workflows/development.yml`) runs `spotlessCheck`, `rubocop`, and `fastlane development` on Java 25.

## Architecture

### API layer is code-generated from a remote OpenAPI spec
`app/build.gradle.kts` configures `openApiGenerate` to pull `https://raw.githubusercontent.com/pi-hole/FTL/master/.../specs/main.yaml` (the upstream spec on `master`) and emit Kotlin Multiplatform Ktor clients into `app/build/generated/source/open-api/debug/kotlin`. This is wired into the Variant API via an `OpenApiKotlinSources` task — do not edit generated code under `repository/apis/` or `repository/infrastructure/`. Patterns to exclude from generation live in `app/openapi-generator-ignore`.

### Multi-Pi-hole repository pattern
A user can register multiple Pi-hole connections. `PiHoleRepositoryManager` (Hilt-bound singleton) tracks the selected connection and exposes a `Flow<PiHoleRepository?>`. `PiHoleRepository` is `@AssistedInject`-constructed per-connection and aggregates all generated API clients with session/cookie auth handled in `authenticate()` / `login()`. Two Ktor `HttpClient`s are provided via the `@DefaultHttpClient` / `@TrustAllCertificatesHttpClient` qualifiers (the latter uses `NaiveTrustManager` for self-signed certs).

### ViewModel state pattern
ViewModels extend `BaseViewModel` and convert flows into UI state via `Flow<T>.asViewFlowState()`. This wraps the flow in a `LoadState<T>` (Idle/Loading/Success/Failure), registers it in a shared list so the screen's aggregate `loading` flag works, and routes thrown `Exception`s into the shared error channel which `SnackBarErrorEffect` surfaces. `refresh()` re-triggers every registered flow via a `MutableSharedFlow`. When adding a new fetched value to a ViewModel, expose it through `asViewFlowState()` rather than hand-rolling state — otherwise it won't participate in refresh or the unified loading indicator.

### Persistence via Protobuf DataStore
Two proto-backed `DataStore`s, defined in `app/src/main/proto/`:
- `user_preferences.proto` → app settings
- `pi_hole_connections.proto` → the connection map (per-id `PiHoleConfiguration` + cached `PiHoleSession`)

Serializers are in `data/`. Sessions persist between launches; `PiHoleRepository.authenticate()` reads the SID from the connection store and tries it before logging in.

### UI
Jetpack Compose + Material 3 + Hilt navigation Compose. Screens live under `ui/screen/<feature>/` as `<Feature>Screen.kt` + `<Feature>ViewModel.kt`. Charts use Vico (`ui/component/Chart.kt`); the line interpolator API changed in Vico 3.1 — use `LineCartesianLayer.Interpolator`, not the deprecated `PointConnector`.

**Strictly follow [Material 3](https://m3.material.io/) for every UI/UX decision.** Treat the official M3 guidelines as the source of truth for components, layout, spacing, typography, color/tonal schemes, elevation, shape, state layers, motion, and accessibility (touch targets, contrast). Prefer the `androidx.compose.material3` component that matches the M3 spec over a hand-rolled equivalent, and pull values from the `MaterialTheme` (`colorScheme`, `typography`, `shapes`) rather than hardcoding. When the spec is ambiguous or a pattern isn't covered, consult https://m3.material.io/ before improvising and keep the choice consistent with the rest of the M3 system.

## Build conventions

- **AGP 9 + built-in Kotlin**: the `kotlin-android` plugin is intentionally not applied — AGP provides Kotlin support directly. The `kotlin {}` DSL is on `Project`, so configure compiler options at the top level (`kotlin { compilerOptions { … } }`), not inside `android {}` (the latter only works via outer-lambda capture and triggers an IDE warning).
- **Kotlin 2.3** with `-Xannotation-default-target=param-property` opted in — Hilt `@Qualifier` annotations on constructor `val` params apply to both parameter and property.
- **KSP only** for Hilt; there is no `kapt`.
- **Spotless** with `ktfmt(...).kotlinlangStyle()` is enforced on `**/*.kt` and `**/*.gradle.kts`. Run `spotlessApply` before committing.
- Locales are filtered to `en, de, pl, ro` in `androidResources.localeFilters`.
