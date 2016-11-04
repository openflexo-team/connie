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

import org.openflexo.toolbox.FileUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Pattern;

/**
 * Represents a {@link Resource} which is explicitely stored (serialized) in a {@link File}
 * 
 * @author xtof,sylvain
 * 
 */
public class FileResourceImpl extends BasicResourceImpl {

	private static final Logger LOGGER = Logger.getLogger(FileResourceImpl.class.getPackage().getName());

	// Related File
	private File _file;

	@Override
	public Resource getContainer() {

		if (_parent == null) {
			File file = getFile();
			try {
				_parent = new FileResourceImpl(this.getLocator(), file.getParentFile());
			} catch (MalformedURLException e) {
				LOGGER.severe("Unable to find parent directory for " + getURI());
				e.printStackTrace();
			} catch (LocatorNotFoundException e) {
				LOGGER.severe("Unable to find Locator for " + getURI());
				e.printStackTrace();
			}
		}
		return _parent;

	}

	@Override
	public void setContainer(Resource parent) {
		// Do nothing as you cannot change parent from a file
		return;
	}

	public FileResourceImpl(ResourceLocatorDelegate locator, String initialPath, URL url2, File file2) throws LocatorNotFoundException {
		this(locator, initialPath, url2);
		_file = file2;
	}

	public FileResourceImpl(ResourceLocatorDelegate locator, String path) throws MalformedURLException, LocatorNotFoundException {
		this(locator, new File(path));
	}

	public FileResourceImpl(ResourceLocatorDelegate locator, String relativePath, URL url) throws LocatorNotFoundException {
		super(locator, relativePath, url);
		try {
			setFile(new File(url.toURI()));
		} catch (URISyntaxException e) {
			LOGGER.severe("Unable to open file from URL: " + url.toString());
			e.printStackTrace();
		}
	}

	public FileResourceImpl(ResourceLocatorDelegate locator, File file) throws MalformedURLException, LocatorNotFoundException {
		super(locator, file.getPath(), file.toURI().toURL());
		_file = file;
	}

	public FileResourceImpl(ResourceLocatorDelegate locator) {
		super(locator);
	}

	public FileResourceImpl(String canonicalPath, URL url, File file) throws LocatorNotFoundException {
		this(ResourceLocator.getInstanceForLocatorClass(FileSystemResourceLocatorImpl.class), canonicalPath, url, file);
	}

	public FileResourceImpl(File file) throws MalformedURLException, LocatorNotFoundException {
		this(ResourceLocator.getInstanceForLocatorClass(FileSystemResourceLocatorImpl.class), file);
	}

	public File getFile() {
		if (_file == null && _url != null) {
			try {
				_file = new File(_url.toURI());
			} catch (URISyntaxException e) {
				LOGGER.severe("Unable to convert URL to File : " + getURI());
				e.printStackTrace();
			}
		}
		return _file;
	}

	public void setFile(File f) {
		_file = f;
		try {
			this._url = f.toURI().toURL();
		} catch (MalformedURLException e) {
			LOGGER.severe("Unable to assign a new file: " + f.getAbsolutePath());
			e.printStackTrace();
		}
	}

	@Override
	public boolean isReadOnly() {
		return getFile().canWrite();
	}

