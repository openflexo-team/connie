/**
 * 
 * Copyright (c) 2026, Openflexo
 * 
 * This file is part of Connie-core, a component of the software infrastructure 
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Set;

import org.junit.Test;

/**
 * Tests {@link ClassScope} and {@link LoadedClassesInfo}: the list of classes must be available on any JRE, in particular on Java 9+ where
 * the private field <code>ClassLoader.classes</code> does not exist anymore (CONNIE-D-2).<br>
 * Headless-safe.
 */
public class ClassScopeTest {

	private static final ClassLoader[] LOADERS = { ClassScopeTest.class.getClassLoader() };

	@Test
	public void testAvailableClassNames() {
		Set<String> names = ClassScope.getAvailableClassNames(LOADERS);
		// From a directory or a jar of the classpath
		assertTrue(names.contains(ClassScope.class.getName()));
		// From a dependency jar
		assertTrue(names.contains("org.junit.Test"));
		// From the JRE
		assertTrue(names.contains("java.util.ArrayList"));
		// Anonymous classes are not listed
		for (String name : names) {
			assertTrue(name, !name.matches(".*\\$\\d.*"));
		}
	}

	@Test
	public void testGetLoadedClassesNeverThrows() {
		assertNotNull(ClassScope.getLoadedClasses(LOADERS));
	}

	@Test
	public void testCallerClassLoaderTreeContainsTheCallerLoader() {
		List<ClassLoader> tree = java.util.Arrays.asList(ClassScope.getCallerClassLoaderTree());
		assertTrue(tree.contains(ClassScopeTest.class.getClassLoader()));
	}

	@Test
	public void testLoadedClassesInfoCanBeCreated() {
		assertNotNull(LoadedClassesInfo.getInstance());
	}

	/**
	 * A class that is not necessarily loaded must be found by a partial name
	 */
	@Test
	public void testSearchByPartialName() {
		List<Class<?>> found = LoadedClassesInfo.getInstance().search("util.concurrent.Phaser");
		assertTrue(found.toString(), found.contains(java.util.concurrent.Phaser.class));
		// Searching twice does not duplicate
		assertEquals(found.size(), LoadedClassesInfo.getInstance().search("util.concurrent.Phaser").size());
	}

	@Test
	public void testSearchBySimpleName() {
		List<Class<?>> found = LoadedClassesInfo.getInstance().search("ClassScopeTest");
		assertTrue(found.toString(), found.contains(ClassScopeTest.class));
		assertNotNull(LoadedClassesInfo.getInstance().registerClass(ClassScopeTest.class));
	}
}
