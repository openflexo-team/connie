/**
 * 
 * Copyright (c) 2013-2014, Openflexo
 * Copyright (c) 2011-2012, AgileBirds
 * 
 * This file is part of Flexoutils, a component of the software infrastructure 
 * developed at Openflexo.
 * 
 * 
 * Openflexo is dual-licensed under the European Union Public License (EUPL, either 
 * version 1.1 of the License, or any later version ), which is available at 
 * https://joinup.ec.europa.eu/software/page/eupl/licence-eupl
 * and the GNU General Public License (GPL, either version 3 of the License, or any 
 * later version), which is available at http://www.gnu.org/licenses/gpl.html .
 * 
 * You can redistribute it and/or modify under the terms of either of these licenses
 * 
 * If you choose to redistribute it and/or modify under the terms of the GNU GPL, you
 * must include the following additional permission.
 *
 *          Additional permission under GNU GPL version 3 section 7
 *
 *          If you modify this Program, or any covered work, by linking or 
 *          combining it with software containing parts covered by the terms 
 *          of EPL 1.0, the licensors of this Program grant you additional permission
 *          to convey the resulting work. * 
 * 
 * This software is distributed in the hope that it will be useful, but WITHOUT ANY 
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A 
 * PARTICULAR PURPOSE. 
 *
 * See http://www.openflexo.org/license.html for details.
 * 
 * 
 * Please contact Openflexo (openflexo-contacts@openflexo.org)
 * or visit www.openflexo.org if you need additional information.
 * 
 */

