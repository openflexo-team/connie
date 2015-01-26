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
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.jar.JarFile;

/**
 * This class provides tools to retrieve files from class path
 * @author Vincent
 *
 */
public class ClassPathUtils {
	
	/**
	 * Get the files in the class path
	 * @return
	 */
	private static List<File> getClassPathFiles(){
		List<File> files = new ArrayList<File>();
		StringTokenizer string = new StringTokenizer(System.getProperty("java.class.path"), 
				Character.toString(File.pathSeparatorChar));
		while(string.hasMoreTokens())
		{
			files.add(new File(string.nextToken()));
		}
    	return files;
	}
	
	/**
	 * Get the files in the class path which are jars
	 * @return
	 */
	public static List<JarFile> getClassPathJarFiles() {
		List<JarFile> jarFiles = new ArrayList<JarFile>();
		for(File jar : getClassPathFiles()){
			if(isJarFile(jar)){
				try {
					jarFiles.add(new JarFile(jar));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return jarFiles;
	}
	
	private static boolean isJarFile(File jar){
		return jar.getName().endsWith(".jar");
	}
	
	public static URL[] findJarsFromDirectory(File targetDirectory){
		// Retrieve downloaded files which are jars
	    File[] files = targetDirectory.listFiles();
	    URL[] urls = new URL[files.length];
	    for (int i = 0; i < files.length; i++) {
	        if (files[i].getName().endsWith(".jar")) {
	        	try {
	        		//loader.addURL(files[i].toURI().toURL());
	        		urls[i] = files[i].toURI().toURL();
				} catch (MalformedURLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	        }
	    }
	    return urls;
	}

	public static void downloadJars(List<URL> remoteUrls, String path){
		// Download the files
		for(URL url : remoteUrls){
			try {
				HTTPFileUtils.getFile(url.toURI().toString(), path);
			} catch (URISyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
}
