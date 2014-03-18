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
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Logger;
import java.util.regex.Pattern;

/**
 * a Resource located in a Jar, from the Classpath, that is not editable
 * 
 * @author xtof
 *
 */
public class InJarResourceImpl extends BasicResourceImpl {


	private static final Logger logger = Logger.getLogger(InJarResourceImpl.class.getPackage().getName());
	
	public InJarResourceImpl(ResourceLocatorDelegate delegate, String initialPath,
			URL url) {
		super(delegate, initialPath, url);

	}


	public InJarResourceImpl(String initialPath, URL url) {
		super(ResourceLocator.getInstanceForLocatorClass(ClasspathResourceLocatorImpl.class), initialPath, url);

	}

	
	@Override
	public List<? extends Resource> getContents(Pattern pattern) {

		URL url = getURL();
		
		JarResourceImpl container = (JarResourceImpl) getContainer();
		
		if (container == null) {
			// finds the container
			String jarPath = url.getPath().substring(5, url.getPath().indexOf("!")); //strip out only the JAR file
			try {
				container = new JarResourceImpl(ResourceLocator.getInstanceForLocatorClass(ClasspathResourceLocatorImpl.class), jarPath);
			} catch (MalformedURLException e) {
				logger.severe("Unable to retrieve containing JarFile: " + jarPath);
				e.printStackTrace();
				return java.util.Collections.emptyList();
			}
			this.setContainer(container);
		}

		String startpath = getRelativePath();
		return container.getContents(startpath, pattern);
		
	}

}
