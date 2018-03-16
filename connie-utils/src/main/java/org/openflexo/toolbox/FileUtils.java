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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.Date;
import java.util.Map;
import java.util.Vector;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.openflexo.logging.FlexoLogger;
import org.openflexo.rm.FileResourceImpl;
import org.openflexo.rm.InJarResourceImpl;
import org.openflexo.rm.Resource;

/**
 * Some File utilities
 * 
 * @author sylvain
 */
public class FileUtils {

	private static final Logger LOGGER = FlexoLogger.getLogger(FileUtils.class.getPackage().getName());

	public static enum CopyStrategy {
		REPLACE, REPLACE_OLD_ONLY, IGNORE_EXISTING
	}

	// Unused private static final String WIN_REGISTRY_DOCUMENTS_KEY_PATH =
	// "HKEY_CURRENT_USER\\Software\\Microsoft\\Windows\\CurrentVersion\\Explorer\\User Shell Folders";

	// Unused private static final String WIN_REGISTRY_DOCUMENTS_ATTRIBUTE = "Personal";

	public static final String BAD_CHARACTERS_FOR_FILE_NAME_REG_EXP = "[\"|\\?\\*/<>:\\\\]|[^\\p{ASCII}]";

	// Unused private static final String GOOD_CHARACTERS_REG_EXP = "[^\"|\\?\\*/<>:\\\\]|[\\p{ASCII}]";

	public static final Pattern BAD_CHARACTERS_FOR_FILE_NAME_PATTERN = Pattern.compile(BAD_CHARACTERS_FOR_FILE_NAME_REG_EXP);

	private static final String NO_BLANK_NO_SLASH = "[^ /\\\\:\\*\\?\"<>|]";

	private static final String SLASH = "(/|\\\\)";

	private static final String NO_SLASH = "[^/\\\\:\\*\\?\"<>|]";

	private static final String UNACCEPTABLE_CHARS = "[:\\*\\?\"<>|]|[^\\p{ASCII}]";

	private static final Pattern UNACCEPTABLE_CHARS_PATTERN = Pattern.compile(UNACCEPTABLE_CHARS);

	private static final String UNACCEPTABLE_SLASH = "\\s+" + SLASH + "\\s*|\\s*" + SLASH + "\\s+|\\\\";

	private static final Pattern UNACCEPTABLE_SLASH_PATTERN = Pattern.compile(UNACCEPTABLE_SLASH);

	private static final String PATH_SEP = System.getProperty("file.separator");

	public static final String VALID_FILE_NAME_REGEXP = SLASH + "?(" + NO_BLANK_NO_SLASH + "(" + NO_SLASH + "+?" + NO_BLANK_NO_SLASH + "|"
			+ NO_BLANK_NO_SLASH + "*?)(" + SLASH + "(" + NO_BLANK_NO_SLASH + "*?|" + NO_BLANK_NO_SLASH + NO_SLASH + "+?)"
			+ NO_BLANK_NO_SLASH + ")*)+" + SLASH + "?";

	/*
	public static byte[] getBytes(File f) {
		byte[] b = new byte[(int) f.length()];
		try (FileInputStream fis = new FileInputStream(f)) {
			fis.read(b);
			return b;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	*/
	public static byte[] getBytes(File f, int nBytes) throws IOException {
		byte[] b = new byte[nBytes];
		try (FileInputStream fis = new FileInputStream(f)) {
			fis.read(b);
			return b;
		}
	}

	/*
		public static void copyDirFromDirToDir(String srcName, File srcParentDir, File destDir) throws IOException {
			copyDirFromDirToDir(srcName, srcParentDir, destDir, CopyStrategy.REPLACE);
		}
	public static void copyDirFromDirToDir(String srcName, File srcParentDir, File destDir, CopyStrategy stragtegy) throws IOException {
		copyDirToDir(new File(srcParentDir, srcName), destDir);
	}
	
	private static File copyDirToDir(File src, File dest) throws IOException {
		return copyDirToDir(src, dest, CopyStrategy.REPLACE);
	}
	*/

	public static File copyDirToDir(File src, File dest, CopyStrategy strategy) throws IOException {
		File newDir = new File(dest, src.getName());
		newDir.mkdirs();
		copyContentDirToDir(src, newDir, strategy);
		return newDir;
	}

