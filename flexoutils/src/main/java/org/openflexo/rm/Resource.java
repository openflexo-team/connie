/*
 * (c) Copyright 2014- Openflexo
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
	 * A Resource is retrieved using a ResourceLocator, so each Resource should point to a unique
	 * {@link ResourceLocatorDelegate} that was used to find it.
	 * 
	 */
	// TODO : check how it will applied for FlexoResources
	public ResourceLocatorDelegate getLocator();
	
	/**
	 * Path relative to parent, or the relative path initially used by the ResourceLocator to
	 * find the Resource
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
	 * Returns a list of resources contained by this resource.
	 * 
	 * @return the list of contained resources.
	 */
	public List<? extends Resource> getContents();
	public List<? extends Resource> getContents(Pattern pattern);

	
	/**
	 * A Resource is a Placeholder for a location storing information, so it can be used to get data
	 * from an InputStream, and write to an OutputStream when Resource si modifiable.
	 * 
	 */	
	// TODO : check how it will applied for FlexoResources
	public InputStream openInputStream();

	/**
	 * A Resource is a Placeholder for a location storing information, so it can be used to get data
	 * from an InputStream, and write to an OutputStream when Resource si modifiable.
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

	
}
