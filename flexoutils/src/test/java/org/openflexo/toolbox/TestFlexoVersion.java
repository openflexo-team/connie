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

import org.junit.Test;
import org.junit.runner.RunWith;
import org.openflexo.test.OrderedRunner;
import org.openflexo.test.TestOrder;

import junit.framework.TestCase;

@RunWith(OrderedRunner.class)
public class TestFlexoVersion extends TestCase {
	/*
		@Test
		@TestOrder(1)
		public void testValideVersion() {
	
			String[] s = { "0.9.0", "0.9.1", "0.9.0RC1", "0.9.0RC2", "0.9.0alpha", "0.9.0beta", "0.10.0", "0.10.1", "0.10.0RC1", "0.10.0RC2",
					"0.10.0alpha", "0.10.0beta", "0.9", "1.0alpha", "1.1alpha", "1.1RC8", "1.1-SNAPSHOT", "1.1.0-SNAPSHOT","1.1.0SNAPSHOT", "1.0SNAPSHOT" };
			for (int i = 0; i < s.length; i++) {
				String string = s[i];
				assertTrue(FlexoVersion.isValidVersionString(string));
				FlexoVersion v = new FlexoVersion(string);
				assertNotNull(v);
			}
	
		}
		*/
	@Test
	@TestOrder(2)
	public void testCompareVersions() {

		FlexoVersion v090 = new FlexoVersion("0.9.0");
		FlexoVersion v091 = new FlexoVersion("0.9.1");

		assertTrue(v090.isLesserThan(v091));
		assertTrue(v091.isGreaterThan(v090));
	}

}