	public static File copyResourceToDir(Resource src, File dest, CopyStrategy strategy) throws IOException {
		if (src instanceof FileResourceImpl && ((FileResourceImpl) src).getFile() != null) {
			return copyDirToDir(((FileResourceImpl) src).getFile(), dest, strategy);
		}
		else if (src instanceof InJarResourceImpl) {
			for (Resource rsc : src.getContents(Pattern.compile(".*" + src.getRelativePath() + "/.*"), false)) {
				if (!rsc.isContainer()) {
					copyInJarResourceToDir((InJarResourceImpl) rsc, dest);
				}
			}
		}
		else {
			LOGGER.severe("Unable to copy resource: " + src.toString());
			return null;
		}
		return null;

	}

	public static void copyResourceToDir(Resource locateResource, File file) throws IOException {
		copyResourceToDir(locateResource, file, CopyStrategy.REPLACE);
	}

	private static void copyInJarResourceToDir(InJarResourceImpl rsc, File dest) throws IOException {
		String rpath = rsc.getRelativePath();
		File f = new File(dest, rpath.replace("/", PATH_SEP));
		if (rpath.endsWith("/")) {
			f.mkdir();
		}
		else {
			f.getParentFile().mkdirs();
			try {
				f.createNewFile();
				if (f.exists()) {
					try (InputStream in = rsc.openInputStream(); OutputStream out = new FileOutputStream(f)) {
						IOUtils.copy(in, out);
					}
				}
				else {
					LOGGER.severe("Unable to copy InJarResource: " + rsc);
				}
			} catch (IOException e) {
				LOGGER.warning("Cannot create file " + f.getAbsolutePath());
				throw e;
			} catch (Exception e) {
				LOGGER.warning("Cannot copy file " + f.getAbsolutePath());
				throw e;
			}

		}
	}

	/*
	private static void copyDirFromDirToDirIncludingCVSFiles(String srcName, File srcParentDir, File destDir) throws IOException {
		copyDirToDirIncludingCVSFiles(new File(srcParentDir, srcName), destDir);
	}
	
	private static File copyDirToDirIncludingCVSFiles(File src, File dest) throws IOException {
		File newDir = new File(dest, src.getName());
		newDir.mkdirs();
		copyContentDirToDirIncludingCVSFiles(src, newDir);
		return newDir;
	}
	*/
	public static void copyContentDirToDir(File src, File dest) throws IOException {
		copyContentDirToDir(src, dest, CopyStrategy.REPLACE);
	}

	public static void copyContentDirToDir(File src, File dest, CopyStrategy strategy) throws IOException {
		copyContentDirToDir(src, dest, strategy, FileFilterUtils.trueFileFilter());
	}

	public static void copyContentDirToDir(File src, File dest, CopyStrategy strategy, FileFilter fileFilter) throws IOException {
		if (src == null || dest == null || !src.exists() || !src.isDirectory()) {
			return;
		}
		if (!dest.exists()) {
			dest.mkdirs();
		}
		File[] fileArray = src.listFiles();
		if (fileArray == null)
			return;
		for (int i = 0; i < fileArray.length; i++) {
			File curFile = fileArray[i];
			if (curFile.isDirectory() && !curFile.getName().equals("CVS") && fileFilter.accept(curFile)) {
				copyContentDirToDir(curFile, new File(dest, curFile.getName()), strategy, fileFilter);
			}
			else if (curFile.isFile() && fileFilter.accept(curFile)) {
				File destFile = new File(dest, curFile.getName());
				if (destFile.exists()) {
					switch (strategy) {
						case IGNORE_EXISTING:
							continue;
						case REPLACE_OLD_ONLY:
							if (!getDiskLastModifiedDate(curFile).after(getDiskLastModifiedDate(destFile))) {
								continue;
							}
						default:
							break;
					}
				}
				copyFileToFile(curFile, destFile);
			}
		}
	}

