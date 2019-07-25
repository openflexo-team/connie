/**
 *
 * Copyright (c) 2019, Openflexo
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
package org.openflexo.rm;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * Resources utility functions
 *
 * @author Fabien Dagnat
 *
 */
public class Resources {
	/**
	 * Internal function to collect all the sub resources of a resource whose name end by a string
	 *
	 * @param list
	 *            the resulting list
	 * @param directory
	 *            the resource from where we search
	 * @param extension
	 *            the end of the name of the matched resources
	 */
	private static void addToList(final List<Object[]> list, final Resource directory, final String extension) {
		for (Resource f : directory.getContents()) {
			if (f.getURI().endsWith(extension)) {
				final Object[] construcArgs = { f, f.getURI().substring(f.getURI().lastIndexOf("/") + 1) };
				list.add(construcArgs);
			}
			else if (f.isContainer()) {
				addToList(list, f, extension);
			}
		}
	}

	/**
	 * Utility function to collect all the sub resources of a resource whose name end by a string
	 *
	 * @param directory
	 *            the resource from where we search
	 * @param extension
	 *            the end of the name of the matched resources
	 * @return the resulting list
	 */
	public static List<Object[]> getMatchingResource(final Resource directory, final String extension) {
		final List<Object[]> list = new ArrayList<>();
		addToList(list, directory, extension);
		return list;
	}
}
