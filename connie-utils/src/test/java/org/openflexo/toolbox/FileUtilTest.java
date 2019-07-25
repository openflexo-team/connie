/**
 *
 * Copyright (c) 2013-2019, Openflexo
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.util.Date;

import org.junit.jupiter.api.Test;
import org.openflexo.toolbox.FileUtils.CopyStrategy;

public class FileUtilTest {

	@Test
	public void testFileNameFixing() {
		String s256 = "012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789"
				+ "01234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789";
		String invalid = s256 + "/" + s256 + ".xml";
		String valid = s256.substring(0, 240) + "/" + s256.substring(0, 240) + ".xml";
		assertEquals(valid, FileUtils.getValidFileName(invalid));
	}

	@Test
	public void testCopyStrategy() throws Exception {
		File tempDirectory = FileUtils.createTempDirectory("TestFileUtils", null);
		File destTempDirectory = FileUtils.createTempDirectory("TestFileUtilsDestination", null);
		File testFile = new File(tempDirectory, "TestFile");
		assertTrue(FileUtils.createNewFile(testFile));
		FileUtils.saveToFile(testFile, CONTENT);
		Date testFileLastModified = FileUtils.getDiskLastModifiedDate(testFile);
		File destFile = FileUtils.copyFileToDir(testFile, destTempDirectory);
		Date destFileModified = FileUtils.getDiskLastModifiedDate(destFile);
		Thread.sleep(1001);// Let's wait 1s so that FS without ms don't screw up this test.
		FileUtils.copyContentDirToDir(tempDirectory, destTempDirectory, CopyStrategy.IGNORE_EXISTING);
		assertEquals(destFileModified, FileUtils.getDiskLastModifiedDate(destFile),
				"Last modified should not change when ignoring existing files");
		Thread.sleep(1001);// Let's wait 1s so that FS without ms don't screw up this test.
		FileUtils.copyContentDirToDir(tempDirectory, destTempDirectory, CopyStrategy.REPLACE_OLD_ONLY);
		assertEquals(destFileModified, FileUtils.getDiskLastModifiedDate(destFile),
				"Last modified should not change when replacing old files only");
		Thread.sleep(1001);// Let's wait 1s so that FS without ms don't screw up this test.
		FileUtils.copyContentDirToDir(tempDirectory, destTempDirectory, CopyStrategy.REPLACE);
		assertFalse(destFileModified.equals(FileUtils.getDiskLastModifiedDate(destFile)),
				"Last modified should have changed when replacing files");
		// Since we have replaced the file, we need to update its last modified
		destFileModified = FileUtils.getDiskLastModifiedDate(destFile);
		Thread.sleep(1001);// Let's wait 1s so that FS without ms don't screw up this test.
		FileUtils.saveToFile(testFile, CONTENT);
		assertFalse(testFileLastModified.equals(FileUtils.getDiskLastModifiedDate(testFile)),
				"Last modified should have changed after changing its content");
		testFileLastModified = FileUtils.getDiskLastModifiedDate(testFile);
		FileUtils.copyContentDirToDir(tempDirectory, destTempDirectory, CopyStrategy.REPLACE_OLD_ONLY);
		assertFalse(destFileModified.equals(FileUtils.getDiskLastModifiedDate(destFile)),
				"Last modified should have changed when replacing old files only with a newer file");
		FileUtils.deleteDir(tempDirectory);
		FileUtils.deleteDir(destTempDirectory);
	}

	private static final String CONTENT = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed accumsan tellus sit amet enim. In hac habitasse platea dictumst. Aliquam nec lacus. Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia Curae; Aenean pellentesque. Nam dui lorem, tempor quis, aliquet sed, porta ac, lacus. Aliquam erat volutpat. Maecenas lobortis scelerisque sapien. Nunc lorem augue, pulvinar sed, venenatis ac, venenatis at, quam. Curabitur rutrum. Sed vitae quam. Nulla nisi. Ut turpis. Vivamus rhoncus. Sed enim. Sed suscipit laoreet lacus. In hac habitasse platea dictumst.\r\n"
			+ "\r\n"
			+ "Mauris neque enim, congue dignissim, viverra non, consectetur ultrices, pede. Fusce convallis malesuada dui. Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia Curae; Nunc et risus ut sapien blandit tincidunt. Pellentesque habitant morbi tristique senectus et netus et malesuada fames ac turpis egestas. Duis lorem. Nam turpis purus, accumsan eget, tincidunt non, aliquet quis, nibh. Nullam lectus sem, consequat non, mollis in, tempor vitae, nisl. Nulla facilisi. Sed suscipit tempor mauris. Donec volutpat commodo lectus. Pellentesque in arcu a leo posuere pretium. Aliquam eleifend dolor eget ligula. Nunc risus nulla, euismod eu, pellentesque id, vehicula sit amet, mi.\r\n"
			+ "\r\n"
			+ "Morbi condimentum velit in tellus. Aliquam tincidunt metus ut pede. Quisque ultrices quam quis ante. Praesent sit amet velit non eros suscipit mattis. Etiam dui. Nam id tortor et nunc varius cursus. Phasellus et neque non orci ornare adipiscing. Nullam viverra neque ac diam. Cras blandit enim vitae nulla. Nunc eu massa in erat rhoncus fermentum. Phasellus semper elementum nunc. Duis eu diam at pede blandit consectetur. Sed ultricies posuere urna.\r\n"
			+ "\r\n"
			+ "Nullam consectetur, tortor vitae imperdiet gravida, erat felis accumsan tortor, at feugiat nulla quam tristique neque. Praesent sodales. Nullam metus turpis, lacinia eu, posuere at, ornare non, dolor. Donec pharetra consectetur urna. Duis sagittis arcu sit amet quam. Class aptent taciti sociosqu ad litora torquent per conubia nostra, per inceptos himenaeos. Fusce nibh enim, faucibus id, consequat hendrerit, rhoncus at, turpis. Mauris ac quam. Phasellus viverra pulvinar pede. Cras quam. Morbi libero. Nunc ligula eros, lobortis nec, dapibus at, molestie id, ante. In dignissim ultrices neque. Maecenas lorem enim, molestie ac, euismod eu, vehicula eget, augue.\r\n"
			+ "\r\n"
			+ "Pellentesque elit nisi, convallis sit amet, mollis a, semper et, nulla. Vestibulum euismod. Vestibulum tincidunt tempus pede. Class aptent taciti sociosqu ad litora torquent per conubia nostra, per inceptos himenaeos. Duis sed elit at velit aliquet euismod. Integer lectus. Integer at est sed ligula suscipit posuere. Aenean libero. Aenean quis orci non ante porta dapibus. Etiam orci felis, aliquet nec, dictum quis, egestas a, ligula. Vivamus at lectus. Integer quis diam. Etiam vestibulum. Class aptent taciti sociosqu ad litora torquent per conubia nostra, per inceptos himenaeos. Vivamus nisi. Aliquam pharetra tincidunt libero. Mauris bibendum quam eget tellus. Curabitur bibendum elementum quam. ";

}