	@Override
	public Date getLastUpdate() {
		if (_file != null) {
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
		List<Resource> retval = new ArrayList<>();

		addDirectoryContent(this.getLocator(), getFile(), retval);

		return retval;
	}

	@Override
	public List<Resource> getContents(Pattern pattern) {

		File file = getFile();
		if (file == null) {
			try {
				file = new File(getURL().toURI());
				if (file != null)
					setFile(file);
			} catch (URISyntaxException e) {
				LOGGER.severe("Unable to convert URL to File : " + getURL());
				e.printStackTrace();
			}
		}
		if (file != null && file.isDirectory()) {

			List<Resource> retval = new ArrayList<>();

			addDirectoryContent(getLocator(), file, pattern, retval);

			return retval;
		}
		return java.util.Collections.emptyList();

	}

	// Additional methods not in Resource API

	public static void addDirectoryContent(ResourceLocatorDelegate dl, File file, List<Resource> list) {

		File[] fileList = file.listFiles();
		for (final File f : fileList) {
			if (!f.isDirectory()) {
				try {
					final String fileName = f.getCanonicalPath();
					list.add(new FileResourceImpl(dl, fileName, f.toURI().toURL()));

				} catch (final Exception e) {
					try {
						LOGGER.severe("Unable to look for resources inside ResourceLocation: " + file.getCanonicalPath());
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}
			// Recursive search
			else {
				addDirectoryContent(dl, f, list);
			}
		}

	}

	@Override
	public OutputStream openOutputStream() {
		if (_file != null) {
			if (_file.exists() && _file.canWrite()) {
				try {
					return new FileOutputStream(_file);
				} catch (FileNotFoundException e) {
					LOGGER.severe("File Not Found : " + getURI());
					e.printStackTrace();
				}
			}
			else {
				try {
					_file.createNewFile();
					return new FileOutputStream(_file);
				} catch (IOException e) {
					LOGGER.severe("FUnable to create new file : " + getURI());
					e.printStackTrace();
				}

			}
		}
		return null;
	}

	/***********************************************************************************************************************************************/
	/** Those methods do not belong to the ResourceLocatorDelegate interface */

	public static void addDirectoryContent(ResourceLocatorDelegate dl, File file, Pattern pattern, List<Resource> list) {

		File[] fileList = file.listFiles();
		for (final File f : fileList) {
			if (!f.isDirectory()) {
				try {
					final String fileName = f.getCanonicalPath();
					final boolean accept = pattern.matcher(fileName).matches();
					if (accept) {
						list.add(new FileResourceImpl(dl, fileName, f.toURI().toURL()));
					}
				} catch (final Exception e) {
					try {
						LOGGER.severe("Unable to look for resources inside ResourceLocation: " + file.getCanonicalPath());
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}
			// Recursive search
			else {
				addDirectoryContent(dl, f, pattern, list);
			}
		}

	}

	/**
	 * hashCode computation based on _file field
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((_file == null) ? 0 : _file.hashCode());
		return result;
	}

	/**
	 * equals() computation based on _file field<br>
	 * 2 FileResourceImpl are equals if and only if represented files are the same
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FileResourceImpl other = (FileResourceImpl) obj;
		if (_file == null) {
			if (other._file != null)
				return false;
		}
		else if (!_file.equals(other._file))
			return false;
		return true;
	}

	/**
	 * Compute relative path to access supplied {@link Resource}, asserting this relative path is expressed relatively of this resource
	 * 
	 * @param resource
	 * @return
	 */
	@Override
	public String computeRelativePath(Resource resource) {
		ResourceLocatorDelegate locator = getLocator();
		if (locator != null) {
			Resource relocatedResource = locator.locateResource(resource.getRelativePath());
			if (relocatedResource != null) {
				resource = relocatedResource;
			}
		}

		if (resource instanceof FileResourceImpl) {
			FileResourceImpl fileResource = (FileResourceImpl) resource;
			try {
				return FileUtils.makeFilePathRelativeToDir(fileResource.getFile(), getFile());
			} catch (IOException e) {
				e.printStackTrace();
				LOGGER.warning("Could not compute relative path from " + this + " for " + resource);
				return fileResource.getFile().getAbsolutePath();
			}
		}
		LOGGER.warning("Could not compute relative path from a File for a non-file resource: " + resource);
		return resource.getURI();
	}

	/**
	 * Retrieve resource using supplied relative path name, asserting this relative path name represent a relative path from this resource
	 * 
	 * @param relativePathName
	 * @return
	 */
	@Override
	public FileResourceImpl locateResource(String relativePathName) {
		File locatedFile = new File(getFile(), relativePathName);
		if (!locatedFile.exists()) {
			return null;
		}
		if (getLocator() instanceof FileSystemResourceLocatorImpl) {
			return ((FileSystemResourceLocatorImpl) getLocator()).retrieveResource(locatedFile);
		}
		else {
			try {
				return new FileResourceImpl(getLocator(), locatedFile);
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (LocatorNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		LOGGER.warning("Could not locate a resource relatively to a resource (resource=" + this + " locator=" + getLocator() + ")");
		return null;
	}

}
