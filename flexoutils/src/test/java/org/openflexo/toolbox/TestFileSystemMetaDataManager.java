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

import java.io.File;
import java.io.IOException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.openflexo.test.OrderedRunner;
import org.openflexo.test.TestOrder;

import junit.framework.TestCase;

@RunWith(OrderedRunner.class)
public class TestFileSystemMetaDataManager extends TestCase {

	private static FileSystemMetaDataManager fsMetaDataManager;
	private static File testDirectory, dir1, file1, file2, file3;

	@Test
	@TestOrder(1)
	public void testCreateDirectories() throws IOException {

		testDirectory = new File(File.createTempFile("temp", "").getParentFile(), "TestFileSystemMetaDataManager");
		testDirectory.mkdirs();
		System.out.println("dir=" + testDirectory);

		dir1 = new File(testDirectory, "dir1");
		dir1.mkdirs();

		file1 = new File(testDirectory, "file1");
		file1.createNewFile();

		file2 = new File(dir1, "file2");
		file2.createNewFile();

		file3 = new File(dir1, "file3");
		file3.createNewFile();

		assertTrue(testDirectory.exists());
		assertTrue(dir1.exists());
		assertTrue(file1.exists());
		assertTrue(file2.exists());
		assertTrue(file3.exists());

		fsMetaDataManager = new FileSystemMetaDataManager();

	}

	@Test
	@TestOrder(2)
	public void testSetProperties() throws IOException {

		fsMetaDataManager.setProperty("key1", "value1", testDirectory, true);
		fsMetaDataManager.setProperty("key2", "value2", dir1, true);
		fsMetaDataManager.setProperty("key3", "value3", file1, true);
		fsMetaDataManager.setProperty("key4", "value4", file2, true);
		fsMetaDataManager.setProperty("key5", "value5", file3, true);

		File expectedMetaDataFile1 = new File(testDirectory, ".metadata");
		assertTrue(expectedMetaDataFile1.exists());
		assertTrue(FileUtils.fileContents(expectedMetaDataFile1).contains("key1=value1"));
		assertTrue(FileUtils.fileContents(expectedMetaDataFile1).contains("file1.key3=value3"));

		File expectedMetaDataFile2 = new File(dir1, ".metadata");
		assertTrue(expectedMetaDataFile2.exists());
		assertTrue(FileUtils.fileContents(expectedMetaDataFile2).contains("key2=value2"));
		assertTrue(FileUtils.fileContents(expectedMetaDataFile2).contains("file2.key4=value4"));
		assertTrue(FileUtils.fileContents(expectedMetaDataFile2).contains("file3.key5=value5"));

	}

	@Test
	@TestOrder(3)
	public void testGetProperties() throws IOException {

		assertEquals("value1", fsMetaDataManager.getProperty("key1", testDirectory));
		assertEquals("value2", fsMetaDataManager.getProperty("key2", dir1));
		assertEquals("value3", fsMetaDataManager.getProperty("key3", file1));
		assertEquals("value4", fsMetaDataManager.getProperty("key4", file2));
		assertEquals("value5", fsMetaDataManager.getProperty("key5", file3));

	}

}
