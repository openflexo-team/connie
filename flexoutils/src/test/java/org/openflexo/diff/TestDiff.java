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

package org.openflexo.diff;

import java.io.File;
import java.io.IOException;

import junit.framework.TestCase;

import org.junit.Test;
import org.openflexo.diff.ComputeDiff.AdditionChange;
import org.openflexo.diff.ComputeDiff.DiffChange;
import org.openflexo.diff.ComputeDiff.DiffReport;
import org.openflexo.diff.ComputeDiff.ModificationChange;
import org.openflexo.diff.ComputeDiff.RemovalChange;
import org.openflexo.diff.DiffSource.MergeToken;
import org.openflexo.rm.FileResourceImpl;
import org.openflexo.rm.ResourceLocator;
import org.openflexo.toolbox.FileUtils;

public class TestDiff extends TestCase {

	@Test
	public void test() {
		DiffSource source = new DiffSource("\t\t1\t\t\n\t\t\n\n4\n5\n", DelimitingMethod.DEFAULT);
		assertEquals(5, source.getTextTokens().length);
		for (MergeToken token : source.getTextTokens()) {
			System.err.println("Begin: '" + token.getBeginDelim().replace("\n", "\\n") + "'" + " Token: '" + token.getToken() + "'"
					+ " End: '" + token.getEndDelim().replace("\n", "\\n") + "'");
		}
	}

	@Test
	public void test0() throws IOException {
		ResourceLocator rl = ResourceLocator.getResourceLocator();
		File file1 = ((FileResourceImpl) (ResourceLocator.locateResource("TestDiff/TestJava0-v1.java"))).getFile();
		File file2 = ((FileResourceImpl) (ResourceLocator.locateResource("TestDiff/TestJava0-v2.java"))).getFile();
		System.out.println("Test 0");
		DiffPrint.diff(FileUtils.fileContents(file1), FileUtils.fileContents(file2));
		System.out.println("" + ComputeDiff.diff(FileUtils.fileContents(file1), FileUtils.fileContents(file2)));
		DiffReport report = ComputeDiff.diff(file1, file2);
		System.out.println(report.toString());
		assertEquals(report.getChanges().size(), 0);
	}

	@Test
	public void test1() throws IOException {
		ResourceLocator rl = ResourceLocator.getResourceLocator();
		File file1 =  ((FileResourceImpl) (ResourceLocator.locateResource("TestDiff/TestJava1-v1.java"))).getFile();
		File file2 =  ((FileResourceImpl) (ResourceLocator.locateResource("TestDiff/TestJava1-v2.java"))).getFile();
		System.out.println("Test 1");
		DiffPrint.diff(FileUtils.fileContents(file1), FileUtils.fileContents(file2));
		System.out.println("" + ComputeDiff.diff(FileUtils.fileContents(file1), FileUtils.fileContents(file2)));
		DiffReport report = ComputeDiff.diff(file1, file2);
		System.out.println(report.toString());
		assertEquals(report.getChanges().size(), 5);
		assertChange(report.getChanges().get(0), ModificationChange.class, 0, 0, 0, 0);
		assertChange(report.getChanges().get(1), ModificationChange.class, 24, 25, 24, 24);
		assertChange(report.getChanges().get(2), RemovalChange.class, 28, 28, 27, 26);
		assertChange(report.getChanges().get(3), AdditionChange.class, 33, 32, 31, 35);
		assertChange(report.getChanges().get(4), RemovalChange.class, 59, 59, 62, 61);
	}

	@Test
	public void test2() throws IOException {
		ResourceLocator rl = ResourceLocator.getResourceLocator();
		File file1 =  ((FileResourceImpl) (ResourceLocator.locateResource("TestDiff/TestJava2-v1.java"))).getFile();
		File file2 =  ((FileResourceImpl) (ResourceLocator.locateResource("TestDiff/TestJava2-v2.java"))).getFile();
		System.out.println("Test 2");
		DiffPrint.diff(FileUtils.fileContents(file1), FileUtils.fileContents(file2));
		System.out.println("" + ComputeDiff.diff(FileUtils.fileContents(file1), FileUtils.fileContents(file2)));
		DiffReport report = ComputeDiff.diff(file1, file2);
		System.out.println(report.toString());
		assertEquals(report.getChanges().size(), 4);
		assertChange(report.getChanges().get(0), AdditionChange.class, 21, 20, 21, 24);
		assertChange(report.getChanges().get(1), ModificationChange.class, 28, 28, 32, 32);
		assertChange(report.getChanges().get(2), ModificationChange.class, 38, 38, 42, 47);
		assertChange(report.getChanges().get(3), RemovalChange.class, 64, 68, 73, 72);
	}

