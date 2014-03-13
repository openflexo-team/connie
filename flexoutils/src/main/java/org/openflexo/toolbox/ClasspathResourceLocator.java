/*
 * (c) Copyright 2010-2011 AgileBirds
 * (c) Copyright 2013-2014 Openflexo
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
package org.openflexo.toolbox;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Logger;
import java.util.regex.Pattern;

/**
 * @author C. Guychard
 * 
 *        ResourceLocator that looks for resources in the ClassPath
 */


public class ClasspathResourceLocator implements ResourceLocatorDelegate {

	private static final Logger logger = Logger.getLogger(ClasspathResourceLocator.class.getPackage().getName());
	private static final ClassLoader cl = ClassLoader.getSystemClassLoader();
	private static final String PATH_SEP = "/";


	@Override
	public  ResourceLocation locateResource(String relativePathName) {

		ResourceLocation resourceLocation = null;

		try {
			URL url = cl.getResource(relativePathName);
			if (url != null) {
				if (url.getProtocol().equals("file")){
					resourceLocation = new FileResourceLocation(this,relativePathName,url);

				}
				else {
					resourceLocation = new ResourceLocation(this,relativePathName,url);
				}
			}
		}
		catch (Exception e)  {
			logger.severe("Did Not find Resource in classpath " + relativePathName + " got: " + resourceLocation);
		}
		return resourceLocation;

	}


	@Override
	public ResourceLocation locateResourceWithBaseLocation(
			ResourceLocation baseLocation, String relativePath) {

		return locateResource(baseLocation.getRelativePath() + PATH_SEP + relativePath);

	}


	


	@Override
	public List<ResourceLocation> listResources(ResourceLocation dir,
			Pattern pattern) {

		URL url = dir.getURL();
		if (url != null ){
			String protocol = url.getProtocol();

			if (protocol != null && protocol.equals("jar")) {
				try{
					String jarPath = url.getPath().substring(5, url.getPath().indexOf("!")); //strip out only the JAR file
					JarFile jar = new JarFile(URLDecoder.decode(jarPath, "UTF-8"));
					Enumeration<JarEntry> entries = jar.entries(); //gives ALL entries in jar
					List<ResourceLocation> retval = new ArrayList<ResourceLocation>();

					while(entries.hasMoreElements()) {
						String name = entries.nextElement().getName();
						String startpath = dir.getRelativePath();
						boolean accept = pattern.matcher(name).matches();
						if (name.startsWith(dir.getRelativePath()) && accept) { //filter according to the path
							String entry = name.substring(startpath.length());
							int checkSubdir = entry.indexOf("/");
							if (checkSubdir >= 0) {
								// if it is a subdirectory, we just return the directory name
								entry = entry.substring(0, checkSubdir);
							}
							retval.add(new ResourceLocation(this,name, new URL(url.getProtocol(), url.getHost(),"file:"+jarPath +"!/"+name)));
						}
					}

					return retval;
				}
				catch (Exception e){
					logger.severe("Unable to look for resources in URL : " + url);
					e.printStackTrace();
				}

			}
			else if (protocol != null && protocol.equals("file")) {
				if (dir instanceof FileResourceLocation){
					File file = ((FileResourceLocation) dir).getFile();
					if (file == null){
						try {
							file = new File(url.toURI());
							if (file != null) ((FileResourceLocation) dir).setFile(file);
						} catch (URISyntaxException e) {
							logger.severe("Unable to convert URL to File : " + url);
							e.printStackTrace();
						}
					}
					if (file != null && file.isDirectory()){

						List<ResourceLocation> retval = new ArrayList<ResourceLocation>();

						FileSystemResourceLocator.addDirectoryContent(this,file,pattern,retval);

						return retval;
					}
				}
				else {
					// TODO , but it should not happen
					logger.warning("Found a File that is not hold by a FileResourceLocation");
				}
			}

		}
		return java.util.Collections.emptyList();

	}

	@Override
	public List<ResourceLocation> listAllResources(ResourceLocation dir) {
		return listResources(dir,Pattern.compile(".*"+dir.getRelativePath() + "/.*"));
	}


	public InputStream retrieveResourceAsInputStream(ResourceLocation rl) {
		if (rl != null){
			try {
				return rl.getURL().openStream();
			} catch (Exception e) {
				logger.warning("Did Not find Resource with URL " + rl.toString() );
				e.printStackTrace();
			}
		}
		return null;

	}

	public File retrieveResourceAsFile(ResourceLocation rl) {

		File locateFile = null;

		if (rl != null){
			URL url = rl.getURL();
			try {
				if (url.getProtocol().equalsIgnoreCase("file")){
					locateFile = new File(url.toURI());
				}
				else {
					logger.warning("Resource found is not convertible to a File " + url.toString() );
					locateFile = null;
				}
			} catch (URISyntaxException e) {
				locateFile = null;
			}catch (Exception e) {
				locateFile = null;		
				logger.warning("Did Not find Resource in classpath : " + url );
				e.printStackTrace();
			}
		}
		return locateFile;
	}


	public String toString(){
		return this.getClass().getSimpleName();
	}


}
