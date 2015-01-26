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

package org.openflexo.toolbox;

import java.util.Date;

import junit.framework.TestCase;

public class TestDateUtils extends TestCase {

	public void testParseDate() {
		// Pattern d/M/y must be used
		Date[] dates = DateUtils.parseDate(new String[] { "1/1/2010", "2/2/2010", "3/2/10" });
		assertTrue(dates[0].toString().equals("Fri Jan 01 00:00:00 CET 2010"));
		assertTrue(dates[1].toString().equals("Tue Feb 02 00:00:00 CET 2010"));
		assertTrue(dates[2].toString().equals("Wed Feb 03 00:00:00 CET 2010"));

		// Pattern M/d/y must be used
		dates = DateUtils.parseDate(new String[] { "1/5/2010", "2/4/2010", "3/13/10" });
		assertTrue(dates[0].toString().equals("Tue Jan 05 00:00:00 CET 2010"));
		assertTrue(dates[1].toString().equals("Thu Feb 04 00:00:00 CET 2010"));
		assertTrue(dates[2].toString().equals("Sat Mar 13 00:00:00 CET 2010"));

		// Pattern d/M/y H'h'm must be used
		dates = DateUtils.parseDate(new String[] { "1/1/2010 12h24", "2/2/2010 21h56", "3/2/10 01h01" });
		assertTrue(dates[0].toString().equals("Fri Jan 01 12:24:00 CET 2010"));
		assertTrue(dates[1].toString().equals("Tue Feb 02 21:56:00 CET 2010"));
		assertTrue(dates[2].toString().equals("Wed Feb 03 01:01:00 CET 2010"));

		assertNull(DateUtils.parseDate(new String[] { "1/1/2010 12h24", "2/2/2010 21t56", "3/2/10 01h01" }));
	}
}
