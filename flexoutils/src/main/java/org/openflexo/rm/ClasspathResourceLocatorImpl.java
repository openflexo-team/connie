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

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Logger;
import java.util.regex.Pattern;

/**
 * @author C. Guychard
 * 
 *        ResourceLocator that looks for resources in the ClassPath
 */


public class ClasspathResourceLocatorImpl implements ResourceLocatorDelegate {

	private static final Logger logger = Logger.getLogger(ClasspathResourceLocatorImpl.class.getPackage().getName());
	private static final ClassLoader cl = ClassLoader.getSystemClassLoader();
	private static final String PATH_SEP = "/";

	private final Map<String, Resource> JarResourcesList;

	public ClasspathResourceLocatorImpl(){

		JarResourcesList = new Hashtable<String, Resource>();
	}

	@Override
	public  Resource locateResource(String relativePathName) {

		Resource resourceLocation = null;

		try {
			URL url = cl.getResource(relativePathName);
			if (url != null) {
				if (url.getProtocol().equals("file")){
					resourceLocation = new FileResourceImpl(this,relativePathName,url);

				}
				else {
					resourceLocation = new InJarResourceImpl(this,relativePathName,url);
					String jarPath = url.getPath().substring(5, url.getPath().indexOf("!")); //strip out only the JAR file
					Resource parent = JarResourcesList.get(jarPath);
					if (parent == null){
						parent = new JarResourceImpl(this,jarPath);
						if (parent != null){
							resourceLocation.setContainer(parent);
							JarResourcesList.put(jarPath, parent);
						}
					}
				}
			}
		}
		catch (Exception e)  {
			logger.severe("Did Not find Resource in classpath " + relativePathName + " got: " + resourceLocation);
			e.printStackTrace();
		}
		return resourceLocation;

	}


	@Override
	public Resource locateResourceWithBaseLocation(
			Resource baseLocation, String relativePath) {

		return locateResource(baseLocation.getRelativePath() + PATH_SEP + relativePath);

	}

	@Override
	public File retrieveResourceAsFile(Resource rl) {

		File locateFile = null;

		if (rl != null && rl instanceof BasicResourceImpl){
			URL url = ((BasicResourceImpl) rl).getURL();
			try {
				if (url.getProtocol().equalsIgnoreCase("file")){
					locateFile = new File(url.toURI());
				}
				else {
					logger.warning("Resource found is not convertible to a File " + url.toString() );
					locateFile = null;
				}
			} catch (URISyntaxException e) {
				locateFile = null;
			}catch (Exception e) {
				locateFile = null;		
				logger.warning("Did Not find Resource in classpath : " + url );
				e.printStackTrace();
			}
		}
		return locateFile;
	}

	public String toString(){
		return this.getClass().getSimpleName();
	}


}