	/*
		private static void copyContentDirToDirIncludingCVSFiles(File src, File dest) throws IOException {
			if (!src.exists()) {
				return;
			}
			if (!dest.exists()) {
				dest.mkdirs();
			}
			File[] fileArray = src.listFiles();
			for (int i = 0; i < fileArray.length; i++) {
				File curFile = fileArray[i];
				if (curFile.isDirectory()) {
					copyDirFromDirToDirIncludingCVSFiles(curFile.getName(), src, dest);
				}
				else if (curFile.isFile()) {
					FileInputStream is = new FileInputStream(curFile);
					try {
						copyFileToDir(is, curFile.getName(), dest);
					} finally {
						is.close();
					}
				}
			}
		}
	*/
	public static boolean createNewFile(File newFile) throws IOException {
		boolean ret = false;
		if (!newFile.exists()) {
			if (!newFile.getParentFile().exists()) {
				ret = newFile.getParentFile().mkdirs();
				if (!ret) {
					newFile = newFile.getCanonicalFile();
					ret = newFile.getParentFile().mkdirs();
				}
				if (!ret) {
					System.err.println("WARNING: cannot create directory: " + newFile.getParent() + " createNewFile(File)["
							+ FileUtils.class.getName() + "]");
				}
			}
			try {
				ret = newFile.createNewFile();
			} catch (IOException e) {
				newFile = newFile.getCanonicalFile();
				ret = newFile.createNewFile();
				if (!ret) {
					System.err.println("WARNING: cannot create file: " + newFile.getAbsolutePath() + " createNewFile(File)["
							+ FileUtils.class.getName() + "]");
				}
			}
		}
		return ret;
	}

	public static void copyFileToFile(File curFile, File newFile) throws IOException {
		createNewFile(newFile);
		try (FileInputStream is = new FileInputStream(curFile); FileOutputStream os = new FileOutputStream(newFile)) {
			while (is.available() > 0) {
				byte[] byteArray = new byte[is.available()];
				is.read(byteArray);
				os.write(byteArray);
			}
			os.flush();
		}
	}

	public static File copyFileToDir(FileInputStream is, String newFileName, File dest) throws IOException {
		File newFile = new File(dest, newFileName);
		createNewFile(newFile);
		try (FileOutputStream os = new FileOutputStream(newFile)) {
			while (is.available() > 0) {
				byte[] byteArray = new byte[is.available()];
				is.read(byteArray);
				os.write(byteArray);
			}
			os.flush();
		}
		return newFile;
	}

	public static File copyFileToDir(File src, File dest) throws IOException {
		try (FileInputStream fis = new FileInputStream(src)) {
			return copyFileToDir(fis, src.getName(), dest);
		}
	}

	/*
	private static void saveToFile(File dest, byte[] b) throws IOException {
		createNewFile(dest);
		try (FileOutputStream fos = new FileOutputStream(dest)) {
			fos.write(b);
			fos.flush();
		}
	}
	*/

	private static final String LINE_SEPARATOR = System.getProperty("line.separator");

	/*
	private static final FilenameFilter CVSFileNameFilter = new FilenameFilter() {
		@Override
		public boolean accept(File dir, String name) {
			return !"CVS".equals(name);
		}
	};
	
	private static final FilenameFilter JARFileNameFilter = new FilenameFilter() {
		@Override
		public boolean accept(File dir, String name) {
			return name.toLowerCase().endsWith(".jar");
		}
	};
	*/

	public static final FilenameFilter PropertiesFileNameFilter = new FilenameFilter() {
		@Override
		public boolean accept(File dir, String name) {
			return name.toLowerCase().endsWith(".properties");
		}
	};

	public static void saveToFile(File dest, String fileContent) throws IOException {
		saveToFile(dest, fileContent, null);
	}

	public static void saveToFile(File dest, String fileContent, String encoding) throws IOException {
		createNewFile(dest);
		BufferedReader bufferedReader = new BufferedReader(new StringReader(fileContent));
		try (OutputStreamWriter fw = new OutputStreamWriter(new FileOutputStream(dest),
				Charset.forName(encoding != null ? encoding : "UTF-8"))) {
			String line = null;
			while ((line = bufferedReader.readLine()) != null) {
				fw.write(line);
				fw.write(LINE_SEPARATOR);
			}
			fw.flush();
		}
	}

