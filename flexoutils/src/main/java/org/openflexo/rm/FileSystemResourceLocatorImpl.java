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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import org.openflexo.toolbox.FileUtils;

/**
 * @author bmangez
 * 
 *         <B>Class Description</B>
 */
// TODO : have a more consistent API
public class FileSystemResourceLocatorImpl implements ResourceLocator {

	private static final Logger logger = Logger.getLogger(FileSystemResourceLocatorImpl.class.getPackage().getName());
	private static String PATH_SEP = System.getProperty("file.separator");



	@Override
	public  Resource locateResource(String relativePathName) {

		File file = locateFile(relativePathName,false);
		if (file == null){
			file = locateFile(relativePathName,true);
		}
		try {
			if (file != null){
				return new FileResourceImpl(this, relativePathName, file.toURI().toURL(),file);
			}
		} catch (MalformedURLException e) {
			logger.severe("Unable to find given file: " + relativePathName);
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public Resource locateResourceWithBaseLocation(	Resource baseLocation, final String relativePath) {
		if (baseLocation != null) {
			if (baseLocation instanceof FileResourceImpl){
				File f = ((FileResourceImpl) baseLocation).getFile();
				if (f.isDirectory()) {
					File [] foundFiles = f.listFiles(new FilenameFilter() {
						@Override
						public boolean accept(File dir, String name) {
							return name.equals(relativePath);
						}
					});
					if (foundFiles.length == 1) {
						try {
							return new FileResourceImpl( this, baseLocation.getRelativePath() + PATH_SEP + relativePath, foundFiles[0].toURI().toURL(),foundFiles[0]);
						} catch (MalformedURLException e) {
							logger.severe("Unable to convert File To ResourceLocation: " + relativePath);
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

		if (rl != null && rl instanceof FileResourceImpl){

			locateFile =  ((FileResourceImpl) rl).getFile();
		}
		else {
			URL url = null;
			if (rl != null & rl instanceof BasicResourceImpl){
				url = ((BasicResourceImpl) rl).getURL();
			}
			if (url != null){
				try {
					locateFile = new File(url.toURI());
				} catch (URISyntaxException e) {
					logger.severe("Unable to retrieve File...: " + url.toString());
					e.printStackTrace();
				}
				if (locateFile != null){
					((FileResourceImpl) rl).setFile(locateFile);
					return locateFile;
				}
			} 
		}
		return locateFile;

	}

	/***********************************************************************************************************************************************/
	/** Those methods do not belong to the ResourceLocatorDelegate interface */


	public static void addDirectoryContent (ResourceLocator dl, File file, Pattern pattern,List<Resource> list) {

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
		FileResourceImpl rl = (FileResourceImpl) locateResource(relativePathName);

		File f = rl.getFile();
		if (f.isDirectory()){
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

	private  File locateFile(String relativePathName, boolean lenient) {
		final File workingDirectory = new File(System.getProperty("user.dir"));
		// System.out.println("Searching " + relativePathName + " in " + workingDirectory);
		List<File> found = new ArrayList<File>();
		for (File f : getDirectoriesSearchOrder()) {
			File nextTry = new File(f, relativePathName);
			if (nextTry.exists()) {
				if (logger.isLoggable(Level.FINER)) {
					logger.finer("Found " + nextTry.getAbsolutePath());
				}
				try {
					if (nextTry.getCanonicalFile().getName().equals(nextTry.getName()) || lenient) {
						found.add(nextTry);
					}
				} catch (IOException e1) {
				}
			} else {
				if (logger.isLoggable(Level.FINER)) {
					logger.finer("Searched for a " + nextTry.getAbsolutePath());
				}
			}
		}
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

		if (logger.isLoggable(Level.WARNING)) {
			logger.warning("Could not locate resource " + relativePathName);
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

	private List<File> locateAllFiles(String relativePathName, boolean lenient) {
		final File workingDirectory = new File(System.getProperty("user.dir"));
		// System.out.println("Searching " + relativePathName + " in " + workingDirectory);
		List<File> found = new ArrayList<File>();
		for (File f : getDirectoriesSearchOrder()) {
			File nextTry = new File(f, relativePathName);
			if (nextTry.exists()) {
				if (logger.isLoggable(Level.FINER)) {
					logger.finer("Found " + nextTry.getAbsolutePath());
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
	private List<File> directoriesSearchOrder = null;

	private static File preferredResourcePath;

	private static File userDirectory = null;

	private static File userHomeDirectory = null;

	public static File getPreferredResourcePath() {
		return preferredResourcePath;
	}

	public  void resetFlexoResourceLocation(File newLocation) {
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

	/**
	 * Find directory where .git directory is defined.<br>
	 * Start search from working directory
	 * 
	 * @return
	 */
	// TODO : To Remove when ResourceLocator is fixed 
	/*
	private static File getGitRoot() {
		File workingDirectory = new File(System.getProperty("user.dir"));
		// System.out.println("********** workingDirectory = " + workingDirectory);
		File current = workingDirectory;
		while (current != null) {
			// System.out.println("Current: " + current);
			File GIT_DIR = new File(current, ".git");
			if (GIT_DIR.exists()) {
				// System.out.println("Found .git");
				return current;
			}
			current = current.getParentFile();
		}
		return null;
	}
	 */
	/**
	 * Find all directories matching src/main/resources or src/test/resources or src/dev/resources pattern, from a diven root directory
	 * 
	 * @param root
	 * @return
	 */
	/*
	private static void appendAllResourcesDirectories(File root, List<File> returned) {
		appendAllResourcesDirectories(root, "src", returned);
	}

	private static void appendAllResourcesDirectories(File root, final String searchedToken, List<File> returned) {
		for (File f : root.listFiles(new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {
				return name.equals(searchedToken);
			}
		})) {
			if (searchedToken.equals("src")) {
				appendAllResourcesDirectories(f, "main", returned);
				appendAllResourcesDirectories(f, "test", returned);
				appendAllResourcesDirectories(f, "dev", returned);
			} else if (searchedToken.equals("main") || searchedToken.equals("test") || searchedToken.equals("dev")) {
				appendAllResourcesDirectories(f, "resources", returned);
			} else if (searchedToken.equals("resources")) {
				// System.out.println("Found " + f);
				returned.add(f);
			}
		}
		for (File d : root.listFiles()) {
			if (d.isDirectory() && (new File(d, "pom.xml").exists())) {
				appendAllResourcesDirectories(d, returned);
			}
		}
	}
	 */

	private List<File> getDirectoriesSearchOrder() {
		if (directoriesSearchOrder == null) {
			synchronized (FileSystemResourceLocatorImpl.class) {
				if (directoriesSearchOrder == null) {
					if (logger.isLoggable(Level.INFO)) {
						logger.info("Initializing directories search order");
					}
					directoriesSearchOrder = new ArrayList<File>();
					if (preferredResourcePath != null) {
						/*if (logger.isLoggable(Level.INFO)) {
							logger.info("Adding directory " + preferredResourcePath.getAbsolutePath());
						}*/
						directoriesSearchOrder.add(preferredResourcePath);
					}
					// TODO : To remove when ResourceLocator is Fixed
					/*
					File gitRoot = getGitRoot();
					if (gitRoot != null && gitRoot.exists()) {
						// System.out.println("Found gitRoot=" + gitRoot);
						// We gets one level further to handle multiple repositories
						appendAllResourcesDirectories(gitRoot.getParentFile(), directoriesSearchOrder);
					}
					File workingDirectory = new File(System.getProperty("user.dir"));
					System.out.println("********** userDirectory = " + workingDirectory);
					directoriesSearchOrder.add(workingDirectory);
					File current = workingDirectory;
					while (current != null) {
						System.out.println("Current: " + current);
						File GIT_DIR = new File(current, ".git");
						if (GIT_DIR.exists()) {
							System.out.println("Found .git");
						}
						current = current.getParentFile();
					}
					 */
					/*
					 * File flexoDesktopDirectory = findProjectDirectoryWithName(workingDirectory, "openflexo");

					if (flexoDesktopDirectory != null) {
						findAllFlexoProjects(flexoDesktopDirectory, directoriesSearchOrder);
						File technologyadaptersintegrationDirectory = new File(flexoDesktopDirectory.getParentFile(),
								"packaging/technologyadaptersintegration");
						if (technologyadaptersintegrationDirectory != null) {
							findAllFlexoProjects(technologyadaptersintegrationDirectory, directoriesSearchOrder);
						}
					}
					directoriesSearchOrder.add(workingDirectory);*/
				}
			}
		}
		return directoriesSearchOrder;
	}

	/*public static File findProjectDirectoryWithName(File currentDir, String projectName) {
		if (currentDir != null) {
			File attempt = new File(currentDir, projectName);
			if (attempt.exists()) {
				return attempt;
			} else {
				return findProjectDirectoryWithName(currentDir.getParentFile(), projectName);
			}
		}
		return null;
	}

	public static void findAllFlexoProjects(File dir, List<File> files) {
		if (new File(dir, "pom.xml").exists()) {
			files.add(dir);
			for (File f : dir.listFiles()) {
				if (f.getName().startsWith("flexo") || f.getName().contains("connector")
						|| f.getName().equals("technologyadaptersintegration") || f.getName().startsWith("diana")
						|| f.getName().startsWith("fib") || f.getName().startsWith("agilebirdsconnector") || f.getName().equals("projects")
						|| f.getName().equals("free-modelling-editor")) {
					addProjectResourceDirs(files, f);
				}
				if (f.isDirectory()) {
					findAllFlexoProjects(f, files);
				}
			}
		}
	}

	public static void addProjectResourceDirs(List<File> files, File f) {
		File file1 = new File(f.getAbsolutePath() + "/src/main/resources");
		File file2 = new File(f.getAbsolutePath() + "/src/test/resources");
		File file3 = new File(f.getAbsolutePath() + "/src/dev/resources");
		// File file4 = new File(f.getAbsolutePath());
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Adding directory " + file1.getAbsolutePath());
		}
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Adding directory " + file2.getAbsolutePath());
		}
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Adding directory " + file3.getAbsolutePath());
		}
		if (file1.exists()) {
			files.add(file1);
		}
		if (file2.exists()) {
			files.add(file2);
		}
		if (file3.exists()) {
			files.add(file3);
		}
	}*/

	public static File getUserDirectory() {
		return userDirectory;
	}

	public static File getUserHomeDirectory() {
		return userHomeDirectory;
	}


	public String toString(){
		return this.getClass().getSimpleName();
	}

	/**
	 * Prepends a directory to the list of path to be searched
	 * @param path
	 */
	public void prependToDirectories(String path) {
		File d = new File(path);
		if (directoriesSearchOrder == null) {
			this.getDirectoriesSearchOrder();
		}
		if (d.exists() && d.isDirectory()){
			directoriesSearchOrder.add(0,d);
		}

	}

	/**
	 * Appends a directory to the list of path to be searched
	 * @param path
	 */
	public void appendToDirectories(String path) {
		File d = new File(path);
		if (directoriesSearchOrder == null) {
			getDirectoriesSearchOrder();
		}
		if (d.exists() && d.isDirectory()){
			directoriesSearchOrder.add(d);
		}

	}


}
