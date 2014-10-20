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

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
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


	private static final Logger logger = Logger.getLogger(InJarResourceImpl.class.getPackage().getName());
	
	private JarEntry entry = null;
	
	public InJarResourceImpl(ResourceLocatorDelegate delegate, String initialPath,
			URL url) throws LocatorNotFoundException {
		super(delegate, initialPath, url);
	}


	public InJarResourceImpl(String initialPath, URL url) throws LocatorNotFoundException {
		super(ResourceLocator.getInstanceForLocatorClass(ClasspathResourceLocatorImpl.class), initialPath, url);
		((ClasspathResourceLocatorImpl)this.getLocator()).getJarResourcesList().put(initialPath, this);
		
		// Add the InJarResource in the locator resource list if not contained
		ClasspathResourceLocatorImpl locator = (ClasspathResourceLocatorImpl) ResourceLocator.getInstanceForLocatorClass(ClasspathResourceLocatorImpl.class);
		if(locator.getJarResourcesList().get(this)==null){
			locator.getJarResourcesList().put(initialPath, this);
		}
	}



	@Override
	public InputStream openInputStream() {
		if (entry != null && _parent != null){
			return ((JarResourceImpl) _parent).openInputStream(entry);
		}
		if (_url != null){
			try {
				return _url.openStream();
			} catch (IOException e) {
				logger.severe("Cannot open given Resource: " + _url.toString());
				e.printStackTrace();
			}
		}
		return null;
	}

	
	@Override
	public List<Resource> getContents() {
		List<Resource> resources = new ArrayList<Resource>();
		
		if (entry != null && entry.isDirectory()) {
			// Browser the resource of the container
			for(Resource resource : getContainer().getContents()){
				String parentFolderPath=resource.getRelativePath();
				//If it is a folder end with "/" then remove the "/" to find the parent path
				if(parentFolderPath.endsWith("/")){
					int lastSeparator = parentFolderPath.lastIndexOf("/");
					parentFolderPath = parentFolderPath.substring(0, lastSeparator);
				}
				// Find the last separation
				if(parentFolderPath.contains("/")){
					int lastSeparator = parentFolderPath.lastIndexOf("/");
					parentFolderPath = parentFolderPath.substring(0, lastSeparator+1);
				}
				// Check it corresponds to this in jar resource
				if(parentFolderPath.equals(getRelativePath())){
					resources.add(resource);
				}
			}
			// TODO some day ...
			
		}
			
		return resources;
		
	}

	@Override
	public boolean isContainer() {
		return entry.isDirectory();
		
	};
	
	
	@Override
	public Resource getContainer() {
		if(super.getContainer()!=null){
			return super.getContainer();
		} else{
			URL url = getURL();
			
			JarResourceImpl container = (JarResourceImpl) this._parent;
			
			if (container == null) {
				// finds the container
				String jarPath = null;
				try {
					jarPath = URLDecoder.decode(url.getPath().substring(5, url.getPath().indexOf("!")).replace("+", "%2B"),"UTF-8");
				} catch (UnsupportedEncodingException e1) {
					logger.severe("Unable to decode given PATH");
					e1.printStackTrace();
				}
				try {
					container = new JarResourceImpl(ResourceLocator.getInstanceForLocatorClass(ClasspathResourceLocatorImpl.class), jarPath);
				} catch (MalformedURLException e) {
					logger.severe("Unable to retrieve containing JarFile: " + jarPath);
					e.printStackTrace();
					return (Resource) java.util.Collections.emptyList();
				}
				this.setContainer(container);
			}
			return container;
		}

	}
	
	@Override
	public List<? extends Resource> getContents(Pattern pattern) {
		String startpath = getRelativePath();
		return ((JarResourceImpl)getContainer()).getContents(startpath, pattern);
	}


	public void setEntry(JarEntry current) {
		entry = current;		
	}

}
