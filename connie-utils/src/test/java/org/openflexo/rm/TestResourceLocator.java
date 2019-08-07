/**
 *
 * Copyright (c) 2014-2019, Openflexo
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

package org.openflexo.rm;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.io.File;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.openflexo.toolbox.ToolBox;

/**
 * Test (some) ResourceLocator features
 *
 * @author sylvain
 *
 */
@TestMethodOrder(OrderAnnotation.class)
@DisplayName("Test for the resource locator")
class TestResourceLocator {
	@Test
	@Order(1)
	@DisplayName("the resource is in the classpath")
	void testLocateResourceInClassPath() {
		Resource r = ResourceLocator.locateResource("Config/logging.properties");
		assertNotNull(r);
		assertTrue(r instanceof FileResourceImpl);
		assertTrue(((FileResourceImpl) r).getFile().getAbsolutePath().contains("Config"));
	}

	@Test
	@Order(2)
	@DisplayName("the resource is in the cache")
	void testCache() {
		Resource r = ResourceLocator.locateResource("Config/logging.properties");
		Resource r2 = ResourceLocator.locateResource("Config/logging.properties");
		assertSame(r, r2);
	}

	@Test
	@Order(3)
	@DisplayName("the resource is in the source code")
	void testLocateResourceInSourceCode() {
		Resource r = ResourceLocator.locateResource("Config/logging.properties");
		Resource r2 = ResourceLocator.locateSourceCodeResource(r);
		Resource r3 = ResourceLocator.locateSourceCodeResource("Config/logging.properties");

		assertTrue(r instanceof FileResourceImpl);
		assertTrue(r2 instanceof FileResourceImpl);
		assertTrue(r3 instanceof FileResourceImpl);

		assertTrue(((FileResourceImpl) r).getFile().getAbsolutePath().contains("Config"));
		assertTrue(((FileResourceImpl) r2).getFile().getAbsolutePath()
				.contains("src" + File.separator + "main" + File.separator + "resources"));

		assertSame(r2, r3);

	}

	@ParameterizedTest
	@Order(4)
	@DisplayName("the resource is a list")
	@MethodSource("provideResourceLocator")
	void testListResources(ResourceLocatorDelegate rl) throws Exception {
		Resource rloc = ResourceLocator.locateResource("Config");
		assertTrue(rloc != null);
		// rloc is not null
		List<? extends Resource> list = rloc.getContents(Pattern.compile(".*[.]properties"), false);
		assertTrue(list.size() == 7);
		for (Resource r : list) {
			System.out.println(r.getURI());
		}
	}

	@ParameterizedTest
	@Order(4)
	@MethodSource("provideResourceLocator")
	void testListResources2(ResourceLocatorDelegate rl) throws Exception {
		Resource rloc = ResourceLocator.locateResource("TestDiff");
		assertTrue(rloc != null);
		System.out.println(rloc.getURI());

		// This test tend to fail in eclipse because of the way eclipse manage resources
		// Here we have resources that are java files! My solution is to tweak the run
		// configuration for tests adding explicitly the folder containing the java files
		// first in the classpath
		List<? extends Resource> list = rloc.getContents(false);
		assertTrue(list.size() == 8);
		for (Resource r : list) {
			System.out.println(r.getURI());
		}
	}

	@Test
	void testListResources3() {
		new ClasspathResourceLocatorImpl();
		// This does not work after Java 9, the corresponding module does not export FrameClose.wav
		assumeTrue(ToolBox.getJavaVersion() < 9);
		Resource rloc = ResourceLocator.locateResource("javax/swing/plaf/metal/sounds/FrameClose.wav");
		assertTrue(rloc != null);
		// rloc is not null
		Resource container = rloc.getContainer();
		if (container != null) {
			Pattern pat = Pattern.compile(".*[.]wav");
			assertTrue(rloc.getContainer().getContents(pat, false).size() > 1);
			for (Resource r : rloc.getContainer().getContents(pat, false)) {
				System.out.println(r.getURI());
			}
		}
	}

	@ParameterizedTest
	@Order(4)
	@MethodSource("provideResourceLocator")
	void testMetaInf(ResourceLocatorDelegate rl) throws Exception {
		Resource rloc = ResourceLocator.locateResource("META-INF");
		assertTrue(rloc != null);
		assertTrue(rloc instanceof FileResourceImpl);
		System.out.println("Found META-INF here: " + rloc.getURI());
	}

	private static String PATH_SEP = System.getProperty("file.separator");

	static Stream<ResourceLocatorDelegate> provideResourceLocator() {
		FileSystemResourceLocatorImpl rl = new FileSystemResourceLocatorImpl();
		File workingDirectory = new File(System.getProperty("user.dir"));
		rl.prependToDirectories(workingDirectory + PATH_SEP + "src/main/resources");
		rl.prependToDirectories(workingDirectory + PATH_SEP + "src/test/resources");
		return Stream.of(new ClasspathResourceLocatorImpl(), rl);
	}
}
