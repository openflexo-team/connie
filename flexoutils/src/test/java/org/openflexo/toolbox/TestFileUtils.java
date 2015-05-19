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

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;

import org.junit.BeforeClass;
import org.junit.Test;

public class TestFileUtils {

	static File tempDirectory;
	static File a;
	static File b;
	static File c;
	static File d;
	static File e;
	static File f;

	@BeforeClass
	public static void setup() throws IOException {
		tempDirectory = File.createTempFile("test", "fileutils");
		tempDirectory.mkdirs();
		System.out.println("Created " + tempDirectory);
		a = new File(tempDirectory, "a");
		b = new File(a, "b");
		c = new File(b, "c");
		d = new File(c, "d");
		e = new File(c, "e");
		f = new File(e, "f");
	}

	@Test
	public void test1() {
		assertEquals(0, FileUtils.distance(a, a));
	}

	@Test
	public void test2() {
		assertEquals(1, FileUtils.distance(d, c));
		assertEquals(1, FileUtils.distance(e, f));
	}

	@Test
	public void test3() {
		assertEquals(1, FileUtils.distance(c, d));
		assertEquals(1, FileUtils.distance(f, e));
	}

	@Test
	public void test4() {
		assertEquals(2, FileUtils.distance(c, f));
		assertEquals(2, FileUtils.distance(f, c));
	}

	@Test
	public void test5() {
		assertEquals(4, FileUtils.distance(a, f));
		assertEquals(4, FileUtils.distance(f, a));
	}

	@Test
	public void test6() {
		assertEquals(3, FileUtils.distance(d, f));
		assertEquals(3, FileUtils.distance(f, d));
	}
}
