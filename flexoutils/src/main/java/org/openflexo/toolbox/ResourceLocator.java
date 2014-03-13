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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @author c guychard
 * 
 *         A Flexible Resource Locator that enables to get resources from multiple locations using delegates
 */

final public class ResourceLocator implements ResourceLocatorDelegate  {

	private ArrayList<ResourceLocatorDelegate>  _delegatesList;

	private static ClasspathResourceLocator _cprl = new ClasspathResourceLocator();
	private static FileSystemResourceLocator _defaultFSrl = new FileSystemResourceLocator();
	private static ResourceLocator _instance = new ResourceLocator();
	
	
	ResourceLocator() { 
		_delegatesList = new ArrayList<ResourceLocatorDelegate>();
		this.resetConfiguration();
	}
	
	@Override
	public ResourceLocation locateResource(String relativePath) {
		ResourceLocation location = null;
		Iterator<ResourceLocatorDelegate> delegateIt = _delegatesList.iterator();
		while (delegateIt.hasNext() && location == null){
			ResourceLocatorDelegate del = delegateIt.next();
			location = del.locateResource(relativePath);
		}
		return location;
	}
	

	@Override
	public ResourceLocation locateResourceWithBaseLocation(ResourceLocation baseLocation, String relativePath) {
		return baseLocation.getDelegate().locateResourceWithBaseLocation(baseLocation, relativePath);
	}

	@Override
	public List<ResourceLocation> listResources(ResourceLocation dir,
			Pattern pattern) {
		if (dir != null){
			return dir.getDelegate().listResources(dir, pattern);
		}
		return null;
	}

	@Override
	public List<ResourceLocation> listAllResources(ResourceLocation dir) {
		if (dir != null){
			return dir.getDelegate().listAllResources(dir);
		}
		return null;
	}

	
	@Override
	public File retrieveResourceAsFile(ResourceLocation location) {
		return location.getDelegate().retrieveResourceAsFile(location);
	}

	@Override
	public InputStream retrieveResourceAsInputStream(
			ResourceLocation location) {
		return location.getDelegate().retrieveResourceAsInputStream(location);
	}


	
	/**
	 * 
	 * @param delegate
	 */

	public void removeDelegate (ResourceLocatorDelegate delegate){
		if (delegate != null) {
			_delegatesList.remove(delegate);
		}
	}
	/**
	 * 
	 * Adds a new delegate at the end of the list
	 * 
	 * @param newdelegate
	 */
	public void appendDelegate (ResourceLocatorDelegate newdelegate){
		if (newdelegate != null) {
			_delegatesList.add(newdelegate);
		}
	}
	
	/**
	 * 
	 * Adds a new delegate at the beginning of the list
	 * 
	 * @param newdelegate
	 */
	public void prependDelegate (ResourceLocatorDelegate newdelegate){
		if (newdelegate != null) {
			_delegatesList.add(0,newdelegate);
		}
	}
	
	/**
	 * get the singleton instance
	 * @return FlexibleResourceLocator
	 */

	public static ResourceLocator getResourceLocator(){
		return _instance;
	}
	
	/**
	 * clears ResourceLocator Configuration: removes all existing delegates from list 
	 */
	public void clearConfiguration(){
		_delegatesList.clear();
	}
	
	/**
	 * resets ResourceLocator Configuration: removes all existing delegates from list 
	 * and adds only the ClassPath ResourceLocator
	 */
	public void resetConfiguration(){
		_delegatesList.clear();
		appendDelegate(_cprl);
	}

	/**
	 * returns the default FileSystemResourceLocator
	 * @return 
	 */
	public FileSystemResourceLocator getDefaultFSResourceLocator(){
		return this._defaultFSrl;
	}

	/**
	 * returns the ClasspathResourceLocator
	 * @return 
	 */
	public ClasspathResourceLocator getDefaultCPResourceLocator(){
		return this._cprl;
	}


}
