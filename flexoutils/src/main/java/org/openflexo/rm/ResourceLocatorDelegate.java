/*
 * (c) Copyright 2013-2014 Openflexo
 *
 * This file is part of OpenFlexo.
 *
 * OpenFlexo is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OpenFlexo is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OpenFlexo. If not, see <http://www.gnu.org/licenses/>.
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
