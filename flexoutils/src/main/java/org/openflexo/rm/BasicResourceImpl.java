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
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Pattern;

public class BasicResourceImpl implements Resource {

	private static final Logger LOGGER = Logger.getLogger(BasicResourceImpl.class.getPackage().getName());
	protected static final Date _dateZero = new Date(0);

	// Initial requested path
	protected String _relativePath;
	// Location of the resource for the given Delegate
	protected URL _url; 
	// The Delegate that can resolve this location
	private ResourceLocatorDelegate _locator;
	// Parent Resource
	protected Resource _parent;

	public BasicResourceImpl(
			ResourceLocatorDelegate locator, String initialPath, URL url) throws LocatorNotFoundException {
		if (locator == null){
			LOGGER.severe("Cannot create a Resource without a Locator : " + url.toString());
			throw new LocatorNotFoundException();
		}
		_relativePath = initialPath;
		_url = url;
		_locator = locator;

	}

	public BasicResourceImpl(ResourceLocatorDelegate locator) {
		_locator = locator;
	}

	@Override
	public String getURI() {
		return _url.toString();
	}

	@Override
	public void setURI(String anURI) {
		URI uri = null;
		try {
			uri = new URI(anURI);
		} catch (URISyntaxException e) {
			LOGGER.severe("Unable to translate given string to url: " + anURI);
			e.printStackTrace();
		}
		if (uri != null){
			try {
				_url = uri.toURL();
			} catch (MalformedURLException e) {
				LOGGER.severe("Unable to translate given string to url: " + anURI);
				e.printStackTrace();
			}
		}

	}

	@Override
	public ResourceLocatorDelegate getLocator() {
		return _locator;
	}

	@Override
	public String getRelativePath() {
		return this._relativePath;
	}

	@Override
	public Resource getContainer() {
		return this._parent;
	}

	@Override
	public void setContainer(Resource parent) {
		_parent = parent;
	}

	@Override
	public boolean isContainer() {
		return false;
	}


	@Override
	public InputStream openInputStream() {
		if (_url != null){
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
	public OutputStream openOutputStream() {
		return null;
	}

	/** 
	 * By default, a Resource cannot be considered Editable
	 */
	@Override
	public boolean isReadOnly() {
		return false;
	}

	@Override
	public Date getLastUpdate() {
		return _dateZero;
	}

	@Override
	public String toString(){
		ResourceLocator.getInstanceForLocatorClass(FileSystemResourceLocatorImpl.class);
		return "[" + _locator.toString() + "]" +_url.toString();

	}

	@Override
	public List<? extends Resource> getContents() {
		return java.util.Collections.emptyList();
	}

	@Override
	public List<? extends Resource> getContents(Pattern pattern) {
		return java.util.Collections.emptyList();
	}
	
	// Additional methods not from Resource interface
	
	public URL getURL(){
		return this._url;
	}
	/***
	 * Locator Not Found Exception
	 */

	public class LocatorNotFoundException extends Exception {

		/**
		 * 
		 */
		private static final long serialVersionUID = -3102784112143915911L;
		
	}
	

}
