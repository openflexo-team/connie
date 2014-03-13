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

package org.openflexo.toolbox;

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
	public ResourceLocation locateResource (String relativePath);

	/**
	 *  Locates the resource given a relative PATH and a base Location
	 * @param relativePath
	 * @return 
	 */
	public ResourceLocation locateResourceWithBaseLocation(ResourceLocation baseLocation, String relativePath);

	/**
	 *  Returns all The Resources found in the dir base location, and which names correspond to
	 *  given pattern recursively
	 * @param dir
	 * @param nameFilter, expression used to filter results
	 * @return 
	 */
	public List<ResourceLocation> listResources(ResourceLocation dir, Pattern pattern);

	/**
	 *  Returns all The Resources found in the dir base location
	 *  given pattern recursively
	 * @param dir
	 * @return 
	 */
	public List<ResourceLocation>  listAllResources(ResourceLocation dir);

	/**
	 *  Gets the resource pointed by URL as a File
	 * @param resourceURL
	 * @return null when resource cannot be converted to File
	 */
	public File retrieveResourceAsFile(ResourceLocation location);

	/**
	 *  Gets the resource pointed by URL as an Input Stream
	 * @param resourceURL
	 * @return null when resource cannot be converted to InputStream
	 */
	public InputStream retrieveResourceAsInputStream(ResourceLocation resourceURL);


}
