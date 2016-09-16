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
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.logging.Logger;
import java.util.regex.Pattern;

/**
 * a Resource located in a Jar, from the Classpath, that is not editable
 * 
 * @author xtof
 *
 */
public class InJarResourceImpl extends BasicResourceImpl {

	private static final Logger LOGGER = Logger.getLogger(InJarResourceImpl.class.getPackage().getName());

	public static final String JAR_SEPARATOR = "/";

	private JarEntry entry = null;

	private InJarResourceImpl container;
	private List<InJarResourceImpl> contents = new ArrayList<>();

	public InJarResourceImpl(ResourceLocatorDelegate delegate, String initialPath, URL url) throws LocatorNotFoundException {
		super(delegate, initialPath, url);
	}

	public InJarResourceImpl(String initialPath, URL url) throws LocatorNotFoundException {
		super(ResourceLocator.getInstanceForLocatorClass(ClasspathResourceLocatorImpl.class), initialPath, url);
		((ClasspathResourceLocatorImpl) this.getLocator()).getJarResourcesList().put(initialPath, this);

		// Add the InJarResource in the locator resource list if not contained
		ClasspathResourceLocatorImpl locator = (ClasspathResourceLocatorImpl) ResourceLocator
				.getInstanceForLocatorClass(ClasspathResourceLocatorImpl.class);
		if (locator.getJarResourcesList().get(this) == null) {
			locator.getJarResourcesList().put(initialPath, this);
		}
	}

	@Override
	public InputStream openInputStream() {
		if (entry != null && _parent != null) {
			return ((JarResourceImpl) _parent).openInputStream(entry);
		}
		if (_url != null) {
			try {
				return _url.openStream();
			} catch (IOException e) {
				LOGGER.severe("Cannot open given Resource: " + _url.toString());
				e.printStackTrace();
			}
		}
		return null;
	}

	/*@Override
	public List<Resource> getContents() {
		List<Resource> resources = new ArrayList<>();
	
		List<Resource> resources = new ArrayList<Resource>();
	
		if (entry != null && entry.isDirectory()) {
			// Browser the resource of the container
			for (Resource resource : getContainer().getContents()) {
				String parentFolderPath = resource.getRelativePath();
				// If it is a folder end with "/" then remove the "/" to find the parent path
				if (parentFolderPath.endsWith("/")) {
			for (Resource resource :  getContainer().getContents()) {
				String parentFolderPath = resource.getRelativePath();
				// If it is a folder end with JAR_SEPARATOR then remove the "/" to find the parent path
				if (parentFolderPath.endsWith("/")) {
					int lastSeparator = parentFolderPath.lastIndexOf("/");
					parentFolderPath = parentFolderPath.substring(0, lastSeparator);
				}
				// Find the last separation
				if (parentFolderPath.contains("/")) {
					int lastSeparator = parentFolderPath.lastIndexOf("/");
					parentFolderPath = parentFolderPath.substring(0, lastSeparator + 1);
				}
				// Check it corresponds to this in jar resource
				if (parentFolderPath.equals(getRelativePath())) {
					resources.add(resource);
					resource.setContainer(resource);
				}
			}
			// TODO some day ...
	
	
		}
	
	
		System.out.println("Les contents de " + this + " c'est " + resources);
	
		return resources;
	
	}
	
	}*/

	@Override
	public boolean isContainer() {
		return entry.isDirectory();

	};

	@Override
	public InJarResourceImpl getContainer() {
		return container;
	}

	@Override
	public void setContainer(Resource container) {
		if (container instanceof InJarResourceImpl) {
			this.container = (InJarResourceImpl) container;
			this.container.contents.add(this);
		}
	}

	@Override
	public List<InJarResourceImpl> getContents() {
		return contents;
	}

	/*@Override
	public Resource getContainer() {
		if (super.getContainer() != null) {
			return super.getContainer();
		}
		else {
			URL url = getURL();
	
	
			JarResourceImpl container = (JarResourceImpl) this._parent;
	
	
			if (container == null) {
				// finds the container
				String jarPath = null;
				try {
					jarPath = URLDecoder.decode(url.getPath().substring(5, url.getPath().indexOf("!")).replace("+", "%2B"), "UTF-8");
				} catch (UnsupportedEncodingException e1) {
					LOGGER.severe("Unable to decode given PATH");
					e1.printStackTrace();
				}
				try {
					container = new JarResourceImpl(ResourceLocator.getInstanceForLocatorClass(ClasspathResourceLocatorImpl.class),
							jarPath);
				} catch (MalformedURLException e) {
					LOGGER.severe("Unable to retrieve containing JarFile: " + jarPath);
					e.printStackTrace();
					return (Resource) java.util.Collections.emptyList();
				}
				this.setContainer(container);
			}
			return container;
		}
	
	}*/

	@Override
	public List<? extends Resource> getContents(Pattern pattern) {
		// String startpath = getRelativePath();
		// return ((JarResourceImpl) getContainer()).getContents(startpath, pattern);
		// TODO implement this
		return getContents();
	}

	public JarEntry getEntry() {
		return entry;
	}

	public void setEntry(JarEntry current) {
		entry = current;
		entry = current;
	}

	private String name;

	public String getName() {
		if (entry != null) {
			String fullName = entry.getName();
			if (fullName.endsWith(JAR_SEPARATOR)) {
				fullName = fullName.substring(0, fullName.length() - 1);
			}
			return fullName.substring(fullName.lastIndexOf(JAR_SEPARATOR) + 1);
		}
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
