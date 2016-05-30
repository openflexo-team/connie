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
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.rm.BasicResourceImpl.LocatorNotFoundException;
import org.openflexo.toolbox.FileUtils;

/**
 * @author bmangez, sguerin, xtof
 * 
 *         <B>Locates resources on the FileSystem, given a collection of directories to search in</B>
 */

public class FileSystemResourceLocatorImpl implements ResourceLocatorDelegate {

	private static final Logger LOGGER = Logger.getLogger(FileSystemResourceLocatorImpl.class.getPackage().getName());
	private static String PATH_SEP = System.getProperty("file.separator");

	protected final Map<File, FileResourceImpl> cache = new HashMap<File, FileResourceImpl>();

	@Override
	public FileResourceImpl locateResource(String relativePathName) {

		if (relativePathName == null) {
			return null;
		}

		try {
			File file = locateFile(relativePathName);
			if (file != null && file.exists()) {
				FileResourceImpl returned = cache.get(file);
				if (returned == null) {
					returned = new FileResourceImpl(this, relativePathName, file.toURI().toURL(), file);
					cache.put(file, returned);
				}
				return returned;
			}
		} catch (MalformedURLException e) {
			LOGGER.severe("Unable to find given file: " + relativePathName);
			e.printStackTrace();
		} catch (LocatorNotFoundException e) {
			LOGGER.severe(" IMPOSSIBLE!  Locator is null for: " + relativePathName);
			e.printStackTrace();
		}
		return null;

	}

	private File locateFile(String relativePathName) {

		File file = new File(relativePathName);

		if (file.exists()) {
			// A absolute file path
			return file;
		}
		else {

			file = locateFile(relativePathName, false);

			if (file == null) {
				file = locateFile(relativePathName, true);
			}
			if (file != null) {
				return file;
			}

		}
		return null;
	}

	@Override
	public Resource locateResourceWithBaseLocation(Resource baseLocation, final String relativePath) {
		if (baseLocation != null) {
			if (baseLocation instanceof FileResourceImpl) {
				File f = ((FileResourceImpl) baseLocation).getFile();
				if (f.isDirectory()) {
					File[] foundFiles = f.listFiles(new FilenameFilter() {
						@Override
						public boolean accept(File dir, String name) {
							return name.equals(relativePath);
						}
					});
					if (foundFiles.length == 1) {
						try {
							return new FileResourceImpl(this, baseLocation.getRelativePath() + PATH_SEP + relativePath,
									foundFiles[0].toURI().toURL(), foundFiles[0]);
						} catch (MalformedURLException e) {
							LOGGER.severe("Unable to convert File To ResourceLocation: " + relativePath);
							e.printStackTrace();
						} catch (LocatorNotFoundException e) {
							LOGGER.severe(" IMPOSSIBLE!  Locator is nullfor: " + relativePath);
							e.printStackTrace();
						}
					}
				}
			}
			else {
				return locateResource(baseLocation.getRelativePath() + PATH_SEP + relativePath);
			}
		}
		return null;
	}

	/*
	@Override
	public List<Resource> listResources(Resource dir,Pattern pattern) {	
		URL url = null;
		if (dir != null & dir instanceof BasicResourceImpl){
			url = ((BasicResourceImpl) dir).getURL();
		}
		if (url != null ){
	
			if (dir instanceof FileResourceImpl){
				File file = ((FileResourceImpl) dir).getFile();
				if (file == null){
					try {
						file = new File(url.toURI());
						if (file != null) ((FileResourceImpl) dir).setFile(file);
					} catch (URISyntaxException e) {
						logger.severe("Unable to convert URL to File : " + url);
						e.printStackTrace();
					}
				}
				if (file != null && file.isDirectory()){
	
					List<Resource> retval = new ArrayList<Resource>();
	
					FileSystemResourceLocatorImpl.addDirectoryContent(this,file,pattern,retval);
	
					return retval;
				}
			}
			else {
				// TODO , but it should not happen
				logger.warning("Found a File that is not hold by a FileResourceLocation");
			}	
		}
		return java.util.Collections.emptyList();
	
	
	}
	 */
	/*
	@Override
	public List<Resource> listAllResources(Resource dir) {
	
		if (dir instanceof FileResourceImpl && dir.isContainer()){
			File file = ((FileResourceImpl) dir).getFile();
	
			if (file != null){
	
				return (List<Resource>) dir.getContents();
			}
		}
		else {
			logger.warning("Found a File that is not hold by a FileResourceLocation");
		}	
		return java.util.Collections.emptyList();
	
	}
	 */
	/*
	
	@Override
	public InputStream retrieveResourceAsInputStream(Resource rl) {
	
		if (rl != null){
	
			URL url = rl.getURL();
			try {
				if (rl instanceof FileResourceImpl){
					File file = retrieveResourceAsFile(rl);
					try {
						return new FileInputStream(file);
					} catch (FileNotFoundException e) {
						logger.severe("Unable to open file: " + file.getAbsolutePath());
						e.printStackTrace();
					}
				}
				return url.openStream();
			}
			catch (Exception e) {
				logger.severe("Unable to retrieve InputStream for File...: " + rl);
				e.printStackTrace();
			}
		}
		return null;
	}
	
	 */