	@Test
	public void test3() throws IOException {

		ResourceLocator rl = ResourceLocator.getResourceLocator();
		File file1 =  ((FileResourceImpl) (ResourceLocator.locateResource("TestDiff/TestJava3-v1.java"))).getFile();
		File file2 =  ((FileResourceImpl) (ResourceLocator.locateResource("TestDiff/TestJava3-v2.java"))).getFile();
		System.out.println("Test 3");
		DiffPrint.diff(FileUtils.fileContents(file1), FileUtils.fileContents(file2));
		System.out.println("" + ComputeDiff.diff(FileUtils.fileContents(file1), FileUtils.fileContents(file2)));
		DiffReport report = ComputeDiff.diff(file1, file2);
		System.out.println(report.toString());
		assertEquals(report.getChanges().size(), 9);
		assertChange(report.getChanges().get(0), ModificationChange.class, 0, 33, 0, 17);
		assertChange(report.getChanges().get(1), ModificationChange.class, 35, 39, 19, 69);
		assertChange(report.getChanges().get(2), ModificationChange.class, 42, 45, 72, 94);
		assertChange(report.getChanges().get(3), ModificationChange.class, 48, 52, 97, 97);
		assertChange(report.getChanges().get(4), ModificationChange.class, 54, 54, 99, 131);
		assertChange(report.getChanges().get(5), RemovalChange.class, 56, 59, 133, 132);
		assertChange(report.getChanges().get(6), ModificationChange.class, 61, 62, 134, 141);
		assertChange(report.getChanges().get(7), RemovalChange.class, 64, 68, 143, 142);
		assertChange(report.getChanges().get(8), AdditionChange.class, 70, 69, 144, 148);
	}

	private void assertChange(DiffChange change, Class diffClass, int first0, int last0, int first1, int last1) {
		assertTrue(diffClass.isAssignableFrom(change.getClass()));
		assertEquals(first0, change.getFirst0());
		assertEquals(first1, change.getFirst1());
		assertEquals(last0, change.getLast0());
		assertEquals(last1, change.getLast1());
	}

	/*
	public static void main(String[] args)
	{
		File file01 = new FileResource("TestDiff/TestJava0-v1.java");
		File file02 = new FileResource("TestDiff/TestJava0-v2.java");
		File file11 = new FileResource("TestDiff/TestJava1-v1.java");
		File file12 = new FileResource("TestDiff/TestJava1-v2.java");
		File file21 = new FileResource("TestDiff/TestJava2-v1.java");
		File file22 = new FileResource("TestDiff/TestJava2-v2.java");
		File file31 = new FileResource("TestDiff/TestJava3-v1.java");
		File file32 = new FileResource("TestDiff/TestJava3-v2.java");
		File file41 = new File("/Users/sylvain/temp/merge/branchNew.xml");
		File file42 = new File("/Users/sylvain/temp/merge/branchOld.xml");

		try {
			DiffReport report0 = ComputeDiff.diff(file01,file02);
			DiffReport report1 = ComputeDiff.diff(file11,file12);
			DiffReport report2 = ComputeDiff.diff(file21,file22);
			DiffReport report3 = ComputeDiff.diff(file31,file32);
			DiffReport report4 = ComputeDiff.diff(file41,file42);


			final JDialog dialog = new JDialog((Frame)null,true);

			JButton closeButton = new JButton("Close");
			closeButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					dialog.dispose();
					System.exit(0);
				}
			});

			JPanel panel = new JPanel(new BorderLayout());

			JTabbedPane tabbedPane = new JTabbedPane();
			tabbedPane.add(new DiffPanel(report0,TokenMarkerStyle.Java),"Report-0");
			tabbedPane.add(new DiffPanel(report1,TokenMarkerStyle.Java),"Report-1");
			tabbedPane.add(new DiffPanel(report2,TokenMarkerStyle.Java),"Report-2");
			tabbedPane.add(new DiffPanel(report3,TokenMarkerStyle.Java),"Report-3");
			tabbedPane.add(new DiffPanel(report4,TokenMarkerStyle.Java),"Report-4");

			JEditTextArea editorPane = new JEditTextArea();
			editorPane.setEditable(true);
			editorPane.setTokenMarker(new JavaTokenMarker());
			editorPane.setText(FileUtils.fileContents(file01));
			editorPane.setFont(new Font("Verdana",Font.PLAIN,11));

			tabbedPane.add(editorPane,"Code");

			panel.add(tabbedPane,BorderLayout.CENTER);
			panel.add(closeButton,BorderLayout.SOUTH);

			dialog.setPreferredSize(new Dimension(1000,800));
			dialog.getContentPane().add(panel);
			dialog.validate();
			dialog.pack();
			dialog.setVisible(true);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}*/

}
