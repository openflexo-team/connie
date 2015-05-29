/**
 * 
 */
/**
 * 
 * Copyright (c) 2013-2014, Openflexo
 * Copyright (c) 2011-2012, AgileBirds
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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

/**
 * 
 * @author sylvain
 */
public class FileSystemMetaDataManager {

	public String getValue(String key, File f) {

		File metaDataFile = getMetaDataFile(f);
		Properties p = new Properties();
		try {
			p.load(new FileInputStream(metaDataFile));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public void setValue(String key, String value, File f) {

	}

	private File getMetaDataFile(File f) {
		if (f.isDirectory()) {
			return new File(f, ".metadata");
		} else {
			return new File(f.getParent(), ".metadata");
		}
	}

	class MetaDataProperties extends Properties {

		private final File directory;
		private final File metaDataFile;

		public MetaDataProperties(File directory) {
			this.directory = directory;
			metaDataFile = new File(directory, ".metadata");
			if (metaDataFile.exists()) {
				try {
					load(new FileInputStream(metaDataFile));
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		public File getDirectory() {
			return directory;
		}

		public String getProperty(String key, File f) {
			if (f.equals(directory)) {
				return getProperty(key);
			} else if (f.getParent().equals(directory)) {
				return getProperty(f.getName() + "." + key);
			} else {
				System.err.println("Error: cannot retrieve metadata from that file: " + f + " in " + directory);
				return null;
			}
		}

		public String getProperty(String key, String defaultValue, File f) {
			if (f.equals(directory)) {
				return getProperty(key, defaultValue);
			} else if (f.getParent().equals(directory)) {
				return getProperty(f.getName() + "." + key, defaultValue);
			} else {
				System.err.println("Error: cannot retrieve metadata from that file: " + f + " in " + directory);
				return null;
			}
		}
	}
}