	/*
	private static void saveToFile(String fileName, String fileContent, File dir, String fileExtention) throws IOException {
		File dest = new File(dir.getAbsolutePath() + "/" + fileName + "." + fileExtention);
		saveToFile(dest, fileContent);
	}
	
	private static void saveToFile(String fileName, String fileContent, File dir) throws IOException {
		File dest = new File(dir.getAbsolutePath() + "/" + fileName);
		saveToFile(dest, fileContent);
	}
	
	private static void saveToFile(File file, InputStream is) throws IOException {
		FileOutputStream fos = new FileOutputStream(file);
		try {
			byte[] b = new byte[8192];
			int read = 0;
			while ((read = is.read(b)) > 0) {
				fos.write(b, 0, read);
			}
		} finally {
			fos.close();
		}
	}
	*/

	public static String fileContents(File aFile) throws IOException {
		return fileContents(aFile, null);
	}

	public static String fileContents(File aFile, String encoding) throws IOException {
		try (FileInputStream fis = new FileInputStream(aFile)) {
			return fileContents(fis, encoding);
		}
	}

	public static String fileContents(InputStream inputStream, String encoding) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, encoding != null ? encoding : "UTF-8"));
		StringBuilder sb = new StringBuilder();
		String line = null;
		while ((line = br.readLine()) != null) {
			sb.append(line);
			sb.append(StringUtils.LINE_SEPARATOR);
		}
		return sb.toString();
	}

	/**
	 * @param file
	 * @return
	 */
	public static boolean recursiveDeleteFile(File file) {
		try {
			for (Path path : Files.walk(file.toPath()).sorted(Comparator.reverseOrder()).collect(Collectors.toList()))
				Files.deleteIfExists(path);
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	/*
	private static String convertBackslashesToSlash(String fileName) {
		return fileName.replaceAll("\\\\", "/");
	}
	*/

	public static int countFilesInDirectory(File directory, boolean recursive) {
		return countFilesInDirectory(directory, recursive, null);
	}

	public static int countFilesInDirectory(File directory, boolean recursive, FileFilter fileFilter) {
		if (!directory.isDirectory() || !directory.exists()) {
			return -1;
		}
		File[] files = directory.listFiles();
		int count = 0;
		for (File file : files) {
			if (fileFilter != null && !fileFilter.accept(file)) {
				continue;
			}
			if (file.isDirectory()) {
				if (recursive) {
					count += countFilesInDirectory(file, recursive);
				}
			}
			else {
				count++;
			}
		}
		return count;
	}

	/**
	 * Recursive computing of last modified date (deep check for contained files)
	 * 
	 * @param file
	 * @return
	 */
	public static Date getDiskLastModifiedDate(File file) {
		if (file == null || !file.exists()) {
			return new Date(0);
		}
		if (file.isFile()) {
			return new Date(file.lastModified());
		}
		File[] fileArray = file.listFiles();
		Date returned = new Date(file.lastModified());
		if (fileArray == null) {
			return returned;
		}
		if (fileArray.length > 0) {
			returned = new Date(0); // the lastModified() takes into account contained files and directories (but we want to ignore
			// everything related to CVS)
		}
		for (int i = 0; i < fileArray.length; i++) {
			File curFile = fileArray[i];
			if (curFile.isDirectory() && curFile.getName().equals("CVS")) {
				continue;
			}
			if (curFile.isFile() && curFile.getName().equals(".cvsignore")) {
				continue;
			}
			Date d = getDiskLastModifiedDate(curFile);
			if (d.after(returned)) {
				returned = d;
			}
		}
		return returned;
	}

	/*
	private static boolean isStringValidForFileName(String s) {
		return s != null && !UNACCEPTABLE_CHARS_PATTERN.matcher(s).find() && s.matches(VALID_FILE_NAME_REGEXP) && s.length() < 256;
	}
	
	public static String removeNonASCIIAndPonctuationAndBadFileNameChars(String s) {
		if (s.lastIndexOf(".") > 0) {
			String s1 = s.substring(s.lastIndexOf(".") + 1);
			String s0 = s.substring(0, s.lastIndexOf("."));
			s0 = performCleanup(s0);
			s1 = performCleanup(s1);
			return s0 + "." + s1;
		}
	
		return performCleanup(s);
	}
	
	private static String performCleanup(String s) {
		String result = StringUtils.convertAccents(s);
		result = result.replaceAll(BAD_CHARACTERS_FOR_FILE_NAME_REG_EXP, "-");
		result = result.replaceAll("\\P{ASCII}+", "-");
		result = result.replaceAll("[^\\w]+", "-");
		return result;
	}
	*/

	/**
	 * @param fileName
	 * @return
	 */
	public static String getValidFileName(String fileName) {
		fileName = fileName.replace('\\', '/');
		StringBuffer sb = new StringBuffer();
		Matcher m = UNACCEPTABLE_SLASH_PATTERN.matcher(fileName);
		while (m.find()) {
			m.appendReplacement(sb, "/");
		}
		m.appendTail(sb);
		m = UNACCEPTABLE_CHARS_PATTERN.matcher(sb.toString());
		sb = new StringBuffer();
		while (m.find()) {
			m.appendReplacement(sb, "_");
		}
		m.appendTail(sb);
		fileName = sb.toString();
		String extension = null;
		if (fileName.length() > 4) {
			if (fileName.charAt(fileName.length() - 4) == '.') {
				extension = fileName.substring(fileName.length() - 4);
				fileName = fileName.substring(0, fileName.length() - 4);
			}
			else if (fileName.charAt(fileName.length() - 5) == '.') {
				extension = fileName.substring(fileName.length() - 5);
				fileName = fileName.substring(0, fileName.length() - 5);
			}
		}
		sb.setLength(0);
		int previous = 0;
		int index;
		while ((index = fileName.indexOf('/', previous)) > -1) {
			if (index - previous > 240) {
				sb.append(fileName.substring(previous, previous + 240)).append('/');
			}
			else {
				sb.append(fileName.substring(previous, index + 1));
			}
			previous = index + 1;
		}
		index = fileName.length();
		if (index - previous > 240) {
			sb.append(fileName.substring(previous, previous + 240));
		}
		else {
			sb.append(fileName.substring(previous, index));
		}
		if (extension != null) {
			sb.append(extension);
		}
		return sb.toString();
	}

	/**
	 * Deletes a file and log a configurable warning if the deletion fails.
	 * 
	 * @param file
	 *            file to delete
	 * @param warnMsg
	 *            warning message. It will be translated with {@code tr()} and must contain a single parameter <code>{0}</code> for the file
	 *            path
	 * @return {@code true} if and only if the file is successfully deleted; {@code false} otherwise
	 * @since 9296
	 */
	public static boolean deleteFile(File file) {
		boolean result = file.delete();
		if (!result) {
			System.err.println("Cannot delete " + file);
		}
		return result;
	}

	/**
	 * @param dir
	 */
	public static void deleteDir(File dir) {
		if (!dir.isDirectory()) {
			System.err.println("Tried to delete a directory but file is not a directory: " + dir.getAbsolutePath());
			return;
		}
		File[] f = dir.listFiles();
		if (f == null) {
			return;
		}
		for (int i = 0; i < f.length; i++) {
			File file = f[i];
			if (file.isDirectory()) {
				deleteDir(file);
			}
			else {
				file.delete();
			}
		}
		dir.delete();
	}

	/**
	 * Recursively deletes all the files of the specified directory. Directories themselves are not removed.
	 * 
	 * @param dir
	 */
	/*
	private static void deleteFilesInDir(File dir) {
		deleteFilesInDir(dir, false);
	}
	
	private static void deleteFilesInDir(File dir, boolean keepCVSTags) {
		if (!dir.isDirectory()) {
			System.err.println("Tried to delete a directory but file is not a directory: " + dir.getAbsolutePath());
			return;
		}
		if (keepCVSTags && dir.getName().equals("CVS")) {
			System.err.println("Tried to delete CVS directory but keepCVSTags flag is true!");
			return;
		}
	
		File[] f = dir.listFiles();
		if (f == null) {
			return;
		}
		for (int i = 0; i < f.length; i++) {
			File file = f[i];
			if (file.isDirectory()) {
				if (!file.getName().equals("CVS") || !keepCVSTags) {
					deleteFilesInDir(file, keepCVSTags);
				}
			}
			else {
				file.delete();
			}
		}
	}
	*/

	public static boolean directoryContainsFile(File directory, File file, boolean recursive) {
		if (file == null) {
			return false;
		}
		if (file.equals(directory)) {
			return true;
		}
		if (file.getParentFile() != null) {
			if (recursive) {
				return directoryContainsFile(directory, file.getParentFile(), recursive);
			}
			return directory.equals(file.getParentFile());
		}
		return false;
	}

	public static int distance(File f1, File f2) {
		return Math.min(distance(f1, f2, false), distance(f2, f1, false));
	}

	private static int distance(File f1, File f2, boolean computeInverse) {
		if (f2.equals(f1)) {
			return 0;
		}
		if (f2.getParentFile() != null) {
			int d1 = distance(f1, f2.getParentFile());
			if (d1 < 1000) {
				return d1 + 1;
			}
		}
		if (computeInverse) {
			if (f1.getParentFile() != null) {
				int d2 = distance(f2, f1.getParentFile());
				if (d2 < 1000) {
					return d2 + 1;
				}
			}
		}
		return 1000;
	}

	/*
		private static void makeFileHidden(File f) {
			if (ToolBox.isWindows()) {
				try {
					Runtime.getRuntime().exec("attrib +H \"" + f.getAbsolutePath() + "\"");
				} catch (IOException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		
		private static void unmakeFileHidden(File f) {
			if (ToolBox.isWindows()) {
				try {
					Runtime.getRuntime().exec("attrib -H \"" + f.getAbsolutePath() + "\"");
				} catch (IOException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	
	private static void makeFileSystem(File f) {
		if (ToolBox.isWindows()) {
			try {
				Runtime.getRuntime().exec("attrib +S \"" + f.getAbsolutePath() + "\"");
			} catch (IOException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	private static void unmakeFileSystem(File f) {
		if (ToolBox.isWindows()) {
			try {
				Runtime.getRuntime().exec("attrib -S \"" + f.getAbsolutePath() + "\"");
			} catch (IOException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
		 */

	public static boolean isFileContainedIn(File aFile, File ancestorFile) {
		File current = aFile;
		while (current != null) {
			if (current.equals(ancestorFile)) {
				return true;
			}
			current = current.getParentFile();
		}
		return false;
	}

	/**
	 * Finds a relative path to a given file, relative to a specified directory.
	 * 
	 * @param file
	 *            file that the relative path should resolve to
	 * @param relativeToDir
	 *            directory that the path should be relative to
	 * @return a relative path. This always uses / as the separator character.
	 */
	public static String makeFilePathRelativeToDir(File file, File relativeToDir) throws IOException {
		String canonicalFile = file.getCanonicalPath();
		String canonicalRelTo = relativeToDir.getCanonicalPath();
		String[] filePathComponents = getPathComponents(canonicalFile);
		String[] relToPathComponents = getPathComponents(canonicalRelTo);
		int i = 0;
		while (i < filePathComponents.length && i < relToPathComponents.length && filePathComponents[i].equals(relToPathComponents[i])) {
			i++;
		}
		StringBuffer buf = new StringBuffer();
		for (int j = i; j < relToPathComponents.length; j++) {
			buf.append("../");
		}
		for (int j = i; j < filePathComponents.length - 1; j++) {
			buf.append(filePathComponents[j]).append('/');
		}
		buf.append(filePathComponents[filePathComponents.length - 1]);
		return buf.toString();
	}

	/**
	 * Splits a path into components using the OS file separator character. This can be used on the results of File.getCanonicalPath().
	 * 
	 * @param canonicalPath
	 *            a file path that uses the OS file separator character
	 * @return an array of strings, one for each component of the path
	 */
	public static String[] getPathComponents(String canonicalPath) {
		String regex = File.separator;
		if (regex.equals("\\")) {
			regex = "\\\\";
		}
		return canonicalPath.split(regex);
	}

	/*
	 * @param file
	 * @param wd
	 * @return
	 * @throws IOException
	 */
	/*public static String makeFilePathRelativeToDir(File file, File dir) throws IOException {
		System.out.println("file=" + file.getAbsolutePath());
		System.out.println("dir=" + dir.getAbsolutePath());
		file = file.getCanonicalFile();
		dir = dir.getCanonicalFile();
		String d = dir.getCanonicalPath().replace('\\', '/');
		String f = file.getCanonicalPath().replace('\\', '/');
		int i = 0;
		while (i < d.length() && i < f.length() && d.charAt(i) == f.charAt(i)) {
			i++;
		}
		String common = d.substring(0, i);
		if (!new File(common).exists()) {
			if (common.indexOf('/') > -1) {
				common = common.substring(0, common.lastIndexOf('/') + 1);
			}
			if (!new File(common).exists()) {
				System.err.println("WARNING\tNothing in common between\n" + file.getAbsolutePath() + " and\n" + dir.getAbsolutePath());
				return file.getAbsolutePath();
			}
		}
		File commonFather = new File(common);
		System.out.println("commonFather=" + commonFather.getAbsolutePath());
		File parentDir = dir;
		StringBuilder sb = new StringBuilder();
		while (parentDir != null && !commonFather.equals(parentDir)) {
			sb.append("../");
			parentDir = parentDir.getParentFile();
		}
		sb.append(f.substring(common.length()));
		if (sb.charAt(0) == '/') {
			return sb.substring(1);
		}
		System.out.println("returned=" + sb.toString());
		return sb.toString();
	}*/

	/*
	private static File createTempFile(InputStream in) {
		File tempFile;
		try {
			tempFile = File.createTempFile("FlexoTempFile", null);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		tempFile.deleteOnExit();
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(tempFile);
			byte[] b = new byte[8192];
			int r;
			while ((r = in.read(b)) > 0) {
				fos.write(b, 0, r);
			}
			return tempFile;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} finally {
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	*/

	/**
	 * Creates an empty directory in the default temporary-file directory, using the given prefix and suffix to generate its name.
	 * 
	 * @param prefix
	 *            The prefix string to be used in generating the directory's name; must be at least three characters long
	 * @param suffix
	 *            The suffix string to be used in generating the directory's name; may be null, in which case the suffix ".tmp" will be used
	 * @return An abstract pathname denoting a newly-created empty directory
	 * @throws IOException
	 */
	public static File createTempDirectory(String prefix, String suffix) throws IOException {
		File tmp = File.createTempFile(prefix, suffix);
		File tmpDir = new File(tmp.getAbsolutePath());
		if (tmp.delete() && tmpDir.mkdirs()) {
			return tmpDir;
		}
		tmpDir = new File(System.getProperty("java.io.tmpdir"), prefix + suffix);
		tmpDir.mkdirs();
		return tmpDir;
	}

	public static Vector<File> listFilesRecursively(File dir, final FilenameFilter filter) {
		Vector<File> files = new Vector<>();
		File[] listFiles = dir.listFiles();
		if (listFiles != null)
			for (File file : listFiles) {
				if (file.isDirectory()) {
					files.addAll(listFilesRecursively(file, filter));
				}
				else if (filter.accept(dir, file.getName())) {
					files.add(file);
				}
			}
		return files;
	}

	/*
	private static String lowerCaseExtension(String fileName) {
		if (fileName == null) {
			return null;
		}
		if (fileName.indexOf('.') > -1) {
			return fileName.substring(0, fileName.lastIndexOf('.')) + fileName.substring(fileName.lastIndexOf('.')).toLowerCase();
		}
		return fileName;
	}
	*/

	/**
	 * An extension to Java's API rename method. Will attempt Java's method of doing the rename, if this fails, this method will then
	 * attempt to forcibly copy the old file to the new file name, and then delete the old file. (This in appearance makes it look like a
	 * file rename has occurred.) The method will also attempt to preserve the new file's modification times and permissions to equal that
	 * of the original file's.
	 * 
	 * @param source
	 *            File
	 * @param destination
	 *            File
	 * @return boolean
	 * @throws IOException
	 */
	public static boolean rename(File source, File destination) throws IOException {
		// First (very important on Windows) delete the destination if it exists (rename will fail on Windows if destination
		// exists)
		if (destination.exists()) {
			destination.delete();
		}
		// Do a normal API rename attempt
		if (source.renameTo(destination)) {
			return true;
		}

		if (destination.isDirectory()) {
			System.err.println("Cannot rename from " + source + " to " + destination);
			return false;
		}

		FileUtils.createNewFile(destination);
		// API rename attempt failed, forcibly copy
		try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(source));
				BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(destination))) {
			// Do the copy
			pipeStreams(bos, bis);
			// Close the files
			bos.flush();
		}

		// Attempt to preserve file modification times
		destination.setLastModified(source.lastModified());
		if (!source.canWrite()) {
			destination.setReadOnly();
		}

		// Delete the original
		source.delete();

		return true;
	}

	private static void pipeStreams(BufferedOutputStream out, BufferedInputStream in) throws IOException {
		byte[] buffer = new byte[8192];
		int read;
		while ((read = in.read(buffer, 0, 8192)) != -1) {
			out.write(buffer, 0, read);
		}
		out.flush();
	}

	public static String createOrUpdateFileFromURL(URL url, File file) {
		return createOrUpdateFileFromURL(url, file, null);
	}

	private static String createOrUpdateFileFromURL(URL url, File file, Map<String, String> headers) {
		long lastModified = 0;
		String fileContent = null;
		if (file.exists()) {
			lastModified = file.lastModified();
			try {
				fileContent = FileUtils.fileContents(file);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if (url != null) {
			try {
				URLConnection c = url.openConnection();
				if (headers != null) {
					for (Map.Entry<String, String> h : headers.entrySet()) {
						c.addRequestProperty(h.getKey(), h.getValue());
					}
				}
				if (c instanceof HttpURLConnection) {
					HttpURLConnection connection = (HttpURLConnection) c;
					connection.setIfModifiedSince(lastModified);
					connection.connect();
					if (connection.getResponseCode() == 200) {
						fileContent = FileUtils.fileContents(connection.getInputStream(), "UTF-8");
						FileUtils.saveToFile(file, fileContent);
					}
					connection.disconnect();
				}
				else {
					if (c.getDate() == 0 || c.getDate() > lastModified) {
						fileContent = FileUtils.fileContents(c.getInputStream(), "UTF-8");
						FileUtils.saveToFile(file, fileContent);
					}
				}
			} catch (UnknownHostException e) {
				LOGGER.warning("Could access to url " + url + ". Please check your internet connexion");
			} catch (IOException e) {
				LOGGER.warning("Could not read url " + url);
				e.printStackTrace();

			}
		}
		return fileContent;
	}

	public static File getApplicationDataDirectory() {
		File dir = new File(System.getProperty("user.home"), ".openflexo");
		if (ToolBox.isWindows()) {
			String appData = System.getenv("APPDATA");
			if (appData != null) {
				File f = new File(appData);
				if (f.isDirectory() && f.canWrite()) {
					dir = new File(f, "OpenFlexo");
				}
			}
		}
		else if (ToolBox.isMacOS()) {
			dir = new File(new File(System.getProperty("user.home")), "Library/OpenFlexo");
		}
		return dir;
	}

	// Unused private static final String MACOS_DOC_DIRECTORY_KEY = "docs";

	/*
	private static File getDocumentDirectory() {
		if (ToolBox.isMacOS()) {
			try {
				Class<?> fileManagerClass = Class.forName("com.apple.eio.FileManager");
				short userDomain = fileManagerClass.getField("kUserDomain").getShort(null);
				Method typeToInt = fileManagerClass.getDeclaredMethod("OSTypeToInt", String.class);
				Method findFolder = fileManagerClass.getDeclaredMethod("findFolder", short.class, int.class);
				int docDirectoryInt = (Integer) typeToInt.invoke(null, MACOS_DOC_DIRECTORY_KEY);
				String documentDirectory = (String) findFolder.invoke(null, userDomain, docDirectoryInt);
				return new File(documentDirectory);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (NoSuchFieldException e) {
				e.printStackTrace();
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		}
		else if (ToolBox.isWindows()) {
			String value = WinRegistryAccess.getRegistryValue(WIN_REGISTRY_DOCUMENTS_KEY_PATH, WIN_REGISTRY_DOCUMENTS_ATTRIBUTE,
					WinRegistryAccess.REG_EXPAND_SZ_TOKEN);
			value = WinRegistryAccess.substituteEnvironmentVariable(value);
			if (value != null) {
				return new File(value);
			}
		}
		return new File(System.getProperty("user.home"), "Documents");
	}
	*/
}
