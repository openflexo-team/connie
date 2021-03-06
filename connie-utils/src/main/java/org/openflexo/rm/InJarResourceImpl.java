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
import java.util.StringTokenizer;
import java.util.jar.JarEntry;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import org.openflexo.toolbox.JarUtils;

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

	private JarResourceImpl jarResource;
	private InJarResourceImpl container;
	private final List<InJarResourceImpl> contents = new ArrayList<>();

	protected InJarResourceImpl(String initialPath, URL url) throws LocatorNotFoundException {
		super(ResourceLocator.getInstanceForLocatorClass(ClasspathResourceLocatorImpl.class), initialPath, url);
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
	public List<InJarResourceImpl> getContents(boolean deep) {
		if (deep) {
			List<InJarResourceImpl> returned = new ArrayList<>();
			recursivelyAppendContents(returned);
			return returned;
		}
		return contents;
	}

	private void recursivelyAppendContents(List<InJarResourceImpl> list) {
		list.addAll(contents);
		for (InJarResourceImpl child : contents) {
			if (child.isContainer()) {
				child.recursivelyAppendContents(list);
			}
		}
	}

	@Override
	public List<InJarResourceImpl> getContents(Pattern pattern, boolean deep) {
		List<InJarResourceImpl> retval = new ArrayList<>();
		List<InJarResourceImpl> allContents = getContents(deep);
		for (InJarResourceImpl current : allContents) {
			String name = current.getRelativePath();
			boolean accept = pattern.matcher(name).matches();
			if (accept) {
				retval.add(current);
			}
		}

		return retval;
	}

	public JarEntry getEntry() {
		return entry;
	}

	public void setEntry(JarEntry current) {
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

	public JarResourceImpl getJarResource() {
		return jarResource;
	}

	public void setJarResource(JarResourceImpl jarResource) {
		this.jarResource = jarResource;
	}

	@Override
	public InJarResourceImpl locateResource(String relativePathName) {
		InJarResourceImpl current = this;
		StringTokenizer st = new StringTokenizer(relativePathName, "/\\");
		while (st.hasMoreElements()) {
			if (current == null) {
				LOGGER.warning("Could not find resource " + relativePathName + " in " + getJarResource());
				return null;
			}
			String pathElement = st.nextToken();
			if (pathElement.equals("..")) {
				current = current.getContainer();
			}
			else {
				boolean foundChild = false;
				for (InJarResourceImpl child : current.getContents(false)) {
					if (child.getName().equals(pathElement)) {
						current = child;
						foundChild = true;
						break;
					}
				}
				if (!foundChild) {
					LOGGER.warning("Could not find contained path element " + pathElement + " for jar entry " + current);
					return null;
				}
			}
		}
		return current;
	}

	/**
	 * Compute relative path to access supplied {@link Resource}, asserting this relative path is expressed relatively of this resource
	 * 
	 * @param resource
	 * @return
	 */
	@Override
	public String computeRelativePath(Resource resource) {
		if (resource instanceof InJarResourceImpl) {
			return JarUtils.makePathRelativeTo(((InJarResourceImpl) resource), this);
		}
		LOGGER.warning("Could not compute relative path from a InJarResource for a non-jar resource: " + resource);
		return resource.getURI();
	}

	/**
	 * Compute the distance between this resource and supplied resource
	 * 
	 * @param resource
	 * @return
	 */
	@Override
	public int distance(Resource resource) {
		if (resource instanceof InJarResourceImpl) {
			return JarUtils.distance(this, ((InJarResourceImpl) resource));
		}
		LOGGER.warning("Could not compute distance for that resource: " + resource);
		return 1000;
	}

	@Override
	public boolean exists() {
		return getEntry() != null;
	}

}
