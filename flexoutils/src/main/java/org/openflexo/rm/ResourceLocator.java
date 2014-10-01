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
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Logger;

/**
 * @author c guychard
 * 
 *         A Flexible Resource Locator that enables to get resources from multiple locations using delegates A single deletage per
 *         ResourceDelegateLocator implementing class is authorized
 */

final public class ResourceLocator {

	private static final Logger logger = Logger.getLogger(ResourceLocator.class.getPackage().getName());

	static final private ArrayList<ResourceLocatorDelegate> _delegatesOrderedList = new ArrayList<ResourceLocatorDelegate>();
	static final private Map<Class, ResourceLocatorDelegate> _delegatesListMap = new Hashtable<Class, ResourceLocatorDelegate>();

	private final static ClasspathResourceLocatorImpl _cprl = new ClasspathResourceLocatorImpl();
	private final static ResourceLocator _instance = new ResourceLocator();

	ResourceLocator() {
		resetConfiguration();
	}

	static public ResourceLocatorDelegate getInstanceForLocatorClass(Class aClass) {
		return _delegatesListMap.get(aClass);
	}

	/**
	 * Locate a Resource<br>
	 * This lookup is performed according to the order in which all {@link ResourceLocatorDelegate} are registered.
	 * 
	 * @param relativePath
	 * @return
	 */
	static public Resource locateResource(String relativePath) {
		if (relativePath == null) {
			return null;
		}
		Resource location = null;
		Iterator<ResourceLocatorDelegate> delegateIt = _delegatesOrderedList.iterator();
		while (delegateIt.hasNext() && location == null) {
			ResourceLocatorDelegate del = delegateIt.next();
			location = del.locateResource(relativePath);
			// System.out.println("> Searched in " + del + " found " + location);
		}
		return location;
	}

	private static SourceCodeResourceLocatorImpl sourceCodeResourceLocator;

	public static SourceCodeResourceLocatorImpl getSourceCodeResourceLocator() {
		if (sourceCodeResourceLocator == null) {
			sourceCodeResourceLocator = new SourceCodeResourceLocatorImpl();
		}
		return sourceCodeResourceLocator;
	}

	/**
	 * Explicitely locate a Resource in the source code (when source code is available)<br>
	 * 
	 * @param resource
	 * @return
	 */
	static public Resource locateSourceCodeResource(Resource resource) {
		return getSourceCodeResourceLocator().locateResource(resource.getRelativePath());

	}

	/**
	 * Explicitely locate a Resource in the source code (when source code is available)<br>
	 * 
	 * @param relativePath
	 * @return
	 */
	static public Resource locateSourceCodeResource(String relativePath) {
		return getSourceCodeResourceLocator().locateResource(relativePath);

	}

	/**
	 * 
	 * @param delegate
	 */

	static public void removeDelegate(ResourceLocatorDelegate delegate) {
		if (delegate != null) {
			_delegatesOrderedList.remove(delegate);
		}
	}

	/**
	 * 
	 * Adds a new delegate at the end of the list, if a delegate of the given class is already present in delegates list. If a delegate of
	 * that class already exists, moves it to the end of the list if its the same reference as the one already in the delegates list else,
	 * does nothing
	 * 
	 * @param newdelegate
	 */
	static public void appendDelegate(ResourceLocatorDelegate newdelegate) {
		if (newdelegate != null) {
			ResourceLocatorDelegate dl = _delegatesListMap.get(newdelegate.getClass());
			if (dl != null) {
				logger.warning("A delegate for that class (" + newdelegate.getClass().getName() + " already exists");
				if (dl.equals(newdelegate)) {
					_delegatesOrderedList.remove(dl);
					_delegatesOrderedList.add(dl);
				} else {
					logger.severe("The newdelegate is not added as it conflicts with existing one");
				}
			} else {
				_delegatesOrderedList.add(newdelegate);
				_delegatesListMap.put(newdelegate.getClass(), newdelegate);
			}
		}
	}

	/**
	 * 
	 * Adds a new delegate at the beginning of the list, if a delegate of the given class is already present in delegates list. If a
	 * delegate of that classe already exists, moves it to the beginning of the list if its the same reference as the one already in the
	 * delegates list else, does nothing
	 * 
	 * @param newdelegate
	 */
	static public void prependDelegate(ResourceLocatorDelegate newdelegate) {
		if (newdelegate != null) {
			ResourceLocatorDelegate dl = _delegatesListMap.get(newdelegate.getClass());
			if (dl != null) {
				logger.warning("A delegate for that class (" + newdelegate.getClass().getName() + " already exists");
				if (dl.equals(newdelegate)) {
					_delegatesOrderedList.remove(dl);
					_delegatesOrderedList.add(0, dl);
				} else {
					logger.severe("The newdelegate is not added as it conflicts with existing one");
				}
			} else {
				_delegatesOrderedList.add(0, newdelegate);
				_delegatesListMap.put(newdelegate.getClass(), newdelegate);
			}
		}
	}

	/**
	 * get the singleton instance
	 * 
	 * @return FlexibleResourceLocator
	 */

	static public ResourceLocator getResourceLocator() {
		return _instance;
	}

	/**
	 * clears ResourceLocator Configuration: removes all existing delegates from list
	 */
	static public void clearConfiguration() {
		_delegatesOrderedList.clear();
	}

	/**
	 * resets ResourceLocator Configuration: removes all existing delegates from list and adds only the ClassPath ResourceLocator
	 */
	static public void resetConfiguration() {
		_delegatesOrderedList.clear();
		appendDelegate(_cprl);
	}

	@Deprecated
	public static Resource locateResourceWithBaseLocation(Resource baseLocation, String relativePath) {
		return baseLocation.getLocator().locateResourceWithBaseLocation(baseLocation, relativePath);
	}

	@Deprecated
	public static File retrieveResourceAsFile(Resource location) {
		if (location != null) {
			return location.getLocator().retrieveResourceAsFile(location);
		} else {
			logger.warning("Cannot retrieve a File for a null location");
			return null;
		}
	}

}
