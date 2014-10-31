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
import java.io.FilenameFilter;
import java.util.List;
import java.util.logging.Logger;

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

}
