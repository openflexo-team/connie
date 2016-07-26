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
 * @author xtof
 * 
 *         ResourceLocator that looks for resources in the ClassPath
 */

public class ClasspathResourceLocatorImpl implements ResourceLocatorDelegate {

	private static final Logger LOGGER = Logger.getLogger(ClasspathResourceLocatorImpl.class.getPackage().getName());
	private static final ClassLoader cl = ClassLoader.getSystemClassLoader();
	public static final String PATH_SEP = "/";

	private final Map<String, Resource> JarResourcesList;

	private final Map<String, Resource> cache = new HashMap<>();

	public ClasspathResourceLocatorImpl() {

		JarResourcesList = new Hashtable<>();
	}

	@Override
	public Resource locateResource(String relativePathName) {

		if (relativePathName == null) {
			return null;
		}

		Resource resourceLocation = cache.get(relativePathName);

		if (resourceLocation == null) {

			try {
				URL url = cl.getResource(relativePathName);
				if (url != null) {
					if (url.getProtocol().equals("file")) {
						resourceLocation = new FileResourceImpl(this, relativePathName, url);

					}
					else {
						String jarPath = URLDecoder.decode(url.getPath().substring(5, url.getPath().indexOf("!")).replace("+", "%2B"),
								"UTF-8");
						resourceLocation = new InJarResourceImpl(this, relativePathName, url);
						Resource parent = JarResourcesList.get(jarPath);
						if (parent == null) {
							parent = new JarResourceImpl(this, jarPath);
							if (parent != null) {
								resourceLocation.setContainer(parent);
								JarResourcesList.put(jarPath, parent);
							}
						}
					}
				}
			} catch (Exception e) {
				LOGGER.severe("Did Not find Resource in classpath " + relativePathName + " got: " + resourceLocation);
				e.printStackTrace();
			}

			cache.put(relativePathName, resourceLocation);
		}

		return resourceLocation;

	}

	public List<Resource> locateAllResources(String relativePathName) {

		if (relativePathName == null) {
			return null;
		}

		ArrayList<Resource> returned = new ArrayList<>();

		Resource resourceLocation = null;

		try {
			Enumeration<URL> urlList = cl.getResources(relativePathName);

			if (urlList != null && urlList.hasMoreElements()) {
				while (urlList.hasMoreElements()) {
					URL url = urlList.nextElement();

					if (url.getProtocol().equals("file")) {
						resourceLocation = new FileResourceImpl(this, relativePathName, url);

					}
					else {
						String jarPath = URLDecoder.decode(url.getPath().substring(5, url.getPath().indexOf("!")).replace("+", "%2B"),
								"UTF-8");
						resourceLocation = new InJarResourceImpl(this, relativePathName, url);
						Resource parent = JarResourcesList.get(jarPath);
						if (parent == null) {
							parent = new JarResourceImpl(this, jarPath);
							if (parent != null) {
								resourceLocation.setContainer(parent);
								JarResourcesList.put(jarPath, parent);
							}
						}
					}
					if (resourceLocation != null) {
						System.out.println("----- FOUND : " + resourceLocation.getURI());
						returned.add(resourceLocation);
					}
				}
			}
		} catch (Exception e) {
			LOGGER.severe("Did Not find Resource in classpath " + relativePathName + " got: " + resourceLocation);
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
	public Map<String, Resource> getJarResourcesList() {
		return JarResourcesList;
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
					LOGGER.warning("Resource found is not convertible to a File " + url.toString());
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

}
