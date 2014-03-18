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
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Logger;
import java.util.regex.Pattern;

public class FileResourceImpl extends BasicResourceImpl {

	private static final Logger logger = Logger.getLogger(FileResourceImpl.class.getPackage().getName());

	// Related File
	private File _file; 



	@Override
	public Resource getContainer() {

		if (_parent == null ){
			File file = getFile();
			try {
				_parent = new FileResourceImpl(this.getLocator(), file.getParentFile());
			} catch (MalformedURLException e) {
				logger.severe("Unable to find parent directory for " + getURI());
				e.printStackTrace();
			} catch (LocatorNotFoundException e) {
				logger.severe("Unable to find Locator for " + getURI());
				e.printStackTrace();
			}
		}
		return null;

	}


	@Override
	public void setContainer(Resource parent) {
		// Do nothing as you cannot change parent from a file
		return;
	}

	public FileResourceImpl(ResourceLocatorDelegate locator,String initialPath,
			URL url2, File file2) throws LocatorNotFoundException {
		this (locator, initialPath, url2);
		_file = file2;
	}


	public FileResourceImpl(ResourceLocatorDelegate locator,String path) throws MalformedURLException, LocatorNotFoundException {
		this (locator, new File(path));
	}


	public FileResourceImpl(
			ResourceLocatorDelegate locator, String relativePath, URL url) throws LocatorNotFoundException {
		super(locator, relativePath, url);
		try {
			setFile(new File(url.toURI()));
		} catch (URISyntaxException e) {
			logger.severe("Unable to open file from URL: " + url.toString());
			e.printStackTrace();
		}
	}

	public FileResourceImpl(ResourceLocatorDelegate locator,
			File file) throws MalformedURLException, LocatorNotFoundException {
		super(locator,file.getPath(),file.toURI().toURL());
	}

	public FileResourceImpl(ResourceLocatorDelegate locator) {
		super(locator);
	}


	public FileResourceImpl(String canonicalPath, URL url, File file) throws LocatorNotFoundException {
		this(ResourceLocator.getInstanceForLocatorClass(FileSystemResourceLocatorImpl.class),canonicalPath, url, file);
	}


	public FileResourceImpl(File file) throws MalformedURLException, LocatorNotFoundException {
		this (ResourceLocator.getInstanceForLocatorClass(FileSystemResourceLocatorImpl.class),file);
	}


	public File getFile(){
		if (_file == null && _url != null){
			try {
				_file = new File(_url.toURI());
			} catch (URISyntaxException e) {
				logger.severe("Unable to convert URL to File : " + getURI());
				e.printStackTrace();
			}
		}
		return _file;
	}

	public void setFile(File f){
		_file = f;
		try {
			this._url = f.toURI().toURL();
		} catch (MalformedURLException e) {
			logger.severe("Unable to assign a new file: " + f.getAbsolutePath());
			e.printStackTrace();
		}
	}

	@Override
	public boolean isReadOnly(){
		return getFile().canWrite();
	}

	@Override
	public Date getLastUpdate() {
		if (_file != null){
			return new Date(_file.lastModified());
		}
		return _dateZero;
	}

	@Override
	public boolean isContainer() {
		return getFile().isDirectory();
	}

	@Override
	public List<Resource> getContents() {
		List<Resource> retval = new ArrayList<Resource>();

		addDirectoryContent(this.getLocator(),getFile(),retval);

		return retval;
	}



	@Override
	public List<Resource> getContents(Pattern pattern) {

		File file = getFile();
		if (file == null){
			try {
				file = new File(getURL().toURI());
				if (file != null) setFile(file);
			} catch (URISyntaxException e) {
				logger.severe("Unable to convert URL to File : " + getURL());
				e.printStackTrace();
			}
		}
		if (file != null && file.isDirectory()){

			List<Resource> retval = new ArrayList<Resource>();

			addDirectoryContent(getLocator(),file,pattern,retval);

			return retval;
		}
		return java.util.Collections.emptyList();

	}

	// Additional methods not in Resource API

	public static void addDirectoryContent (ResourceLocatorDelegate dl, File file, List<Resource> list) {

		File[] fileList = file.listFiles();
		for(final File f : fileList){
			if(! f.isDirectory()){
				try{
					final String fileName = f.getCanonicalPath();
					list.add(new FileResourceImpl(dl,fileName,f.toURI().toURL()));

				} catch(final Exception e){
					try {
						logger.severe("Unable to look for resources inside ResourceLocation: " + file.getCanonicalPath());
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}
			// Recursive search
			else {
				addDirectoryContent(dl,f, list);
			}
		}

	}


	/***********************************************************************************************************************************************/
	/** Those methods do not belong to the ResourceLocatorDelegate interface */


	public static void addDirectoryContent (ResourceLocatorDelegate dl, File file, Pattern pattern,List<Resource> list) {

		File[] fileList = file.listFiles();
		for(final File f : fileList){
			if(! f.isDirectory()){
				try{
					final String fileName = f.getCanonicalPath();
					final boolean accept = pattern.matcher(fileName).matches();
					if(accept){
						list.add(new FileResourceImpl(dl,fileName,f.toURI().toURL()));
					}
				} catch(final Exception e){
					try {
						logger.severe("Unable to look for resources inside ResourceLocation: " + file.getCanonicalPath());
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}
			// Recursive search
			else {
				addDirectoryContent(dl,f, pattern, list);
			}
		}

	}

	
}
