/*
 * (c) Copyright 2010-2011 AgileBirds
 *
 * This file is part of OpenFlexo.
 *
 * OpenFlexo is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OpenFlexo is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OpenFlexo. If not, see <http://www.gnu.org/licenses/>.
 *
 */
package org.openflexo.toolbox;

import java.io.File;
import java.util.List;
import java.util.regex.Pattern;

import junit.framework.TestCase;

import org.junit.Test;
import org.openflexo.rm.ClasspathResourceLocatorImpl;
import org.openflexo.rm.FileResourceImpl;
import org.openflexo.rm.FileSystemResourceLocatorImpl;
import org.openflexo.rm.Resource;

public class FileSystemResourceLocatorTest extends TestCase {

	private static String PATH_SEP = System.getProperty("file.separator");

	@Test
	public void testListResources() throws Exception {
		FileSystemResourceLocatorImpl rl = new FileSystemResourceLocatorImpl();
		File workingDirectory = new File(System.getProperty("user.dir"));
		rl.prependToDirectories(workingDirectory + PATH_SEP + "src/main/resources");
		rl.prependToDirectories(workingDirectory + PATH_SEP + "src/test/resources");
		

		rl.printDirectoriesSearchOrder(System.out);

		Resource rloc = null;

		rloc = rl.locateResource("Config");

		assertTrue(rloc != null);

		if (rloc != null){

			List<Resource> list = rl.listResources(rloc, Pattern.compile(".*[.]properties"));

			assertTrue(list.size() == 7);

		}
	}

	@Test
	public void testListResources2() throws Exception {
		FileSystemResourceLocatorImpl rl = new FileSystemResourceLocatorImpl();
		File workingDirectory = new File(System.getProperty("user.dir"));
		rl.appendToDirectories(workingDirectory + PATH_SEP + "src/main/resources");
		rl.appendToDirectories(workingDirectory + PATH_SEP + "src/test/resources");

		rl.printDirectoriesSearchOrder(System.out);
		
		Resource rloc = null;

		rloc = rl.locateResource("META-INF");

		assertTrue(rloc != null);

		assertTrue (rloc instanceof FileResourceImpl);

		System.out.println(rloc.getURI());


		rloc = rl.locateResource("TestDiff");
		
		assertTrue(rloc != null);

		if (rloc != null){

			List<? extends Resource> list = rloc.getContents();

			assertTrue (list.size() == 8);

			for (Resource r : list){
				System.out.println(r.getURI());
			}

		}
	}
}
