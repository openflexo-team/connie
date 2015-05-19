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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Logger;

import org.openflexo.logging.FlexoLogger;

/**
 * Utilities to encode meta-data on file system
 * 
 * @author sylvain
 */
public class FileMetaDataUtils {

	private static final Logger LOGGER = FlexoLogger.getLogger(FileMetaDataUtils.class.getPackage().getName());

	public static String getProperty(String propertyName, File aFile) {
		return getProperties(aFile).getProperty(propertyName);
	}

	public static void setProperty(String propertyName, String value, File aFile) {
		Properties properties = getProperties(aFile);
		properties.setProperty(propertyName, value);
		try {
			properties.store(new FileOutputStream(getMetaDataFile(aFile)), "MetaData for file " + aFile);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static Properties getProperties(File file) {
		File metaDataFile = getMetaDataFile(file);
		Properties properties = new Properties();
		if (metaDataFile.exists()) {
			try {
				properties.load(new FileReader(metaDataFile));
			} catch (FileNotFoundException e) {
				properties = null;
			} catch (IOException e) {
				properties = null;
			}
		}
		return properties;
	}

	private static File getMetaDataFile(File file) {
		if (file.isDirectory()) {
			return new File(file, ".metadata");
		} else {
			return new File(file.getParentFile(), "." + file.getName() + ".metadata");
		}
	}

	public static void main(String[] args) {
		File f = new File("/Users/sylvain/Temp/TestMetaData");
		System.out.println("coucou=" + getProperty("coucou", f));
		setProperty("coucou", "c'est moi", f);
		System.out.println("coucou=" + getProperty("coucou", f));
		setProperty("coucou2", "c'est encore moi", f);
		System.out.println("coucou2=" + getProperty("coucou2", f));
		File f2 = new File(f, "salut");
		setProperty("hop", "zobi", f2);
		System.out.println("hop=" + getProperty("hop", f2));
		System.out.println("f2.lastModified()=" + f2.lastModified());
		System.out.println("getMetaDataFile(f2).lastModified()=" + getMetaDataFile(f2).lastModified());
	}
}
