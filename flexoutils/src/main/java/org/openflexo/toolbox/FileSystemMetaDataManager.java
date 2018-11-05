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
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * 
 * @author sylvain
 */
public class FileSystemMetaDataManager {

	private final Map<File, MetaDataProperties> propertiesForFiles = new HashMap<>();

	public String getProperty(String key, File f) {
		return getMetaDataProperties(f).getProperty(key, f);
	}

	public String getProperty(String key, String defaultValue, File f) {
		return getMetaDataProperties(f).getProperty(key, defaultValue, f);
	}

	public void setProperty(String key, String value, File f, boolean save) {
		getMetaDataProperties(f).setProperty(key, value, f, save);
	}

	public void saveMetaDataProperties(File f) {
		getMetaDataProperties(f).save();
	}

	public long metaDataLastModified(File f) {
		return getMetaDataProperties(f).metaDataFile.lastModified();
	}

	private MetaDataProperties getMetaDataProperties(File f) {
		MetaDataProperties returned = propertiesForFiles.get(f);
		if (returned == null) {
			if (f.isDirectory()) {
				returned = new MetaDataProperties(f);
			}
			else {
				returned = getMetaDataProperties(f.getParentFile());
			}
			propertiesForFiles.put(f, returned);
		}
		return returned;
	}

	private class MetaDataProperties extends Properties {

		private final File directory;
		private final File metaDataFile;

		public MetaDataProperties(File directory) {
			this.directory = directory;
			metaDataFile = new File(directory, ".metadata");
			if (metaDataFile.exists()) {
				try (FileInputStream fis = new FileInputStream(metaDataFile)) {
					load(fis);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		/* Unused
		public File getDirectory() {
			return directory;
		}
		 */

		public String getProperty(String key, File f) {
			if (f.equals(directory)) {
				return getProperty(key);
			}
			else if (f.getParentFile().equals(directory)) {
				return getProperty(f.getName() + "." + key);
			}
			else {
				System.err.println("Error: cannot retrieve metadata from that file: " + f + " in " + directory);
				return null;
			}
		}

		public String getProperty(String key, String defaultValue, File f) {
			if (f.equals(directory)) {
				return getProperty(key, defaultValue);
			}
			else if (f.getParentFile().equals(directory)) {
				return getProperty(f.getName() + "." + key, defaultValue);
			}
			else {
				System.err.println("Error: cannot retrieve metadata from that file: " + f + " in " + directory);
				return null;
			}
		}

		public void setProperty(String key, String value, File f, boolean save) {

			if (value == null) {
				remove(key);
				// System.err.println("Error: cannot set null value for key=" + key + " file: " + f + " in " + directory);
				return;
			}

			String currentValue = getProperty(key, f);
			if (!value.equals(currentValue)) {
				if (f.equals(directory)) {
					setProperty(key, value);
				}
				else if (f.getParentFile().equals(directory)) {
					setProperty(f.getName() + "." + key, value);
				}
				else {
					System.err.println("Error: cannot set metadata for that file: " + f + " in " + directory);
				}
				if (save) {
					save();
				}
			}
		}

		private void save() {
			try (FileOutputStream fos = new FileOutputStream(metaDataFile)) {
				store(fos, "Metadata for directory " + directory);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
