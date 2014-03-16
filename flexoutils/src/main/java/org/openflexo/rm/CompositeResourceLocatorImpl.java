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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Pattern;

/**
 * @author c guychard
 * 
 *         A Flexible Resource Locator that enables to get resources from multiple locations using delegates
 */

final public class CompositeResourceLocatorImpl implements ResourceLocator  {

	private static final Logger logger = Logger.getLogger(CompositeResourceLocatorImpl.class.getPackage().getName());

	private ArrayList<ResourceLocator>  _delegatesList;

	private static ClasspathResourceLocatorImpl _cprl = new ClasspathResourceLocatorImpl();
	private static FileSystemResourceLocatorImpl _defaultFSrl = new FileSystemResourceLocatorImpl();
	private static CompositeResourceLocatorImpl _instance = new CompositeResourceLocatorImpl();
	
	
	CompositeResourceLocatorImpl() { 
		_delegatesList = new ArrayList<ResourceLocator>();
		this.resetConfiguration();
	}
	
	@Override
	public Resource locateResource(String relativePath) {
		Resource location = null;
		Iterator<ResourceLocator> delegateIt = _delegatesList.iterator();
		while (delegateIt.hasNext() && location == null){
			ResourceLocator del = delegateIt.next();
			location = del.locateResource(relativePath);
		}
		return location;
	}
	

	@Override
	public Resource locateResourceWithBaseLocation(Resource baseLocation, String relativePath) {
		return baseLocation.getLocator().locateResourceWithBaseLocation(baseLocation, relativePath);
	}

	@Override
	public List<Resource> listResources(Resource dir,
			Pattern pattern) {
		if (dir != null){
			return dir.getLocator().listResources(dir, pattern);
		}
		return null;
	}

	/*
	@Override
	public List<Resource> listAllResources(Resource dir) {
		if (dir != null){
			return dir.getLocator().listAllResources(dir);
		}
		return null;
	}
*/
	
	@Override
	public File retrieveResourceAsFile(Resource location) {
		if (location != null){
			return location.getLocator().retrieveResourceAsFile(location);
		}
		else {
			logger.warning("Cannot retrieve a File for a Null Location!");
			return null;
		}
	}

	/*
	@Override
	public InputStream retrieveResourceAsInputStream(
			Resource location) {
		return location.getLocator().retrieveResourceAsInputStream(location);
	}
	*/


	
	/**
	 * 
	 * @param delegate
	 */

	public void removeDelegate (ResourceLocator delegate){
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
	public void appendDelegate (ResourceLocator newdelegate){
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
	public void prependDelegate (ResourceLocator newdelegate){
		if (newdelegate != null) {
			_delegatesList.add(0,newdelegate);
		}
	}
	
	/**
	 * get the singleton instance
	 * @return FlexibleResourceLocator
	 */

	public static CompositeResourceLocatorImpl getResourceLocator(){
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
	public FileSystemResourceLocatorImpl getDefaultFSResourceLocator(){
		return this._defaultFSrl;
	}

	/**
	 * returns the ClasspathResourceLocator
	 * @return 
	 */
	public ClasspathResourceLocatorImpl getDefaultCPResourceLocator(){
		return this._cprl;
	}


}
