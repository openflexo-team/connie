# connie — Backlog

Features and evolutions of connie to develop. Each entry says the problem, the options and the acceptance criteria. Bugs go to `KNOWN_DEFECTS.md`.

Identifiers are stable and never reused: `CONNIE-F-<n>`. Status legend: `TODO` · `IN PROGRESS` · `BLOCKED` · `DONE` · `DEFERRED`.

---

### CONNIE-F-4 — A strict evaluation mode, and exceptions that keep their message and cause  ·  `TODO`

**Problem (audit of `CORE-F-11`, openflexo-core, 2026-11-13).** Connie turns several evaluation failures into `null`, which FML
cannot tell from a real null value:
- `DataBinding.getBindingValue` returns null for an invalid binding, and `setBindingValue` only logs a warning for an invalid or not
  settable binding.
- `Expression.evaluate` prints and returns null for any `TransformException` other than a type mismatch, a null reference or an
  invocation target (an `InvalidCastException` is lost that way), and throws `new InvocationTargetException(null)`, which wraps nothing.
- `BindingPath.getBindingValue` returns null on a `ConcurrentModificationException`.
- The method path elements (`JavaInstanceMethodPathElement` and its static / new-instance variants) return null on
  `IllegalArgumentException` and `IllegalAccessException`.

Several exceptions lose what they carry:
- `TransformException(String)` drops its message, so `InvalidCastException` and `NotSettableContextException(String)` give a null
  `getMessage()`.
- `InvocationTargetTransformException` sets no Java cause (`getCause()` is null).
- `VisitorException(String)` drops its message.

**Proposed.**
- An evaluation policy, `STRICT` or `LENIENT`, given by the `BindingEvaluationContext` (a default method returning `LENIENT`, so current users,
  GINA among them, are unchanged).
- In `STRICT`, the cases above raise a new unchecked `BindingEvaluationException` that carries the binding, the kind of failure and the cause.
  FML evaluation contexts return `STRICT`.
- Messages and causes are kept in every exception of `org.openflexo.connie.exception`.

**Acceptance criteria.**
- Tests in connie for each case, in both modes.
- `CORE-F-11` phase 3 green on top of it.
- The new version of connie is decided with the user.

### CONNIE-F-3 — Write a binding back as it was written  ·  `DONE`

**Problem.** `DataBinding.toString()` is the normalized text of the expression (`1 + 2`): a language that keeps the text the user wrote
(`1+2`, parentheses, qualified names) had no way to give it for a binding (openflexo-core, CORE-D-33).

**Done (2026-11-13), decided with the user.** `toString()`, `equals` and `hashCode` stay on the normalized text. A binding may carry a
`DataBindingPrettyPrintDelegate`, and `DataBinding.getPrettyPrint()` gives its text (the normalized text without delegate). The language that
parses a binding installs it: from the text of a unit (FML installs it itself), or from a typed text: `BindingFactory.parseExpression(text,
owner, delegateReceiver)` (a default method that gives none) may give one, which the binding installs only if it takes the parsed expression
(`setExpression` keeps an equal expression, and the delegate of that one). `rebuild()` and `clone()` parse the text as written. connie does
not depend on p2pp: FML implements the delegate with its p2pp nodes. Test: `TestDataBindingPrettyPrint` (openflexo-core,
`flexo-foundation-test`).

**Revised after review (2026-11-13), decided with the user.** `rebuild()` and `clone()` parse the normalized text again (a wrong
preserved text must not change the meaning; an equal expression keeps its text anyway). An expression set by code, without a delegate,
drops the former delegate and the node tree it holds. A typed binding keeps, for its life, its AST, raw source, node map and vehicle
binding (cost accepted).

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
