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

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

/**
 * 
 * Resource interface represents a generic resource, that abstracts from location or storing mechanism
 * 
 * @author xtof
 *
 */

public interface Resource {

	/**
	 * A Resource has an URI/L that uniquely identifies the place where it can be found
	 * 
	 * @return
	 */
	public String getURI();

	public void setURI(String anURI);

	/**
	 * A Resource is retrieved using a ResourceLocator, so each Resource should point to a unique {@link ResourceLocatorDelegate} that was
	 * used to find it.
	 * 
	 */
	// TODO : check how it will applied for FlexoResources
	public ResourceLocatorDelegate getLocator();

	/**
	 * Path relative to parent, or the relative path initially used by the ResourceLocator to find the Resource
	 * 
	 * @return
	 */
	public String getRelativePath();

	/**
	 * A Resource may be contained in another one, e.g., a directory
	 * 
	 */
	public Resource getContainer();

	void setContainer(Resource parent);

	public boolean isContainer();

	/**
	 * Returns a list of resources contained by this resource. (non-recursive method)
	 * 
	 * @return the list of contained resources.
	 */
	public List<? extends Resource> getContents();

	/**
	 * Returns a list of resources contained by this resource.
	 * 
	 * @param deep
	 *            when set to true, recursively return all contained resources in sub-folders
	 * 
	 * @return the list of contained resources.
	 */
	public List<? extends Resource> getContents(boolean deep);

	/**
	 * Returns a list of resources contained by this resource matching supplied pattern
	 * 
	 * @param deep
	 *            when set to true, recursively return all contained resources in sub-folders
	 * 
	 * @return the list of contained resources.
	 */
	public List<? extends Resource> getContents(Pattern pattern, boolean deep);

	/**
	 * A Resource is a Placeholder for a location storing information, so it can be used to get data from an InputStream, and write to an
	 * OutputStream when Resource si modifiable.
	 * 
	 */
	// TODO : check how it will applied for FlexoResources
	public InputStream openInputStream();

	/**
	 * A Resource is a Placeholder for a location storing information, so it can be used to get data from an InputStream, and write to an
	 * OutputStream when Resource si modifiable.
	 * 
	 * @return null when not Editable
	 * 
	 */
	// TODO : check how it will applied for FlexoResources
	public OutputStream openOutputStream();

	/**
	 * Not all Resource are editable, so Modification time maybe not be relevant
	 * 
	 */
	public boolean isReadOnly();

	/**
	 * Not all Resource are editable, so Modification time maybe not be relevant
	 * 
	 * @return Date(0), when not editable
	 */
	public Date getLastUpdate();

	/**
	 * Compute relative path to access supplied {@link Resource}, asserting this relative path is expressed relatively of this resource
	 * 
	 * @param resource
	 * @return
	 */
	public String computeRelativePath(Resource resource);

	/**
	 * Retrieve resource using supplied relative path name, asserting this relative path name represent a relative path from this resource
	 * 
	 * @param relativePathName
	 * @return
	 */
	public Resource locateResource(String relativePathName);

}
