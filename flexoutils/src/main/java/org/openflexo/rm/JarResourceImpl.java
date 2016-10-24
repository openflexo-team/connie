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

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Logger;
import java.util.regex.Pattern;

/**
 * 
 * Implementation of a {@link Resource} representing contents stored in a Jar file<br>
 * Contents of a {@link JarResourceImpl} are instantiated as {@link InJarResourceImpl}<br>
 * Use {@link #getInJarResource(String)} to retrieve an {@link InJarResourceImpl} contained in this resource
 * 
 * 
 * @author xtof, sylvain
 *
 */

public class JarResourceImpl extends BasicResourceImpl implements Resource {

	private static final Logger LOGGER = Logger.getLogger(JarResourceImpl.class.getPackage().getName());

	// Stores contained InJarResourceImpl where key is the relative path name
	private Map<String, InJarResourceImpl> cache;
	// Stores contained InJarResourceImpl where key is the jar entry
	private Map<JarEntry, InJarResourceImpl> contents;
	private JarFile jarfile = null;

	private InJarResourceImpl rootEntry;

	private String jarfilename = null;

	public JarResourceImpl(ResourceLocatorDelegate locator, String filename) throws MalformedURLException {
		super(locator);
		this._relativePath = filename;
		jarfile = null;
		try {
			jarfile = new JarFile(filename);
		} catch (UnsupportedEncodingException e) {
			LOGGER.severe("Unable to create JarResource with filename: " + filename);
			e.printStackTrace();
		} catch (IOException e) {
			LOGGER.severe("Unable to create JarResource with filename: " + filename);
			e.printStackTrace();
		}
		if (jarfile != null) {
			_url = new URL("file:" + filename);
			jarfilename = filename;
		}
		else {
			LOGGER.severe("Unable to create JarResource with filename: " + filename);
			return;
		}

		// Add the jarResource in the locator resource list if not contained
		/*if(locator instanceof ClasspathResourceLocatorImpl){
			ClasspathResourceLocatorImpl classPathLocator = (ClasspathResourceLocatorImpl)locator;
			if(classPathLocator.getJarResourcesList().get(this)==null){
				classPathLocator.getJarResourcesList().put(this.getRelativePath(), this);
			}
		}*/

		try {
			loadJarFile();
		} catch (LocatorNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public JarResourceImpl(ResourceLocatorDelegate locator, JarFile jarFile) throws MalformedURLException {
		super(locator);
		this._relativePath = jarFile.getName();
		jarfilename = jarFile.getName();
		this.jarfile = jarFile;
		if (jarfile != null) {
			_url = new URL("file:" + jarFile.getName());
			jarfilename = jarFile.getName();
		}
		else {
			LOGGER.severe("Unable to create JarResource with filename: " + jarFile.getName());
			return;
		}

		try {
			loadJarFile();
		} catch (LocatorNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public JarFile getJarfile() {
		return jarfile;
	}

	public String getJarFileName() {
		if (jarfilename != null && jarfilename.contains("/")) {
			return jarfilename.substring(jarfilename.lastIndexOf("/") + 1);
		}
		return jarfilename;
	}

	public String getFullQualifiedJarFileName() {
		return jarfilename;
	}

	public InJarResourceImpl getRootEntry() {
		return rootEntry;
	}

	@Override
	public Resource getContainer() {
		return null;
	}

	@Override
	public boolean isContainer() {
		return true;
	}

	@Override
	public boolean isReadOnly() {
		return true;
	}

	private void loadJarFile() throws MalformedURLException, LocatorNotFoundException {

		// System.out.println("Loading " + jarfile+" fileName="+jarfilename);

		cache = new HashMap<>();
		contents = new HashMap<>();
		URL url = getURL();

		rootEntry = new InJarResourceImpl("/", new URL("jar", url.getHost(), "file:" + jarfilename + "!/"));
		rootEntry.setJarResource(this);
		rootEntry.setName("");

		Enumeration<JarEntry> entries = jarfile.entries(); // gives ALL entries in jar

		while (entries.hasMoreElements()) {

			JarEntry current = entries.nextElement();
			String name = current.getName();
			if (name.endsWith("/")) {
				name = name.substring(0, name.length() - 1);
			}
			String shortName = name.substring(name.lastIndexOf("/") + 1);

			InJarResourceImpl inJarResource = new InJarResourceImpl(name,
					new URL("jar", url.getHost(), "file:" + jarfilename + "!/" + name));
			inJarResource.setJarResource(this);
			inJarResource.setEntry(current);
			contents.put(current, inJarResource);
			if (name.equals(rootEntry.getName() + shortName)) {
				inJarResource.setContainer(rootEntry);
			}
			// We build the hierarchy of InJarResourceImpl, inside the jar file
			for (JarEntry potentialParent : contents.keySet()) {
				if (name.equals(potentialParent.getName() + shortName)) {
					inJarResource.setContainer(contents.get(potentialParent));
				}
			}

			// Register the inJarResource in the cache of the locator, when relevant
			/*if (getLocator() instanceof ClasspathResourceLocatorImpl) {
				((ClasspathResourceLocatorImpl)getLocator()).registerResource(inJarResource);
			}*/

			// Put the InJarResourceImpl in the cache of the JarResourceImpl
			cache.put(inJarResource.getRelativePath(), inJarResource);

		}

		// System.out.println("DONE. Loaded " + jarfile+" fileName="+jarfilename);

	}

	public InJarResourceImpl getInJarResource(String relativePath) {
		return cache.get(relativePath);
	}

	@Override
	public List<? extends Resource> getContents() {
		return new ArrayList<Resource>(contents.values());
	}

	@Override
	public List<? extends Resource> getContents(Pattern pattern) {
		List<Resource> retval = new ArrayList<Resource>();
		for (JarEntry current : contents.keySet()) {
			String name = current.getName();
			boolean accept = pattern.matcher(name).matches();
			if (accept) {
				retval.add(contents.get(current));
			}
		}
		return retval;
	}

	public List<? extends Resource> getContents(String startpath, Pattern pattern) {
		List<Resource> retval = new ArrayList<Resource>();
		for (JarEntry current : contents.keySet()) {
			String name = current.getName();
			boolean accept = pattern.matcher(name).matches();
			if (name.startsWith(startpath) && accept) { // filter according to the path
				retval.add(contents.get(current));
			}
		}
		return retval;
	}

	@Override
	public void setURI(String anURI) {
		return;
	}

	@Override
	public String getRelativePath() {
		return jarfilename;

	}

	public InputStream openInputStream(JarEntry entry) {
		if (jarfile != null) {
			try {
				return jarfile.getInputStream(entry);
			} catch (IOException e) {
				LOGGER.severe("Unable to access Resource");
				e.printStackTrace();
			}
		}
		return null;
	}

	@Override
	public String computeRelativePath(Resource resource) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Resource locateResource(String relativePathName) {
		// TODO Auto-generated method stub
		return null;
	}
}
