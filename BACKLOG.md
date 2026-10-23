# connie — Backlog

Features and evolutions of connie to develop. Each entry says the problem, the options and the acceptance criteria. Bugs go to `KNOWN_DEFECTS.md`.

Identifiers are stable and never reused: `CONNIE-F-<n>`. Status legend: `TODO` · `IN PROGRESS` · `BLOCKED` · `DONE` · `DEFERRED`.

---

### CONNIE-F-2 — Guava out of connie-core  ·  `DONE`

**Done (2026-10-23).** `connie-core` no longer depends on Guava: the three `TypeToken` literals of `DataBindingFactory` and the two tests use the JDK / `ParameterizedTypeImpl`, `TypeUtils.isPrimitive` / `isWrapperClass` no longer use `Primitives` (same semantics, `void` and `Void` included). commons-lang3 stays (`reflect.TypeUtils.isAssignable` and `getTypeArguments` have no equivalent in Guava). The modules that use Guava directly now declare `implementation guava()` themselves: `flexographicutils`, `gina-api`, `gina-core`, `flexo-foundation`, `flexo-ui`, `owl-ta`, `pamela-core`, `opc-ua-ta-test`.

### CONNIE-F-1 — One implementation of `ClassScope` / `LoadedClassesInfo`, in connie  ·  `DONE`

**Done (2026-10-23).** `ClassScope` and `LoadedClassesInfo` are single, in connie (option 2): `ClassEditor` supplies the icons (`getClassIcon(ClassInfo)`, `getPackageIcon()`), `ClassEditor.fib` / `ClassSelector.fib` bind to them and name connie's classes. Both classes then moved to `connie-utils` (`org.openflexo.toolbox`), and with the whole of `connie-utils` to `openflexo-utils` 2.0, the bottom layer. `ClassScope` also lost its `SecurityManager` (now `StackWalker`). The stale copy in `gina-swing-editor-test` (with its `TestClassSelector.fib`) is left as is. Not checked visually: the icons of the class browser.

**Problem.** `ClassScope` and `LoadedClassesInfo` exist twice: in `connie-core` (`org.openflexo.connie.type`, used by `TypeMustBeResolved`) and in `gina-swing` (`org.openflexo.gina.swing.utils`, used by `ClassEditor`, `TypeSelector`, `ClassEditor.fib`, `ClassSelector.fib`). The copy was made in January 2025 so that `flexo-foundation` does not depend on gina-swing, and the GINA one was marked `@Deprecated` with "use connie's, remove the Icon dependencies". The migration stopped there; both copies had to be fixed separately (`GINA-D-1`, `CONNIE-D-2`).

**Options.**
1. `gina-swing` keeps a thin subclass or wrapper adding `getIcon()` on `ClassInfo` / `PackageInfo`; `ClassScope` of gina is removed.
2. The `.fib` get the icons through a binding on the class kind (enum / interface / class), and `gina-swing` uses connie's classes directly.

**Constraints.** The `.fib` reach `LoadedClassesInfo$ClassInfo`, `$PackageInfo` and `$IgnoredPattern` by class name; `ClassEditor` and `TypeSelector` must follow. gina (2.4) would then depend on a connie (2.1.0) that already has the scan: check the version line.

**Acceptance.** A single `ClassScope`; `ClassEditor` and `TypeSelector` work as before (type field of the inspector, search by partial name); the gina copy and its test copy in `gina-swing-editor-test` are gone.
