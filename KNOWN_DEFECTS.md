# connie — Known defects

Bugs of connie (connie-utils, the expression language and its parsers) that are known and not fixed yet. Each entry says what is verified, how to reproduce it, and how to work around it. Features and evolutions go to `BACKLOG.md`, not here.

Identifiers are stable and never reused: `CONNIE-D-<n>`. Reference them from commits and from other entries.

Status legend: `TODO` · `IN PROGRESS` · `BLOCKED` · `DONE` · `DEFERRED`.

---

## Resource location

### CONNIE-D-1 — On a Jenkins agent, source-code resources are looked up in the workspaces of other jobs  ·  `TODO`

**Symptom.** The tests of `openflexo-core-2.99` read the localization of `flexo-foundation` from the checkout of **another job**, on the 3.0 branch. Every test suite of `flexo-foundation-test` logs:

    Main localization directory: [SourceCodeResourceLocatorImpl]file:/home/jenkins/agent/agent/workspace/openflexo-core-3.0/flexo-foundation/src/main/resources/FlexoLocalization/General/

**Measured (2026-10-13/14).** In the test reports of `openflexo-core-2.99` #149 to #160, 98 to 100 suites per build give that line (the others give none), in green and in red builds alike: it was **not** the cause of the intermittent failures investigated then (CORE-D-38, CORE-D-39). Locally the same lookup resolves to `openflexo-core` itself.

**Mechanism, read in the code (`connie-utils`, `org.openflexo.rm`).**
1. `SourceCodeResourceLocatorImpl.getDirectoriesSearchOrder()` finds the git root above `user.dir`, then **climbs one level** ("to handle multiple repositories") and adds the `src/{main,test,dev}/resources` directories of every directory there holding a `build.gradle` or a `pom.xml`, recursively. In the `GIT-2.99` workspace that level is the set of repositories, as intended; on a Jenkins agent it is `/home/jenkins/agent/agent/workspace`, which holds the checkout of **every job** run on that agent, on every branch — and the half-deleted `*_ws-cleanup_*` copies of the deferred workspace wipeouts.
2. `FileSystemResourceLocatorImpl.locateResource(String)` keeps the **first** file found along that list (`locateAllFiles(...).get(0)`), and the order of the list is the order of `File.listFiles()`: sorted on macOS, the order of the directory entries on Linux. On `of-jenkins-runner-1`, `openflexo-core-3.0` comes before `openflexo-core-2.99`.

**Consequences.**
- A test of one branch reads resources — localization, and whatever else is located as source code — of another branch, which may hold different keys or be changing under a concurrent build of that other job.
- The localization service runs with automatic saving (`DefaultLocalizationService.automaticSaving = true`, `FlexoMainLocalizer` in dev mode): a missing key is added and the dictionaries are written back after 3 s, by a `SaveLocales` thread, into **that other checkout**. Not observed — Jenkins keeps only ~1 KB of the output of a passing suite — but nothing prevents it.

**Reproduction.** Any build of `openflexo-core-2.99` on `of-jenkins-runner-1` while `openflexo-core-3.0` has a workspace there: read `Main localization directory` in the output of a failing suite, or in the test report of a passing one (it is in its first kilobyte).

**Options.**
- Do not climb above the git root when running outside a development workspace — for instance only when the parent of the git root is not a Jenkins workspace directory, or under an explicit system property set by the development setup.
- Or, among several matches, prefer the one closest to `user.dir`, as `SourceCodeResourceLocatorImpl.locateFile(relativePathName, regexFilter)` already does for its filtered lookups.
- And, in any case, do not save localization dictionaries during tests.

**Acceptance.** On the agent, the tests of `openflexo-core-2.99` log a localization directory inside the `openflexo-core-2.99` workspace; no test writes into another job's workspace.

---

## Class information

### CONNIE-D-2 — `ClassScope` cannot list classes on Java 9+: `LoadedClassesInfo` throws  ·  `DONE`

**Symptom (Java 17).** `LoadedClassesInfo.getInstance()` throws `RuntimeException: ClassScope::getLoadedClasses() cannot be used in this JRE` (caused by `NoSuchFieldException` in `ClassScope.<clinit>`). It is reached from `TypeMustBeResolved` (`flexo-foundation`), the fix proposal for an unresolved type.

**Cause.** Same as `GINA-D-1`: `ClassScope` read the private field `ClassLoader.classes`, which no longer exists. `connie-core` holds a copy of the GINA classes taken in January 2025 (commit `54bcb5f`), without the Swing icons.

**Fix.** Same as `GINA-D-1`: `ClassScope.getAvailableClassNames(loaders)` scans the classpath, the loaders' URLs and the `java.*` / `javax.*` runtime image; `LoadedClassesInfo.search` scans lazily and loads only the matches; `getLoadedClasses` returns an empty array instead of throwing. Test: `ClassScopeTest` in `connie-core` (4 failures before, 5 successes after).

**Still duplicated.** `ClassScope` is byte for byte the one of `gina-swing`, and `LoadedClassesInfo` differs only by the icons. Ending the migration started in 2025 is `CONNIE-F-1`.