package org.openflexo.toolbox;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Vector;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A simple static API for listing classes loaded in a JVM, or available to it. Based on {@link #getClassLocation(Class)} from
 * http://www.javaworld.com/javaworld/javaqa/2003-07/01-qa-0711-classsrc.html
 * 
 * @author (C) <a href="http://www.javaworld.com/columns/jw-qna-index.shtml">Vlad Roubtsov</a>, 2003
 */
public class ClassScope {
	private static final Logger LOGGER = Logger.getLogger(ClassScope.class.getPackage().getName());

	private static final Class<?>[] EMPTY_CLASS_ARRAY = new Class<?>[0];
	private static final Throwable CVF_FAILURE; // set in <clinit>

	static {
		Throwable failure = null;

		Field tempf = null;
		try {
			// this can fail if this is not a Sun-compatible JVM
			// or if the security is too tight:

			tempf = ClassLoader.class.getDeclaredField("classes");
			if (tempf.getType() != Vector.class) {
				throw new RuntimeException("not of type java.util.Vector: " + tempf.getType().getName());
			}

			tempf.setAccessible(true);
		} catch (Throwable t) {
			failure = t;
		}
		CLASSES_VECTOR_FIELD = tempf;
		CVF_FAILURE = failure;

	}

	/**
	 * Given a class loader instance, returns all classes currently loaded by that class loader.
	 * 
	 * This relies on the private field <code>ClassLoader.classes</code>, which does not exist anymore in recent JREs (Java 9+): the result
	 * is then empty. Use {@link #getAvailableClassNames(ClassLoader[])} to list the classes that can be loaded.
	 * 
	 * @param defining
	 *            class loader to inspect [may not be null]
	 * @return Class array such that every Class has 'loader' as its defining class loader [never null, may be empty, always empty if the
	 *         "classes" field hack is not possible in this JRE]
	 */
	@SuppressWarnings("unchecked")
	public static Class<?>[] getLoadedClasses(final ClassLoader loader) {
		if (loader == null) {
			throw new IllegalArgumentException("null input: loader");
		}
		if (CLASSES_VECTOR_FIELD == null) {
			LOGGER.fine("ClassScope::getLoadedClasses() cannot be used in this JRE: " + CVF_FAILURE);
			return EMPTY_CLASS_ARRAY;
		}

		try {
			final Vector<Class<?>> classes = (Vector<Class<?>>) CLASSES_VECTOR_FIELD.get(loader);
			if (classes == null) {
				return EMPTY_CLASS_ARRAY;
			}

			final Class<?>[] result;

			// note: Vector is synchronized in Java 2, which helps us make
			// the following into a safe critical section:

			synchronized (classes) {
				result = new Class<?>[classes.size()];
				classes.toArray(result);
			}

			return result;
		}
		// this should not happen if <clinit> was successful:
		catch (IllegalAccessException e) {
			e.printStackTrace(System.out);

			return EMPTY_CLASS_ARRAY;
		}
	}

	/**
	 * A convenience multi-loader version of {@link #getLoadedClasses(ClassLoader)}.
	 * 
	 * @param an
	 *            array of defining class loaders to inspect [may not be null]
	 * @return Class<?> array [never null, may be empty, always empty if the "classes" field hack is not possible in this JRE]
	 */
	public static Class<?>[] getLoadedClasses(final ClassLoader[] loaders) {
		if (loaders == null) {
			throw new IllegalArgumentException("null input: loaders");
		}

		final List<Class<?>> resultList = new LinkedList<>();

		for (int l = 0; l < loaders.length; ++l) {
			final ClassLoader loader = loaders[l];
			if (loader != null) {
				final Class<?>[] classes = getLoadedClasses(loaders[l]);

				resultList.addAll(Arrays.asList(classes));
			}
		}

		final Class<?>[] result = new Class<?>[resultList.size()];
		resultList.toArray(result);

		return result;
	}

	/**
	 * Lists the names of the classes that can be loaded, whether they are already loaded or not, by scanning the classpath
	 * (<code>java.class.path</code>), the URLs of the {@link URLClassLoader}s found in the given loaders (and their parents), and the public
	 * API of the JRE (<code>java.*</code> and <code>javax.*</code> classes of the runtime image, Java 9+).<br>
	 * 
	 * Nothing is loaded: only names are read. An entry that cannot be read is skipped. Anonymous and local classes are ignored.
	 * 
	 * @param loaders
	 *            class loaders whose URLs are scanned in addition to the classpath [may not be null, may contain null]
	 * @return the class names, as returned by {@link Class#getName()} [never null, may be empty]
	 */
	public static Set<String> getAvailableClassNames(final ClassLoader[] loaders) {
		if (loaders == null) {
			throw new IllegalArgumentException("null input: loaders");
		}

		final Set<File> roots = new LinkedHashSet<>();
		for (String entry : System.getProperty("java.class.path", "").split(File.pathSeparator)) {
			if (!entry.isEmpty()) {
				roots.add(new File(entry));
			}
		}
		for (ClassLoader loader : loaders) {
			for (; loader != null; loader = loader.getParent()) {
				if (loader instanceof URLClassLoader) {
					for (URL url : ((URLClassLoader) loader).getURLs()) {
						if ("file".equals(url.getProtocol())) {
							try {
								roots.add(new File(url.toURI()));
							} catch (URISyntaxException | IllegalArgumentException e) {
								LOGGER.fine("Ignored classpath URL " + url + ": " + e);
							}
						}
					}
				}
			}
		}

		final Set<String> result = new LinkedHashSet<>();
		for (File root : roots) {
			try {
				if (root.isDirectory()) {
					scanDirectory(root.toPath(), result);
				}
				else if (root.isFile()) {
					scanJar(root, result);
				}
			} catch (IOException | RuntimeException e) {
				LOGGER.fine("Ignored classpath entry " + root + ": " + e);
			}
		}
		scanRuntimeImage(result);
		return result;
	}

	private static void scanJar(final File jar, final Set<String> result) throws IOException {
		try (JarFile jarFile = new JarFile(jar)) {
			final Enumeration<JarEntry> entries = jarFile.entries();
			while (entries.hasMoreElements()) {
				addClassEntry(entries.nextElement().getName(), result);
			}
		}
	}

	private static void scanDirectory(final Path root, final Set<String> result) throws IOException {
		try (Stream<Path> files = Files.walk(root)) {
			files.filter(Files::isRegularFile).forEach(f -> addClassEntry(root.relativize(f).toString().replace(File.separatorChar, '/'), result));
		}
	}

	/**
	 * Adds the public API of the JRE (Java 9+ runtime image, <code>jrt:/</code>). Does nothing if the runtime image is not readable.
	 */
	private static void scanRuntimeImage(final Set<String> result) {
		try {
			final FileSystem jrt = FileSystems.getFileSystem(URI.create("jrt:/"));
			try (Stream<Path> modules = Files.list(jrt.getPath("/modules"))) {
				modules.forEach(module -> {
					try (Stream<Path> files = Files.walk(module)) {
						files.filter(Files::isRegularFile).forEach(f -> {
							final String name = module.relativize(f).toString();
							if (name.startsWith("java/") || name.startsWith("javax/")) {
								addClassEntry(name, result);
							}
						});
					} catch (IOException | RuntimeException e) {
						LOGGER.fine("Ignored module " + module + ": " + e);
					}
				});
			}
		} catch (IOException | RuntimeException e) {
			// No runtime image (Java 8, or restricted): the JRE classes are simply not listed
			LOGGER.fine("Runtime image not scanned: " + e);
		}
	}

	private static void addClassEntry(final String entryName, final Set<String> result) {
		if (!entryName.endsWith(".class") || entryName.endsWith("module-info.class") || entryName.endsWith("package-info.class")) {
			return;
		}
		final String name = entryName.substring(0, entryName.length() - ".class".length()).replace('/', '.');
		// Anonymous and local classes (Foo$1, Foo$1Bar) are not meaningful for a user
		for (int i = name.indexOf('$'); i >= 0; i = name.indexOf('$', i + 1)) {
			if (i + 1 < name.length() && Character.isDigit(name.charAt(i + 1))) {
				return;
			}
		}
		result.add(name);
	}

	/**
	 * Returns the class loader set "relevant" to the calling class, as described in the article. Starting with the class that is the caller
	 * of this method, it collects all class loaders that are loaders for all classes on the call stack and their respective parent loaders.
	 * 
	 * @return ClassLoader array [never null]
	 */
	public static ClassLoader[] getCallerClassLoaderTree() {
		// The first frame is this method: the call context starts at its caller
		final List<Class<?>> callContext = STACK_WALKER
				.walk(frames -> frames.skip(1).map(StackWalker.StackFrame::getDeclaringClass).collect(Collectors.toList()));

		final Set<ClassLoader> resultSet = new HashSet<>();

		for (Class<?> cls : callContext) {
			getClassLoaderTree(cls, resultSet);
		}

		final ClassLoader[] result = new ClassLoader[resultSet.size()];
		resultSet.toArray(result);

		return result;
	}

	/**
	 * Given a Class<?> object, attempts to find its .class location [returns null if no such definiton could be found].
	 * 
	 * @return URL that points to the class definition [null if not found]
	 */
	public static URL getClassLocation(final Class<?> cls) {
		if (cls == null) {
			throw new IllegalArgumentException("null input: cls");
		}

		URL result = null;
		final String clsAsResource = cls.getName().replace('.', '/').concat(".class");

		final ProtectionDomain pd = cls.getProtectionDomain();
		// java.lang.Class<?> contract does not specify if 'pd' can ever be
		// null;
		// it is not the case for Sun's implementations, but guard against null
		// just in case:
		if (pd != null) {
			final CodeSource cs = pd.getCodeSource();
			// 'cs' can be null depending on the classloader behavior:
			if (cs != null) {
				result = cs.getLocation();
			}

			if (result != null) {
				// convert a code source location into a full class file
				// location
				// for some common cases:
				if ("file".equals(result.getProtocol())) {
					try {
						if (result.toExternalForm().endsWith(".jar") || result.toExternalForm().endsWith(".zip")) {
							result = new URL("jar:".concat(result.toExternalForm()).concat("!/").concat(clsAsResource));
						}
						else if (new File(result.getFile()).isDirectory()) {
							result = new URL(result, clsAsResource);
						}
					} catch (MalformedURLException ignore) {
					}
				}
			}
		}

		if (result == null) {
			// try to find 'cls' definition as a resource; this is not
			// documented to be legal but Sun's implementations seem to allow
			// this:
			final ClassLoader clsLoader = cls.getClassLoader();

			result = clsLoader != null ? clsLoader.getResource(clsAsResource) : ClassLoader.getSystemResource(clsAsResource);
		}

		return result;
	}

	private ClassScope() {
	} // this class is not extendible

	private static void getClassLoaderTree(final Class<?> cls, final Set<ClassLoader> resultSet) {
		if (cls != null && resultSet != null) {
			for (ClassLoader loader = cls.getClassLoader(); loader != null; loader = loader.getParent()) {
				resultSet.add(loader);
			}
		}
	}

	private static final Field CLASSES_VECTOR_FIELD; // set in <clinit> [can be
														// null]
	private static final StackWalker STACK_WALKER = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE);

}
