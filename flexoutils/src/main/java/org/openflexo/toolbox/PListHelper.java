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

/**
 * @author gpolet
 * 
 */
public class PListHelper {
	public static int getInteger(Object o) throws NullPointerException, NumberFormatException {
		if (o == null) {
			throw new NullPointerException();
		}
		if (o instanceof String) {
			return Integer.parseInt((String) o);
		} else if (o instanceof Number) {
			return ((Number) o).intValue();
		} else {
			System.err.println("Don't know how to convert from " + o.getClass().getName() + " to int");
			return 0;
		}
	}

	public static boolean getBoolean(Object o) throws NullPointerException {
		if (o == null) {
			throw new NullPointerException();
		}
		if (o instanceof String) {
			String s = ((String) o).toLowerCase();
			return s.equals("true") || s.equals("y") || s.equals("yes");
		} else if (o instanceof Boolean) {
			return ((Boolean) o).booleanValue();
		} else {
			System.err.println("Don't know how to convert from " + o.getClass().getName() + " to boolean");
			return false;
		}
	}

	public static Object getObject(Boolean o) throws NullPointerException {
		if (o == null) {
			throw new NullPointerException();
		}
		if (o) {
			return "Y";
		} else {
			return "N";
		}
	}

	public static Object getObject(Integer o) throws NullPointerException {
		if (o == null) {
			throw new NullPointerException();
		}
		return String.valueOf(o);
	}
}
