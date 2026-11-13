# connie — Fixed defects

Defects of connie that are fixed: entries moved from [`KNOWN_DEFECTS.md`](./KNOWN_DEFECTS.md) once their status is `DONE`,
with what was verified, the cause and the fix. Identifiers are those of `KNOWN_DEFECTS.md`, never reused.

---

## Class information

### CONNIE-D-2 — `ClassScope` cannot list classes on Java 9+: `LoadedClassesInfo` throws  ·  `DONE`

**Symptom (Java 17).** `LoadedClassesInfo.getInstance()` throws `RuntimeException: ClassScope::getLoadedClasses() cannot be used in this JRE` (caused by `NoSuchFieldException` in `ClassScope.<clinit>`). It is reached from `TypeMustBeResolved` (`flexo-foundation`), the fix proposal for an unresolved type.

**Cause.** Same as `GINA-D-1`: `ClassScope` read the private field `ClassLoader.classes`, which no longer exists. `connie-core` holds a copy of the GINA classes taken in January 2025 (commit `54bcb5f`), without the Swing icons.

**Fix.** Same as `GINA-D-1`: `ClassScope.getAvailableClassNames(loaders)` scans the classpath, the loaders' URLs and the `java.*` / `javax.*` runtime image; `LoadedClassesInfo.search` scans lazily and loads only the matches; `getLoadedClasses` returns an empty array instead of throwing. Test: `ClassScopeTest` in `connie-core` (4 failures before, 5 successes after).

**Still duplicated.** `ClassScope` is byte for byte the one of `gina-swing`, and `LoadedClassesInfo` differs only by the icons. Ending the migration started in 2025 is `CONNIE-F-1`.

---

## Expressions

### CONNIE-D-3 — The text of a binding path stays the old one after a renamed element  ·  `DONE`

**Symptom.** `BindingPath.toString()` (hence `DataBinding.toString()`) kept the name a function or a property had when the text was first
computed: `new ConceptB::constructor1()` stayed so after the constructor was renamed, in FML (CORE-D-67 of openflexo-core).

**Cause (verified 2026-11-13).** The path caches its text and forgets it when it is invalidated, when its binding variable changes, or when an
element tells it. An element can only tell the path that activated it (`BindingPathElement.getBindingPath()`), and an element is activated
when the path is validated: before that, or for a clone that shares the elements of another path, nothing could reach the cache. (The FML
elements did not tell their path either: fixed in openflexo-core.)

**Fix.** `BindingPath.toString()` keeps its text only while every element is activated by this path; otherwise it computes it each time.
Test: `TestRenameInExpressions` in `openflexo-core/flexo-foundation-test`.

**Measured (2026-11-13).** On the 254 paths of the 32 `FMLParsingExamples` units, 60 (24 %) are not cached (their elements are
shared or activated by another path): `hashCode()` costs about 420 ns on average, 180-230 ns with a forced cache; no visible change in
the time of the flexo-foundation-test suite. A forced cache gave stale texts in one run.