	@Override
	public File retrieveResourceAsFile(Resource rl) {

		File locateFile = null;

		if (rl != null && rl instanceof FileResourceImpl) {

			locateFile = ((FileResourceImpl) rl).getFile();
		}
		else {
			URL url = null;
			if (rl != null & rl instanceof BasicResourceImpl) {
				url = ((BasicResourceImpl) rl).getURL();
			}
			if (url != null) {
				try {
					locateFile = new File(url.toURI());
				} catch (URISyntaxException e) {
					LOGGER.severe("Unable to retrieve File...: " + url.toString());
					e.printStackTrace();
				}
				if (locateFile != null) {
					((FileResourceImpl) rl).setFile(locateFile);
					return locateFile;
				}
			}
		}
		return locateFile;

	}

	/**
	 * Locate and returns file identified by relativePathName, if it's a directory<br>
	 * If many files match supplied relativePathName, then return the one which is most narrow of current dir, relative to the distance
	 * between files as defined in {@link FileUtils}.<br>
	 * Please use locateAllFiles(String) to get the list of all files matching supplied relativePathName
	 * 
	 * @param relativePathName
	 * @return
	 */

	public File locateDirectory(String relativePathName) {
		FileResourceImpl rl = locateResource(relativePathName);

		File f = rl.getFile();
		if (f.isDirectory()) {
			return f;
		}
		else {
			return null;
		}
	}

	/**
	 * Locate and returns file identified by relativePathName<br>
	 * If many files match supplied relativePathName, then return the one which is most narrow of current dir, relative to the distance
	 * between files as defined in {@link FileUtils}.<br>
	 * Please use locateAllFiles(String) to get the list of all files matching supplied relativePathName
	 * 
	 * @param relativePathName
	 * @return
	 */

	// TODO: to be re-factored

	private File locateFile(String relativePathName, boolean lenient) {
		final File workingDirectory = new File(System.getProperty("user.dir"));
		List<File> found = locateAllFiles(relativePathName, lenient);

		if (found.size() == 1) {
			// System.out.println("Returning " + found.get(0));
			return found.get(0);
		}

		// In this case, the response is ambigous
		if (found.size() > 1) {
			// We try to privilegiate files that are closer to working dir
			Collections.sort(found, new Comparator<File>() {
				@Override
				public int compare(File o1, File o2) {
					return FileUtils.distance(workingDirectory, o1) - FileUtils.distance(workingDirectory, o2);
				}

			});
			/*System.out.println("Ambigous files: ");
			for (File f : found) {
				System.out.println("> Found: distance=" + FileUtils.distance(workingDirectory, f) + " " + f);
			}*/
			return found.get(0);
		}

		if (LOGGER.isLoggable(Level.WARNING)) {
			LOGGER.warning("Could not locate resource " + relativePathName);
			// Thread.dumpStack();
		}
		return null;
		// TODO: this should not happen!
		/* 
		return new File(userDirectory, relativePathName);
		 */
	}

	/**
	 * Locate and returns the list of all files matching supplied relativePathName
	 * 
	 * @param relativePathName
	 * @return
	 */
	public List<File> locateAllFiles(String relativePathName) {
		return locateAllFiles(relativePathName, true);
	}

	protected List<File> locateAllFiles(String relativePathName, boolean lenient) {
		List<File> found = new ArrayList<File>();
		for (File f : getDirectoriesSearchOrder()) {
			File nextTry = new File(f, relativePathName);
			if (nextTry.exists()) {
				if (LOGGER.isLoggable(Level.FINER)) {
					LOGGER.finer("Found " + nextTry.getAbsolutePath());
				}
				try {
					if (nextTry.getCanonicalFile().getName().equals(nextTry.getName()) || lenient) {
						found.add(nextTry);
					}
				} catch (IOException e1) {
				}
			}
		}

		return found;

	}

