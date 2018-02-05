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

package org.openflexo.toolbox;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collection;
import java.util.List;

import org.apache.commons.io.FileUtils;

/**
 * 
 * This class Loader looks for classes and resources in a set of jars found in a set of directories Any Jar in the directory List ist
 * 
 * Directories can only be added to the list and not removed.
 * 
 * @author sylvain,xtof
 *
 */

public class JarInDirClassLoader extends URLClassLoader {
	private static final java.util.logging.Logger LOGGER = org.openflexo.logging.FlexoLogger
			.getLogger(JarInDirClassLoader.class.getPackage().getName());

	private final List<File> jarDirectories;

	public JarInDirClassLoader(List<File> jarDirectories) {
		super(new URL[] {}, JarInDirClassLoader.class.getClassLoader());
		this.jarDirectories = jarDirectories;
		for (File jarDir : jarDirectories) {
			addFilesFromDirectory(jarDir);
		}
	}

	@Override
	protected void finalize() throws Throwable {
		LOGGER.info("Finalizing Jar Class Loader");
		super.finalize();
	}

	/**
	 * adds a Directory in the list of directories to search for
	 * 
	 * @param directory
	 */

	public void addJarDirectory(File directory) {
		if (directory != null && directory.isDirectory()) {
			this.jarDirectories.add(directory);
			addFilesFromDirectory(directory);
		}
	}

	/**
	 * 
	 * looks for files in directory and add them to the list of files to be used to resolve class and resources
	 * 
	 * @param jarDir
	 */
	private void addFilesFromDirectory(File jarDir) {
		if (jarDir != null && jarDir.isDirectory()) {
			Collection<File> lstFile = FileUtils.listFiles(jarDir, new String[] { "jar" }, false);
			for (File f : lstFile) {
				try {
					this.addURL(f.toURI().toURL());
				} catch (MalformedURLException e) {
					LOGGER.severe("JarInDirClassLoader: cannot load file from Malformed URI: " + jarDir.toString());
				}
			}
		}
	}

}
