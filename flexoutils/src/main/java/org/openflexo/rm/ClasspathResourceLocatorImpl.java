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
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * An implementation of {@link ResourceLocatorDelegate} that looks for resources in the ClassPath<br>
 * Only one instance of this class should be instantiated in the application, generally in the {@link ResourceLocator}
 * 
 * @see ResourceLocator
 * @author C. Guychard, Sylvain
 * 
 */

public class ClasspathResourceLocatorImpl implements ResourceLocatorDelegate {

	private static final Logger LOGGER = Logger.getLogger(ClasspathResourceLocatorImpl.class.getPackage().getName());

	private static final ClassLoader cl = ClasspathResourceLocatorImpl.class.getClassLoader();

	public static final String PATH_SEP = "/";

	private final Map<String, JarResourceImpl> jarResources;

	private final Map<String, List<Resource>> cache = new HashMap<>();

	public ClasspathResourceLocatorImpl() {

		jarResources = new Hashtable<String, JarResourceImpl>();
	}

	@Override
	public Resource locateResource(String relativePathName) {

		if (relativePathName == null) {
			return null;
		}

		List<Resource> foundResources = locateAllResources(relativePathName);
		if (foundResources.size() > 0) {
			return foundResources.get(0);
		}

		return null;

	}

	@Override
	public List<Resource> locateAllResources(String relativePathName) {

		if (relativePathName == null) {
			return null;
		}

		// First look in the cache
		List<Resource> resourceLocations = cache.get(relativePathName);

		if (resourceLocations != null) {
			return resourceLocations;
		}

		// When not found, perform search in the whole classpath
		ArrayList<Resource> returned = new ArrayList<Resource>();
		cache.put(relativePathName, returned);

		try {
			// First, use the ClassLoader to lookup the resource
			// List of URLs are returned
			Enumeration<URL> urlList = cl.getResources(relativePathName);

			if (urlList != null && urlList.hasMoreElements()) {

				while (urlList.hasMoreElements()) {
					URL url = urlList.nextElement();

					Resource resourceLocation = null;

					if (url.getProtocol().equals("file")) {
						// If protocol is file, just instanciate the FileResourceImpl
						resourceLocation = new FileResourceImpl(this, relativePathName, url);
					}
					else if (url.getProtocol().equals("jar")) {
						// If protocol is jar, we have to lookup an InJarResourceImpl in a JarResourceImpl
						String jarPath = URLDecoder.decode(url.getPath().substring(5, url.getPath().indexOf("!")).replace("+", "%2B"),
								"UTF-8");
						JarResourceImpl jarResource = jarResources.get(jarPath);
						if (jarResource == null) {
							jarResource = new JarResourceImpl(this, jarPath);
							if (jarResource != null) {
								jarResources.put(jarPath, jarResource);
							}
						}
						// Retrieve right InJarResourceImpl from the JarResourceImpl
						resourceLocation = jarResource.getInJarResource(relativePathName);
					}
					if (resourceLocation != null) {
						returned.add(resourceLocation);
					}

				}
			}
		} catch (Exception e) {
			LOGGER.warning("Did Not find Resource in classpath " + relativePathName);
			e.printStackTrace();
		}

		return returned;

	}

	@Override
	public Resource locateResourceWithBaseLocation(Resource baseLocation, String relativePath) {

		return locateResource(baseLocation.getRelativePath() + PATH_SEP + relativePath);

	}

	/**
	 * Return the lit of jar resources stored in this Locator
	 * 
	 * @return
	 */
	public Map<String, JarResourceImpl> getJarResourcesList() {
		return jarResources;
	}

	@Override
	public File retrieveResourceAsFile(Resource rl) {

		File locateFile = null;

		if (rl != null && rl instanceof BasicResourceImpl) {
			URL url = ((BasicResourceImpl) rl).getURL();
			try {
				if (url.getProtocol().equalsIgnoreCase("file")) {
					locateFile = new File(url.toURI());
				}
				else {
					LOGGER.info("Resource found is not convertible to a File " + url.toString());
					locateFile = null;
				}
			} catch (URISyntaxException e) {
				locateFile = null;
			} catch (Exception e) {
				locateFile = null;
				LOGGER.warning("Did Not find Resource in classpath : " + url);
				e.printStackTrace();
			}
		}
		return locateFile;
	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName();
	}

	/*protected void registerResource(InJarResourceImpl resource) {
		if (cache.get(resource.getRelativePath()) != null) {
			LOGGER.warning("Duplicated resource "+resource.getRelativePath()+" registered in the classpath");
		}
		cache.put(resource.getRelativePath(), resource);
	}*/

}