	/*
	static String retrieveRelativePath(FileResource fileResource) {
		for (File f : getDirectoriesSearchOrder()) {
			if (fileResource.getAbsolutePath().startsWith(f.getAbsolutePath())) {
				return fileResource.getAbsolutePath().substring(f.getAbsolutePath().length() + 1).replace('\\', '/');
			}
		}
		if (fileResource.getAbsolutePath().startsWith(userDirectory.getAbsolutePath())) {
			return fileResource.getAbsolutePath().substring(userDirectory.getAbsolutePath().length() + 1).replace('\\', '/');
		}
		if (logger.isLoggable(Level.SEVERE)) {
			logger.severe("File resource cannot be found: " + fileResource.getAbsolutePath());
		}
		return null;
	}
	 */
	// TODO Should not be used any more
	/*
	public String cleanPath(String relativePathName) {
		try {
			return retrieveResourceAsFile(locateResource(relativePathName)).getCanonicalPath();
		} catch (IOException e) {
			return retrieveResourceAsFile(locateResource(relativePathName)).getAbsolutePath();
		}
		// return cleanAbsolutePath(dirtyPath);
	}
	 */
	protected List<File> directoriesSearchOrder = null;

	private static File preferredResourcePath;

	private static File userDirectory = null;

	private static File userHomeDirectory = null;

	public static File getPreferredResourcePath() {
		return preferredResourcePath;
	}

	public void resetFlexoResourceLocation(File newLocation) {
		preferredResourcePath = newLocation;
		directoriesSearchOrder = null;
	}

	public void printDirectoriesSearchOrder(PrintStream out) {
		out.println("Directories search order is:");
		for (File file : getDirectoriesSearchOrder()) {
			out.println(file.getAbsolutePath());
		}
	}

	public void init() {
		getDirectoriesSearchOrder();
	}

	/*public static void addProjectDirectory(File projectDirectory) {
		init();
		if (projectDirectory.exists()) {
			addProjectResourceDirs(directoriesSearchOrder, projectDirectory);
		}
	}*/

	protected List<File> getDirectoriesSearchOrder() {
		if (directoriesSearchOrder == null) {
			synchronized (FileSystemResourceLocatorImpl.class) {
				if (directoriesSearchOrder == null) {
					if (LOGGER.isLoggable(Level.INFO)) {
						LOGGER.info("Initializing directories search order");
					}
					directoriesSearchOrder = new ArrayList<File>();
					if (preferredResourcePath != null) {
						/*if (logger.isLoggable(Level.INFO)) {
							logger.info("Adding directory " + preferredResourcePath.getAbsolutePath());
						}*/
						directoriesSearchOrder.add(preferredResourcePath);
					}
				}
			}
		}
		return directoriesSearchOrder;
	}

	public static File getUserDirectory() {
		return userDirectory;
	}

	public static File getUserHomeDirectory() {
		return userHomeDirectory;
	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName();
	}

	/**
	 * Prepends a directory to the list of path to be searched
	 * 
	 * @param path
	 */
	public void prependToDirectories(String path) {
		File d = new File(path);
		if (directoriesSearchOrder == null) {
			this.getDirectoriesSearchOrder();
		}
		if (d.exists() && d.isDirectory()) {
			directoriesSearchOrder.add(0, d);
		}

	}

	/**
	 * Appends a directory to the list of path to be searched
	 * 
	 * @param path
	 */
	public void appendToDirectories(String path) {
		File d = new File(path);
		if (directoriesSearchOrder == null) {
			getDirectoriesSearchOrder();
		}
		if (d.exists() && d.isDirectory()) {
			directoriesSearchOrder.add(d);
		}

	}

	/**
	 * Append a directory to the file system resource locator, create the locator if it doesn't exist
	 * 
	 * @param path
	 */
	public static void appendDirectoryToFileSystemResourceLocator(String path) {
		FileSystemResourceLocatorImpl fsrl = (FileSystemResourceLocatorImpl) ResourceLocator
				.getInstanceForLocatorClass(FileSystemResourceLocatorImpl.class);
		if (fsrl == null) {
			fsrl = new FileSystemResourceLocatorImpl();
			ResourceLocator.appendDelegate(fsrl);
		}
		fsrl.appendToDirectories(path);
	}
}
