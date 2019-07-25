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

import java.util.logging.Logger;

import org.openflexo.logging.FlexoLogger;
import org.openflexo.rm.InJarResourceImpl;

/**
 * Some Jar utilities
 *
 * @author sylvain
 */
public class JarUtils {

	private static final Logger LOGGER = FlexoLogger.getLogger(JarUtils.class.getPackage().getName());

	/**
	 * Finds a relative path to a given InJarResourceImpl, relative to a specified directory represented as a InJarResourceImpl
	 *
	 * @param inJarResource
	 *            file that the relative path should resolve to
	 * @param relativeToDir
	 *            directory that the path should be relative to
	 * @return a relative path. This always uses / as the separator character.
	 */
	public static String makePathRelativeTo(InJarResourceImpl inJarResource, InJarResourceImpl relativeToDir) {
		String canonicalFile = inJarResource.getEntry().getName();
		String canonicalRelTo = relativeToDir.getEntry().getName();
		String[] filePathComponents = FileUtils.getPathComponents(canonicalFile);
		String[] relToPathComponents = FileUtils.getPathComponents(canonicalRelTo);
		int i = 0;
		while (i < filePathComponents.length && i < relToPathComponents.length && filePathComponents[i].equals(relToPathComponents[i])) {
			i++;
		}
		StringBuffer buf = new StringBuffer();
		for (int j = i; j < relToPathComponents.length; j++) {
			buf.append("../");
		}
		for (int j = i; j < filePathComponents.length - 1; j++) {
			buf.append(filePathComponents[j]).append('/');
		}
		buf.append(filePathComponents[filePathComponents.length - 1]);
		return buf.toString();
	}

	public static int distance(InJarResourceImpl f1, InJarResourceImpl f2) {
		return Math.min(distance(f1, f2, false), distance(f2, f1, false));
	}

	private static int distance(InJarResourceImpl f1, InJarResourceImpl f2, boolean computeInverse) {
		if (f2.equals(f1)) {
			return 0;
		}
		if (f2.getContainer() != null) {
			int d1 = distance(f1, f2.getContainer());
			if (d1 < 1000) {
				return d1 + 1;
			}
		}
		if (computeInverse) {
			if (f1.getContainer() != null) {
				int d2 = distance(f2, f1.getContainer());
				if (d2 < 1000) {
					return d2 + 1;
				}
			}
		}
		return 1000;
	}

}
