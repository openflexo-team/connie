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


package org.openflexo.rm;

import java.io.File;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.util.List;
import java.util.regex.Pattern;

public interface ResourceLocatorDelegate {

	/**
	 *  Locates the resource given a relative PATH
	 * @param relativePath
	 * @return 
	 */
	public Resource locateResource (String relativePath);

	/**
	 *  Locates the resource given a relative PATH and a base Location
	 * @param relativePath
	 * @return 
	 */
	// TODO : voir si on ne peut pas supprimer ça avec le getContents
	public Resource locateResourceWithBaseLocation(Resource baseLocation, String relativePath);


	/**
	 *  Gets the resource pointed by URL as a File
	 * @param resourceURL
	 * @return null when resource cannot be converted to File
	 */
	public File retrieveResourceAsFile(Resource location);
	

}
