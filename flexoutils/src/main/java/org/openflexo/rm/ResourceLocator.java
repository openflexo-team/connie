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
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author xtof
 * 
 *         A Flexible Resource Locator that enables to get resources from multiple locations using delegates A single deletage per
 *         ResourceDelegateLocator implementing class is authorized
 */

final public class ResourceLocator {

	private static final Logger LOGGER = Logger.getLogger(ResourceLocator.class.getPackage().getName());

	static final private ArrayList<ResourceLocatorDelegate> _delegatesOrderedList = new ArrayList<>();
	static final private Map<Class<?>, ResourceLocatorDelegate> _delegatesListMap = new Hashtable<>();

	private final static ClasspathResourceLocatorImpl _cprl = new ClasspathResourceLocatorImpl();
	private final static ResourceLocator _instance = new ResourceLocator();

	ResourceLocator() {
		resetConfiguration();
	}

	static public ResourceLocatorDelegate getInstanceForLocatorClass(Class<?> aClass) {
		return _delegatesListMap.get(aClass);
	}

	/**
	 * Locate a Resource<br>
	 * Return first found resource according to the order in which all {@link ResourceLocatorDelegate} are registered.
	 * 
	 * @param relativePath
	 * @return
	 */
	public static Resource locateResource(String relativePath) {
		if (relativePath == null) {
			return null;
		}
		Resource foundResource = null;
		Iterator<ResourceLocatorDelegate> delegateIt = _delegatesOrderedList.iterator();
		while (delegateIt.hasNext() && foundResource == null) {
			ResourceLocatorDelegate del = delegateIt.next();
			// System.out.println("> Searching " + relativePath + " in " + del);
			foundResource = del.locateResource(relativePath);
			if (foundResource != null) {
				return foundResource;
			}
			// System.out.println("> Searched " + relativePath + " in " + del + " found " + foundResource);
		}
		if (LOGGER.isLoggable(Level.WARNING)) {
			LOGGER.warning("Could not locate resource " + relativePath);
			Thread.dumpStack();
		}

		return null;
	}

	/**
	 * Locate some {@link Resource}<br>
	 * Return list of Resource of the first {@link ResourceLocatorDelegate} which contains at least one {@link Resource} matching supplied
	 * relative path. Computation is performed according to the order in which all {@link ResourceLocatorDelegate} are registered.
	 * 
	 * @param relativePath
	 * @return
	 */
	public static List<Resource> locateAllResources(String relativePath) {
		if (relativePath == null) {
			return null;
		}
		List<Resource> foundResources = new ArrayList<>();
		Iterator<ResourceLocatorDelegate> delegateIt = _delegatesOrderedList.iterator();
		while (delegateIt.hasNext()) {
			ResourceLocatorDelegate del = delegateIt.next();
			List<? extends Resource> foundResourcesInDelegate = del.locateAllResources(relativePath);
			if (foundResourcesInDelegate != null && foundResourcesInDelegate.size() > 0) {
				foundResources.addAll(foundResourcesInDelegate);
			}
			// System.out.println("> Searched "+relativePath+" in " + del + " found " + location);
		}
		if (foundResources.size() == 0 && LOGGER.isLoggable(Level.WARNING)) {
			LOGGER.warning("Could not locate resource " + relativePath);
			Thread.dumpStack();
		}

		return foundResources;
	}

	private static SourceCodeResourceLocatorImpl sourceCodeResourceLocator;

	public static SourceCodeResourceLocatorImpl getSourceCodeResourceLocator() {
		if (sourceCodeResourceLocator == null) {
			sourceCodeResourceLocator = new SourceCodeResourceLocatorImpl();
		}
		return sourceCodeResourceLocator;
	}

	/**
	 * Explicitly locate a Resource in the source code (when source code is available)<br>
	 * 
	 * @param resource
	 * @param regexFilter
	 *            an additional regexFilter for disambiguation when several resources are found.
	 * @return
	 */
	static public Resource locateSourceCodeResource(Resource resource, String regexFilter) {
		return getSourceCodeResourceLocator().locateResource(resource.getRelativePath(), regexFilter);

	}

	/**
	 * Explicitly locate a Resource in the source code (when source code is available)<br>
	 * 
	 * @param resource
	 * @return
	 */
	static public Resource locateSourceCodeResource(Resource resource) {
		if (resource != null) {
			return getSourceCodeResourceLocator().locateResource(resource.getRelativePath());
		}
		return null;

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
				LOGGER.warning("A delegate for that class (" + newdelegate.getClass().getName() + " already exists");
				if (dl.equals(newdelegate)) {
					_delegatesOrderedList.remove(dl);
					_delegatesOrderedList.add(dl);
				}
				else {
					LOGGER.severe("The newdelegate is not added as it conflicts with existing one");
				}
			}
			else {
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
				LOGGER.warning("A delegate for that class (" + newdelegate.getClass().getName() + " already exists");
				if (dl.equals(newdelegate)) {
					_delegatesOrderedList.remove(dl);
					_delegatesOrderedList.add(0, dl);
				}
				else {
					LOGGER.severe("The newdelegate is not added as it conflicts with existing one");
				}
			}
			else {
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
		}
		else {
			LOGGER.warning("Cannot retrieve a File for a null location");
			return null;
		}
	}

}
