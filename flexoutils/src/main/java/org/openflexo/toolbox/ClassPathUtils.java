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
