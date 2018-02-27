/*
 * File: JarClassLoader.java
 *
 * Copyright (C) 2008-2013 JDotSoft. All Rights Reserved.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2 as
 * published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301 USA
 *
 * Visit jdotsoft.com for commercial license.
 *
 * $Id: JarClassLoader.java,v 1.39 2016/04/24 17:25:30 mg Exp $
 */
package org.openflexo.toolbox;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.util.Collections;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * An implementation of a ClassLoader which is able to handle jar in jar
 * 
 * @author sylvain
 *
 */
public class JarClassLoader extends URLClassLoader {
	private static boolean isJar(String fileName) {
		return fileName != null && fileName.toLowerCase().endsWith(".jar");
	}

	private static File jarEntryAsFile(JarFile jarFile, JarEntry jarEntry) throws IOException {
		String name = jarEntry.getName().replace('/', '_');
		int i = name.lastIndexOf(".");
		String extension = i > -1 ? name.substring(i) : "";
		File file = File.createTempFile(name.substring(0, name.length() - extension.length()) + ".", extension);
		file.deleteOnExit();
		try (InputStream input = jarFile.getInputStream(jarEntry); OutputStream output = new FileOutputStream(file)) {
			int readCount;
			byte[] buffer = new byte[4096];
			while ((readCount = input.read(buffer)) != -1) {
				output.write(buffer, 0, readCount);
			}
			return file;
		}
	}

	public JarClassLoader(URL jarURL, ClassLoader parent) {
		super((Collections.singletonList(jarURL)).toArray(new URL[1]), parent);
		try {
			ProtectionDomain protectionDomain = getClass().getProtectionDomain();
			CodeSource codeSource = protectionDomain.getCodeSource();
			URL rootJarUrl = codeSource.getLocation();
			String rootJarName = rootJarUrl.getFile();
			if (isJar(rootJarName)) {
				addJarResource(new File(rootJarUrl.getPath()));
			}
			File f = new File(jarURL.getPath());
			addJarResource(f);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void addJarResource(File file) throws IOException {
		JarFile jarFile = new JarFile(file);
		addURL(file.toURI().toURL());
		Enumeration<JarEntry> jarEntries = jarFile.entries();
		while (jarEntries.hasMoreElements()) {
			JarEntry jarEntry = jarEntries.nextElement();
			if (!jarEntry.isDirectory() && isJar(jarEntry.getName())) {
				File f = jarEntryAsFile(jarFile, jarEntry);
				addJarResource(f);
			}
		}
	}

	@Override
	protected synchronized Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
		try {
			Class<?> clazz = findLoadedClass(name);
			if (clazz == null) {
				clazz = findClass(name);
				if (resolve)
					resolveClass(clazz);
			}
			return clazz;
		} catch (ClassNotFoundException e) {
			return super.loadClass(name, resolve);
		}
	}
}
