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
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.rm.BasicResourceImpl.LocatorNotFoundException;
import org.openflexo.toolbox.FileUtils;

/**
 * This {@link ResourceLocatorDelegate} allows to retrieve {@link Resource} from source code repositories
 * 
 * @author sylvain
 */

public class SourceCodeResourceLocatorImpl extends FileSystemResourceLocatorImpl {

	private static final Logger LOGGER = Logger.getLogger(FileSystemResourceLocatorImpl.class.getPackage().getName());

	@Override
	protected List<File> getDirectoriesSearchOrder() {
		if (directoriesSearchOrder == null) {
			super.getDirectoriesSearchOrder();
			File gitRoot = getGitRoot();
			if (gitRoot != null && gitRoot.exists()) {
				// System.out.println("Found gitRoot=" + gitRoot);
				// We gets one level further to handle multiple repositories
				appendAllResourcesDirectories(gitRoot.getParentFile(), directoriesSearchOrder);
			}
			File workingDirectory = new File(System.getProperty("user.dir"));
			// System.out.println("********** userDirectory = " + workingDirectory);
			directoriesSearchOrder.add(workingDirectory);
			File current = workingDirectory;
			while (current != null) {
				// System.out.println("Current: " + current);
				File GIT_DIR = new File(current, ".git");
				if (GIT_DIR.exists()) {
					// System.out.println("Found .git");
				}
				current = current.getParentFile();
			}
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
		return directoriesSearchOrder;
	}

	/**
	 * Find directory where .git directory is defined.<br>
	 * Start search from working directory
	 * 
	 * @return
	 */
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

	/**
	 * Find all directories matching src/main/resources or src/test/resources or src/dev/resources pattern, from a diven root directory
	 * 
	 * @param root
	 * @return
	 */
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
			}
			else if (searchedToken.equals("main") || searchedToken.equals("test") || searchedToken.equals("dev")) {
				appendAllResourcesDirectories(f, "resources", returned);
			}
			else if (searchedToken.equals("resources")) {
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

	/**
	 * Locates the resource given a relative PATH and a filter to avoid ambiguity
	 * 
	 * @param relativePathName
	 * @param regexFilter
	 * @return
	 */
	// TODO: ask if this must not be promoted at ResourceLocator level
	public Resource locateResource(String relativePathName, String regexFilter) {

		if (relativePathName == null) {
			return null;
		}

		try {
			File file = locateFile(relativePathName, regexFilter);
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

	/**
	 * Locate and returns file identified by relativePathName<br>
	 * If many files match supplied relativePathName, then return the one : -- which is most narrow of current dir, relative to the distance
	 * between files as defined in {@link FileUtils}.<br>
	 * -- which matches the Pattern given as a parameter in case distance is equal
	 * 
	 * @param relativePathName
	 * @return
	 */

	// TODO: to be re-factored

	private File locateFile(String relativePathName, String regexFilter) {

		final File workingDirectory = new File(System.getProperty("user.dir"));

		List<File> found = locateAllFiles(relativePathName, true);
		List<File> matches = new ArrayList<>();
		// Apply Filter
		if (regexFilter != null) {
			for (File f : found) {
				if (f.getAbsolutePath().matches(regexFilter)) {
					matches.add(f);
				}

			}
		}

		if (matches.size() == 1) {
			// System.out.println("Returning " + found.get(0));
			return matches.get(0);
		}

		// In this case, the response is ambiguous
		if (matches.size() > 1) {
			// We try to privilegiate files that are closer to working dir
			Collections.sort(matches, new Comparator<File>() {
				@Override
				public int compare(File o1, File o2) {
					return FileUtils.distance(workingDirectory, o1) - FileUtils.distance(workingDirectory, o2);
				}

			});

			return matches.get(0);
		}

		if (LOGGER.isLoggable(Level.WARNING)) {
			LOGGER.warning("Could not locate resource " + relativePathName);
		}
		return null;
		// TODO: this should not happen!
		/* 
		return new File(userDirectory, relativePathName);
		 */
	}

}
