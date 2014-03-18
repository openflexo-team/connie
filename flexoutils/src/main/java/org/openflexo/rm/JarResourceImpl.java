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

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Logger;
import java.util.regex.Pattern;

/**
 * 
 * This Resource Impl represents a Jar File Containing other files
 * 
 * @author xtof
 *
 */

public class JarResourceImpl extends BasicResourceImpl implements Resource {


	private static final Logger logger = Logger.getLogger(JarResourceImpl.class.getPackage().getName());

	private List<Resource> contents;
	private JarFile jarfile = null;
	private String jarfilename = null;


	public JarResourceImpl(ResourceLocatorDelegate locator, String filename)
			throws MalformedURLException {
		super(locator);
		this._relativePath = filename;
		jarfile = null;
		try {
			jarfile = new JarFile(URLDecoder.decode(filename, "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			logger.severe("Unable to create JarResource whith filename: " +filename);
			e.printStackTrace();
		} catch (IOException e) {
			logger.severe("Unable to create JarResource whith filename: " +filename);
			e.printStackTrace();
		}
		if (jarfile != null){
			_url = new URL("file:"+filename);
			jarfilename = filename;
		}
		else {
			logger.severe("Unable to create JarResource whith filename: " +filename);
			return;
		}
	}


	@Override
	public Resource getContainer() {
		return null;
	}

	@Override
	public boolean isContainer() {
		return true;
	}

	@Override
	public boolean isReadOnly() {
		return true;
	}

	@Override
	public List<Resource> getContents() {

		if (contents == null) {
			contents = new ArrayList<Resource>();

			Enumeration<JarEntry> entries = jarfile.entries();
			while(entries.hasMoreElements()) {
				JarEntry entry = entries.nextElement();
				String name = entry.getName();
				InJarResourceImpl res;
				try {
					res = new InJarResourceImpl(this.getLocator(),name,new URL("jar:"+this.getURL().toString() + "!/" +entry.getName()));

					contents.add(res);
				} catch (MalformedURLException e) {
					logger.severe("Unable to create JarResource whith filename: " +name);
					e.printStackTrace();
				} catch (LocatorNotFoundException e) {
					logger.severe("Unable to find locator for : " +name);
					e.printStackTrace();
				}
			}

		}
		return contents;
	}

	@Override
	public List<? extends Resource> getContents(Pattern pattern) {
		URL url = getURL();
		try{
			Enumeration<JarEntry> entries = jarfile.entries(); //gives ALL entries in jar
			List<Resource> retval = new ArrayList<Resource>();

			while(entries.hasMoreElements()) {
				String name = entries.nextElement().getName();
				boolean accept = pattern.matcher(name).matches();
				if (accept) {
					retval.add(new InJarResourceImpl(name, new URL(url.getProtocol(), url.getHost(),"file:"+jarfilename +"!/"+name)));
				}
			}

			return retval;
		}
		catch (Exception e){
			logger.severe("Unable to look for resources in URL : " + url);
			e.printStackTrace();
		}

		return java.util.Collections.emptyList();
	}

	
	public List<? extends Resource> getContents(String startpath, Pattern pattern) {

		URL url = getURL();
		try{
			Enumeration<JarEntry> entries = jarfile.entries(); //gives ALL entries in jar
			List<Resource> retval = new ArrayList<Resource>();

			while(entries.hasMoreElements()) {
				String name = entries.nextElement().getName();
				boolean accept = pattern.matcher(name).matches();
				if (name.startsWith(startpath) && accept) { //filter according to the path
					String entry = name.substring(startpath.length());
					int checkSubdir = entry.indexOf("/");
					if (checkSubdir >= 0) {
						// if it is a subdirectory, we just return the directory name
						entry = entry.substring(0, checkSubdir);
					}
					retval.add(new InJarResourceImpl(name, new URL(url.getProtocol(), url.getHost(),"file:"+jarfilename +"!/"+name)));
				}
			}

			return retval;
		}
		catch (Exception e){
			logger.severe("Unable to look for resources in URL : " + url);
			e.printStackTrace();
		}

		return java.util.Collections.emptyList();

	}



	@Override
	public void setURI(String anURI) {
		return;
	}

	@Override
	public String getRelativePath() {
		return jarfilename;
	}



}
