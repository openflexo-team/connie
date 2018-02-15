/**
 * 
 * Copyright (c) 2014, Openflexo
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

import java.util.List;
import java.util.regex.Pattern;

import org.junit.Test;
import org.openflexo.rm.ClasspathResourceLocatorImpl;
import org.openflexo.rm.Resource;
import org.openflexo.rm.ResourceLocator;

import junit.framework.TestCase;

public class ClasspathResourceLocatorTest extends TestCase {

	@Test
	public void testListResources() throws Exception {
		// Unused ClasspathResourceLocatorImpl rl =
		new ClasspathResourceLocatorImpl();

		Resource rloc = null;

		rloc = ResourceLocator.locateResource("Config");

		assertTrue(rloc != null);

		if (rloc != null) {

			List<?> list = rloc.getContents(Pattern.compile(".*[.]properties"), false);

			assertTrue(list.size() == 7);

		}
	}

	@Test
	public void testListResources2() throws Exception {
		// Unused ClasspathResourceLocatorImpl rl =
		new ClasspathResourceLocatorImpl();

		Resource rloc = null;

		rloc = ResourceLocator.locateResource("META-INF");

		assertTrue(rloc != null);
		System.out.println("Found META-INF here: " + rloc.getURI());

		// Sylvain, this does not work on Java 9, the corresponding module does not export FrameClose.wav
		rloc = ResourceLocator.locateResource("javax/swing/plaf/metal/sounds/FrameClose.wav");

		if (ToolBox.getJavaVersion() < 9) {
			assertTrue(rloc != null);
		}
		// System.out.println(rloc.getURI());

		if (rloc != null) {

			Resource container = rloc.getContainer();

			if (container != null) {
				Pattern pat = Pattern.compile(".*[.]wav");
				assertTrue(rloc.getContainer().getContents(pat, false).size() > 1);

				for (Resource r : rloc.getContainer().getContents(pat, false)) {
					System.out.println(r.getURI());
				}
			}

		}
	}

	@Test
	public void testListResources3() throws Exception {
		// Unused ClasspathResourceLocatorImpl rl =
		new ClasspathResourceLocatorImpl();

		Resource rloc = null;

		rloc = ResourceLocator.locateResource("TestDiff");

		assertTrue(rloc != null);

		System.out.println(rloc.getURI());

		if (rloc != null) {

			// This test tend to fail in eclipse because of the way eclipse manage resources
			// Here we have resources that are java files! My solution is to tweak the run
			// configuration for tests adding explicitly the folder containing the java files
			// first in the classpath
			List<? extends Resource> list = rloc.getContents(false);

			for (Resource r : list) {
				System.out.println(r.getURI());
			}

			assertTrue(list.size() == 8);
		}
	}
}
