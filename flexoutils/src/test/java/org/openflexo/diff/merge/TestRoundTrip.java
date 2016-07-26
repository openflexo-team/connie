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

package org.openflexo.diff.merge;

public class TestRoundTrip {

	/*public static void main(String[] args) 
	{
		File generatedFile1 = new FileResource("TestRoundTrip/Generated1.txt");
		File acceptedFile1 = new FileResource("TestRoundTrip/Accepted1.txt");
		File generatedFile2 = new FileResource("TestRoundTrip/Generated2.txt");
		
		try {
			DiffReport report0 = ComputeDiff.diff(generatedFile1,acceptedFile1);
			
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
			tabbedPane.add(new DiffPanel(report0,TokenMarkerStyle.Java,"GeneratedFile1","AcceptedFile1","No diffs",true),"Initial");

			Merge merge = new Merge(
					new DiffSource(generatedFile1),
					new DiffSource(generatedFile2),
					new DiffSource(acceptedFile1),
					DefaultMergedDocumentType.JAVA);
			
			MergeEditor editor = new MergeEditor(merge) {
				@Override
				public void done() {
					dialog.dispose();
				}
			};

			tabbedPane.add(editor,"Generation merge");			
			
			panel.add(tabbedPane,BorderLayout.CENTER);
			panel.add(closeButton,BorderLayout.SOUTH);
			
			dialog.setPreferredSize(new Dimension(1000,800));
			dialog.getContentPane().add(panel);
			dialog.validate();
			dialog.pack();
			dialog.setVisible(true);
			dialog.repaint();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}*/
}
