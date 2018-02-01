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

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.AccessibleObject;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;

import javax.swing.Icon;
import javax.swing.UIManager;
import javax.swing.filechooser.FileSystemView;
import javax.swing.filechooser.FileView;

/**
 * @author bmangez
 */
public class ToolBox {

	private enum PlatformKind {
		Windows, Linux, Macos, Other
	};

	private static PlatformKind PLATFORM;

	static {
		String osName = System.getProperty("os.name");
		if (osName.indexOf("Mac OS") > -1) {
			PLATFORM = PlatformKind.Macos;
		}
		else if (osName.indexOf("Windows") > -1) {
			PLATFORM = PlatformKind.Windows;
		}
		else if (osName.indexOf("Linux") > -1) {
			PLATFORM = PlatformKind.Linux;
		}
		else {
			PLATFORM = PlatformKind.Other;
		}
	}

	public static boolean isMacOS() {
		return PLATFORM == PlatformKind.Macos;
	}

	public static boolean isWindows() {
		return PLATFORM == PlatformKind.Windows;
	}

	public static boolean isLinux() {
		return PLATFORM == PlatformKind.Linux;
	}

	public static boolean isOther() {
		return PLATFORM == PlatformKind.Other;
	}

	/*
		public static String memoryInfo() {
			StringBuffer returned = new StringBuffer();
			long maxMemory = Runtime.getRuntime().maxMemory();
			returned.append("Memory free:" + (Runtime.getRuntime().freeMemory() / 1024 / 1024) + "Mb");
			returned.append(" max:" + (maxMemory == Long.MAX_VALUE ? "no limit" : maxMemory / 1024 / 1024) + "Mb");
			returned.append(" total:" + (Runtime.getRuntime().totalMemory() / 1024 / 1024) + "Mb");
			return returned.toString();
		}
	*/
	public static String replaceStringByStringInString(String replacedString, String aNewString, String message) {
		if (message == null || message.equals("")) {
			return "";
		}
		if (replacedString == null || replacedString.equals("")) {
			return message;
		}
		if (aNewString == null || aNewString.equals("")) {
			aNewString = "";
		}

		// String newString = "";
		// int replacedStringLength = replacedString.length();
		// int indexOfTag = message.indexOf(replacedString);
		// while (indexOfTag != -1) {
		// newString = newString + message.substring(0, indexOfTag) + aNewString;
		// message = message.substring(indexOfTag + replacedStringLength);
		// indexOfTag = message.indexOf(replacedString);
		// }
		// return newString + message;

		StringBuffer newString = new StringBuffer("");
		int replacedStringLength = replacedString.length();
		int indexOfTag = message.indexOf(replacedString);
		while (indexOfTag != -1) {
			newString.append(message.substring(0, indexOfTag)).append(aNewString);
			message = message.substring(indexOfTag + replacedStringLength);
			indexOfTag = message.indexOf(replacedString);
		}
		return newString.append(message).toString();
	}

	/*
		public static String replaceStringByStringInStringOld(String replacedString, String aNewString, String message) {
			if (message == null || message.equals("")) {
				return "";
			}
			if (replacedString == null || replacedString.equals("")) {
				return message;
			}
			if (aNewString == null || aNewString.equals("")) {
				aNewString = "";
			}
	
			String newString = "";
			int replacedStringLength = replacedString.length();
			int indexOfTag = message.indexOf(replacedString);
			while (indexOfTag != -1) {
				newString = newString + message.substring(0, indexOfTag) + aNewString;
				message = message.substring(indexOfTag + replacedStringLength);
				indexOfTag = message.indexOf(replacedString);
			}
			return newString + message;
	
		}
	*/
	private static String capitalize(String s, boolean removeStartingUnderscore) {
		if (s == null) {
			return null;
		}
		if (s.length() == 0) {
			return s;
		}
		if (s.startsWith("_") && removeStartingUnderscore) {
			s = s.substring(1);
		}
		if (s.length() == 0) {
			return s;
		}
		if (s.length() == 1) {
			return s.toUpperCase();
		}
		return s.substring(0, 1).toUpperCase() + s.substring(1);

	}

	private static String capitalize(String s) {
		return capitalize(s, false);
	}

	/*
		public static String uncapitalize(String s) {
			if (s == null) {
				return null;
			}
			if (s.length() > 0 && Character.isUpperCase(s.charAt(0))) {
				s = Character.toLowerCase(s.charAt(0)) + s.substring(1);
			}
			return s;
		}
	
	public static String cleanStringForProcessDictionaryKey(String s) {
		String cleanedString = getJavaName(s, false, true);
	
		if ("_".equals(cleanedString)) {
			return null;
		}
		return cleanedString;
	}
	*/

	/**
	 * Replace ",',\n,\r by blank
	 * 
	 * @param comment
	 * @return a String to use in a javascript
	 */
	/*
	public static String getJavascriptComment(String comment) {
		if (comment == null) {
			return null;
		}
		return ToolBox.replaceStringByStringInString("\r", " ", ToolBox.replaceStringByStringInString("\n", " ",
				ToolBox.replaceStringByStringInString("\"", " ", ToolBox.replaceStringByStringInString("'", " ", comment))));
	}
	*/

	/**
	 * @deprecated use methods from JavaUtils
	 * @param name
	 * @return a java name ( starts with a minuscule, and no blanks, dot,..., convert accentuated characters)
	 */
	@Deprecated
	public static String getJavaName(String name) {
		if (name == null) {
			return null;
		}
		if (name.equals("")) {
			return name;
		}
		name = StringUtils.convertAccents(name);
		StringBuffer sb = new StringBuffer();
		Matcher m = JavaUtils.JAVA_VARIABLE_ACCEPTABLE_PATTERN.matcher(name);
		while (m.find()) {
			String group = m.group();
			if (sb.length() == 0 && !group.matches(JavaUtils.JAVA_BEGIN_VARIABLE_NAME_REGEXP)) {
				sb.append('_');
			}
			sb.append(group);
		}
		name = sb.toString();
		if (name.equals("")) {
			return "_";
		}
		if (ReservedKeyword.contains(name)) {
			return "_" + name;
		}
		return name;
	}

	/**
	 * 
	 * @param name
	 * @return a java name ( starts with a minuscule, and no blanks, dot,..., convert accentuated characters)
	 */
	/*
	public static String getWarName(String name) {
		if (name == null) {
			return null;
		}
		if (name.equals("")) {
			return name;
		}
		name = StringUtils.convertAccents(name);
		StringBuffer sb = new StringBuffer();
		Matcher m = WAR_NAME_ACCEPTABLE_PATTERN.matcher(name);
		while (m.find()) {
			String group = m.group();
			if (sb.length() == 0 && !group.matches(JavaUtils.JAVA_BEGIN_VARIABLE_NAME_REGEXP)) {
				sb.append('_');
			}
			sb.append(group);
		}
		name = sb.toString();
		if (name.equals("")) {
			return "_";
		}
		return name;
	}
	
	public static String convertStringToJavascriptString(String stringToConvert) {
		StringBuffer sb = new StringBuffer();
		Matcher m = JAVASCRIPT_CHAR_TO_ESCAPE_IN_STRINGS_PATTERN.matcher(stringToConvert);
		while (m.find()) {
			m.appendReplacement(sb, "\\\\$0");
		}
		m.appendTail(sb);
		return sb.toString().replaceAll("[\n\r]", "\\\\n");
	}
	
	public static String convertJavaStringToDBName(String javaString) {
		int index = 0;
		boolean lastCharIsUpperCase = true;
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < javaString.length(); i++) {
			char c = javaString.charAt(i);
			if (Character.isUpperCase(c)) {
				if (i != 0) {
					if (!lastCharIsUpperCase) {
						sb.append(javaString.substring(index, i).toUpperCase());
						sb.append("_");
						index = i;
					}
					else {
						if (i + 1 < javaString.length()) {
							if (!Character.isUpperCase(javaString.charAt(i + 1))) {
								sb.append(javaString.substring(index, i).toUpperCase());
								sb.append("_");
								index = i;
							}
						}
						else {
							sb.append(javaString.substring(index, i + 1).toUpperCase());
							index = i;
						}
					}
				}
				lastCharIsUpperCase = true;
			}
			else {
				if (i + 1 == javaString.length()) {
					sb.append(javaString.substring(index, i + 1).toUpperCase());
				}
				lastCharIsUpperCase = false;
			}
		}
		return sb.toString();
	}
	*/

	// Unused private static final String JAVASCRIPT_CHAR_TO_ESCAPE_IN_STRINGS_REG_EXP = "['\\\\]";

	// Unused private static final Pattern JAVASCRIPT_CHAR_TO_ESCAPE_IN_STRINGS_PATTERN = Pattern
	// Unused .compile(JAVASCRIPT_CHAR_TO_ESCAPE_IN_STRINGS_REG_EXP);

	// Unused private static final String WAR_NAME_ACCEPTABLE_CHARS = "[_A-Za-z0-9.]+";

	// Unused private static final Pattern WAR_NAME_ACCEPTABLE_PATTERN = Pattern.compile(WAR_NAME_ACCEPTABLE_CHARS);

	/**
	 * Getter method for the attribute pLATFORM
	 * 
	 * @return Returns the pLATFORM.
	 */

	public static String getPLATFORM() {
		return PLATFORM.toString();
	}

	/*
	public static class RequestResponse {
		public int status;
		public String response;
	}
	
	public static RequestResponse getRequest(Hashtable<String, String> param, String url) throws IOException {
		StringBuffer paramsAsString = new StringBuffer("");
		if (param != null && param.size() > 0) {
			// paramsAsString.append("?");
			Enumeration<String> en = param.keys();
			while (en.hasMoreElements()) {
				String key = en.nextElement();
				String value = param.get(key);
				try {
					paramsAsString.append(URLEncoder.encode(key, "UTF-8")).append("=").append(URLEncoder.encode(value, "UTF-8"));
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				if (en.hasMoreElements()) {
					paramsAsString.append("&");
				}
			}
	
		}
	
		// Create a URL for the desired page
		URL local_url = new URL(url);
		URLConnection conn = local_url.openConnection();
		conn.setDoOutput(true);
		OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
		wr.write(paramsAsString.toString());
		wr.flush();
		// Read all the text returned by the server
		int httpStatus = 200;
		if (conn instanceof HttpURLConnection) {
			httpStatus = ((HttpURLConnection) conn).getResponseCode();
		}
		BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		String str;
		StringBuffer reply = new StringBuffer();
		while ((str = in.readLine()) != null) {
			reply.append(str).append("\n");
		}
		wr.close();
		in.close();
		RequestResponse response = new RequestResponse();
		response.response = reply.toString();
		response.status = httpStatus;
		return response;
	
	}
	
	public static RequestResponse postRequest(Hashtable<String, String> parameters, String url) {
		try {
			// Construct data
			StringBuffer data = new StringBuffer();
			if (parameters != null && parameters.size() > 0) {
				Enumeration<String> en = parameters.keys();
				while (en.hasMoreElements()) {
					String key = en.nextElement();
					String value = parameters.get(key);
					data.append(URLEncoder.encode(key, "UTF-8")).append("=").append(URLEncoder.encode(value, "UTF-8"));
					if (en.hasMoreElements()) {
						data.append("&");
					}
				}
	
			}
	
			// Send data
			URL local_url = new URL(url);
			URLConnection conn = local_url.openConnection();
			conn.setDoOutput(true);
			OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
			wr.write(data.toString());
			wr.flush();
	
			// Get the response
			int httpStatus = 200;
			if (conn instanceof HttpURLConnection) {
				httpStatus = ((HttpURLConnection) conn).getResponseCode();
			}
			StringBuffer reply = new StringBuffer();
			BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String line;
			while ((line = rd.readLine()) != null) {
				reply.append(line).append("\n");
			}
			wr.close();
			rd.close();
			RequestResponse response = new RequestResponse();
			response.response = reply.toString();
			response.status = httpStatus;
			return response;
		} catch (Exception e) {
			e.printStackTrace();
			RequestResponse response = new RequestResponse();
			response.response = e.getMessage();
			response.status = -1;
			return response;
		}
	
	}
	
	public static String serializeHashtable(Hashtable<String, String> params) {
		StringBuffer buf = new StringBuffer();
		String key = null;
		Enumeration<String> en = params.keys();
		while (en.hasMoreElements()) {
			key = en.nextElement();
			buf.append(key).append("=").append(params.get(key));
			if (en.hasMoreElements()) {
				buf.append("&");
			}
		}
		return buf.toString();
	}
	*/

	public static String getWodKeyPath(String s) {
		if (s == null) {
			return null;
		}
		s = ToolBox.replaceStringByStringInString("()", "", s);
		return s;
	}

	public static String stackTraceAsAString(Throwable e) {
		StringBuilder sb = new StringBuilder(
				"Exception " + e.getClass().getName() + ":\n" + (e.getMessage() != null ? e.getMessage() : ""));
		stackTraceAsAString(e, sb);
		return sb.toString();
	}

	private static void stackTraceAsAString(Throwable e, StringBuilder sb) {
		StackTraceElement[] stackTrace = e.getStackTrace();
		if (stackTrace != null) {
			for (int i = 0; i < stackTrace.length; i++) {
				sb.append("\tat " + stackTrace[i] + "\n");
			}
			if (e.getCause() != null) {
				sb.append("Caused by ").append(e.getCause().getClass().getName()).append('\n');
				stackTraceAsAString(e.getCause(), sb);
			}
		}
		else {
			sb.append("StackTrace not available\n");
		}
	}

	public static void openURL(String url) {
		try {
			Desktop.getDesktop().browse(new URI(url));
		} catch (IOException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}

	public static void showFileInExplorer(File fileToOpen) throws IOException {
		String[] command;
		if (ToolBox.isWindows()) {
			command = new String[3];
			command[0] = "explorer";
			command[1] = "/select,";
			command[2] = fileToOpen.getAbsolutePath();
		}
		else {
			command = new String[2];
			command[0] = "open";
			command[1] = fileToOpen.isDirectory() ? fileToOpen.getCanonicalPath() : fileToOpen.getParentFile().getCanonicalPath();
		}
		Runtime.getRuntime().exec(command);
	}

	/*
	public static boolean openFile(File fileToOpen) {
		try {
			Desktop.getDesktop().open(fileToOpen);
			return true;
		} catch (IOException e2) {
			e2.printStackTrace();
			return false;
		}
	}
	*/
	/**
	 * @param name
	 */
	/*
	public static String getDBTableNameFromPropertyName(String name) {
		StringBuffer sb = new StringBuffer();
		boolean previousCharIsUpperCase = false;
		for (int i = 0; i < name.length(); i++) {
			if (Character.isUpperCase(name.charAt(i))) {
				if (i == 0) {
					sb.append(name.charAt(i));
				}
				else if (i + 1 == name.length()) {
					sb.append(name.charAt(i));
				}
				else if (previousCharIsUpperCase) {
					if (Character.isUpperCase(name.charAt(i + 1))) {
						sb.append(name.charAt(i));
					}
					else {
						sb.append('_').append(name.charAt(i));
					}
				}
				else {
					sb.append('_').append(name.charAt(i));
				}
				previousCharIsUpperCase = true;
			}
			else {
				sb.append(Character.toUpperCase(name.charAt(i)));
				previousCharIsUpperCase = false;
			}
		}
		return sb.toString();
	}
	*/
	/*
		public static Document parseXMLData(StringReader xmlStream) throws IOException, JDOMException {
			SAXBuilder parser = new SAXBuilder();
			return parser.build(xmlStream);
		}
	*/
	private static Boolean fileChooserRequiresFix;

	public static boolean fileChooserRequiresFix() {
		if (fileChooserRequiresFix == null) {
			if (isWindows()) {
				String javaVersion = System.getProperty("java.version");
				String version;
				String release = null;
				if (javaVersion.indexOf('_') > 0) {
					version = javaVersion.substring(0, javaVersion.indexOf('_'));
					release = javaVersion.substring(javaVersion.indexOf('_') + 1);
				}
				else {
					version = javaVersion;
				}
				try {
					fileChooserRequiresFix = version.startsWith("1.6.0") && (release == null || Integer.parseInt(release) < 10);
				} catch (NumberFormatException e) {
					e.printStackTrace();
					fileChooserRequiresFix = true;
				}
			}
			else {
				fileChooserRequiresFix = false;
			}
		}
		return fileChooserRequiresFix;
	}

	// A deadlock can occurs with windows when retrieving an icon. It seems that it occurs on remote folders only
	// The following fixes that:
	// - check a user defined icon doesn't exists in the view,
	// - check if the folder is a drive
	// If this is true then return an icon, otherwise return null;
	public static Icon getIconFileChooserWithFix(File f, FileView view) {
		if (getIconFileChooserRequiresFix(f, view)) {
			return UIManager.getDefaults().getIcon("FileView.computerIcon");
		}
		return null;
	}

	public static boolean getIconFileChooserRequiresFix(File f, FileView view) {
		if (ToolBox.isWindows() && FileSystemView.getFileSystemView().isDrive(f) && (view == null || view.getIcon(f) == null)) {
			return true;
		}
		return false;
	}

	public static void fixFileChooser() {
		String[] cmd = new String[] { "regsvr32", "/u", "/s", System.getenv("windir") + "\\system32\\zipfldr.dll" };
		try {
			Runtime.getRuntime().exec(cmd);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/*
		public static void fixFileChooserDeadlock() {
			String[] cmd = new String[] { "regsvr32", "/u", "/s", System.getenv("windir") + "\\system32\\zipfldr.dll" };
			try {
				Runtime.getRuntime().exec(cmd);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	*/
	public static void undoFixFileChooser() {
		String[] cmd = new String[] { "regsvr32", "/s", System.getenv("windir") + "\\system32\\zipfldr.dll" };
		try {
			Runtime.getRuntime().exec(cmd);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param s
	 * @return a string to be inserted between single quote in js.
	 */
	/*
	public static String escapeStringForJS(String s) {
		if (s == null) {
			return null;
		}
		return s.replaceAll("\\\\", "\\\\\\\\").replaceAll("'", "\\\\'").replaceAll("\n", "\\\\n").replaceAll("\r", "");
	}
	
	public static String escapeStringForProperties(String s) {
		if (s == null) {
			return null;
		}
		return s.replaceAll("#", "\\\\#").replaceAll("!", "\\\\!").replaceAll("=", "\\\\=").replaceAll(":", "\\\\:");
	}
	
	public static String escapeStringForCsv(String s) {
		if (s == null) {
			return null;
		}
		return s.replaceAll("\"", "\"\"");
	}
	
	public static String getCsvLine(List<String> list) {
		List<List<String>> tmp = new ArrayList<>();
		tmp.add(list);
		return getCsv(tmp);
	}
	
	public static String getCsv(List<List<String>> list) {
		StringBuilder sb = new StringBuilder();
	
		boolean isFirstLine = true;
		for (List<String> line : list) {
			if (!isFirstLine) {
				sb.append("\n");
			}
	
			boolean isFirstValue = true;
			for (String value : line) {
				if (!isFirstValue) {
					sb.append(";");
				}
				if (!StringUtils.isEmpty(value)) {
					sb.append("\"" + escapeStringForCsv(value) + "\"");
				}
				isFirstValue = false;
			}
	
			isFirstLine = false;
		}
	
		return sb.toString();
	}
	
	public static List<String> parseCsvLine(String csvLine) {
		List<List<String>> result = parseCsv(csvLine);
	
		if (result.size() > 0) {
			return result.get(0);
		}
	
		return new ArrayList<>();
	}
	
	public static List<List<String>> parseCsv(String csvString) {
		csvString = csvString != null ? csvString.trim() : null;
		List<List<String>> result = new ArrayList<>();
	
		if (StringUtils.isEmpty(csvString)) {
			return result;
		}
	
		char separator;
		if (csvString.indexOf(';') == -1 && csvString.indexOf(',') > -1) {
			separator = ',';
		}
		else {
			separator = ';';
		}
	
		List<String> line = new ArrayList<>();
		StringBuilder currentValue = new StringBuilder();
		boolean isInsideQuote = false;
		boolean wasInsideQuote = false;
		for (int i = 0; i < csvString.length(); i++) {
			if (!wasInsideQuote && csvString.charAt(i) == '"' && (isInsideQuote || currentValue.toString().trim().length() == 0)) {
				if (i + 1 < csvString.length() && csvString.charAt(i + 1) == '"') { // Double quote, escape
					i++;
				}
				else {
					if (isInsideQuote) {
						wasInsideQuote = true;
					}
					else {
						currentValue = new StringBuilder();
					}
					isInsideQuote = !isInsideQuote;
					continue;
				}
			}
			else if ((csvString.charAt(i) == separator || csvString.charAt(i) == '\n') && !isInsideQuote) {
				line.add(currentValue.toString());
				currentValue = new StringBuilder();
				wasInsideQuote = false;
				if (csvString.charAt(i) == '\n') {
					result.add(line);
					line = new ArrayList<>();
				}
				continue;
			}
	
			if (!wasInsideQuote) {
				currentValue.append(csvString.charAt(i));
			}
		}
	
		if (result.size() > 0 || currentValue.length() > 0 || line.size() > 0) {
			line.add(currentValue.toString());
			result.add(line);
		}
	
		return result;
	}
	*/

	/**
	 * Returns the owner frame if not null, or the hidden frame otherwise.
	 * 
	 * @param owner
	 * @return
	 */
	/*
	public static Frame getFrame(Frame owner) {
		return owner == null ? Frame.getFrames().length > 0 ? Frame.getFrames()[0] : JOptionPane.getRootFrame() : owner;
	}
	
	public static String getMd5Hash(String toHash) throws NoSuchAlgorithmException {
		if (toHash == null) {
			return null;
		}
		java.security.MessageDigest md5 = java.security.MessageDigest.getInstance("MD5");
		byte dataBytes[] = toHash.getBytes();
		md5.update(dataBytes);
		byte digest[] = md5.digest();
	
		StringBuffer hashString = new StringBuffer();
	
		for (int i = 0; i < digest.length; ++i) {
			String hex = Integer.toHexString(digest[i]);
	
			if (hex.length() == 1) {
				hashString.append('0');
				hashString.append(hex.charAt(hex.length() - 1));
			}
			else {
				hashString.append(hex.substring(hex.length() - 2));
			}
		}
		return hashString.toString();
	}
	*/
	public static String[] getHostPortFromString(String hostPort, int defaultPort) {
		int hostPortSepIndex = hostPort.indexOf(":");
		if (hostPortSepIndex > -1) {
			return new String[] { hostPort.substring(0, hostPortSepIndex), hostPort.substring(hostPortSepIndex + 1) };
		}
		return new String[] { hostPort, String.valueOf(defaultPort) };
	}

	public static String getContentAtURL(URL url) throws UnsupportedEncodingException, IOException {
		if (url.getProtocol().toLowerCase().startsWith("http")) {
			return getContentAtHTTPURL(url);
		}
		else if (url.getProtocol().toLowerCase().startsWith("file")) {
			return getContentAtFileURL(url);
		}
		else {
			System.err.println("Can't handle prototcol: " + url.getProtocol());
			return null;
		}
	}

	public static String getContentAtHTTPURL(URL url) throws IOException {
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		int httpStatus = conn.getResponseCode();
		if (httpStatus > 199 && httpStatus < 300) {
			try (InputStream is = conn.getInputStream()) {
				return getContentFromInputStream(is);
			}
		}
		else if (httpStatus > 299 && httpStatus < 400) {
			return getContentAtHTTPURL(new URL(conn.getHeaderField("Location")));
		}
		return null;
	}

	public static String getContentAtFileURL(URL url) throws UnsupportedEncodingException, IOException {
		return getContentFromInputStream(url.openStream());
	}

	public static String getContentFromInputStream(InputStream is) throws IOException, UnsupportedEncodingException {
		StringBuilder sb = new StringBuilder();
		byte[] b = new byte[1024];
		while (is.available() > 0) {
			int read = is.read(b);
			sb.append(new String(b, 0, read, "UTF-8"));
		}
		return sb.toString();
	}

	/*
		public static String getSystemProperties() {
			return getSystemProperties(false);
		}
	*/
	public static String getSystemProperties(boolean replaceBackslashInClasspath) {
		StringBuilder sb = new StringBuilder();
		for (Entry<Object, Object> e : new TreeMap<>(System.getProperties()).entrySet()) {
			String key = (String) e.getKey();
			if ("line.separator".equals(key)) {
				String nl = (String) e.getValue();
				nl = nl.replace("\r", "\\r");
				nl = nl.replace("\n", "\\n");
				sb.append(key).append(" = ").append(nl).append('\n');
			}
			else if ("java.class.path".equals(key)) {
				String nl = (String) e.getValue();
				nl = nl.replace('\\', '/');
				sb.append(key).append(" = ").append(nl).append('\n');
			}
			else {
				sb.append(key).append(" = ").append(e.getValue()).append('\n');
			}
		}
		return sb.toString();
	}

	public static String getStackTraceAsString(Throwable t) {
		if (t == null) {
			return null;
		}
		StringBuilder returned = new StringBuilder();
		if (t.getStackTrace() != null) {
			for (StackTraceElement ste : t.getStackTrace()) {
				returned.append("\tat ").append(ste).append("\n");
			}
		}
		return returned.toString();
	}

	public static List<?> getListFromIterable(Object iterable) {
		if (iterable instanceof List) {
			return (List<?>) iterable;
		}
		if (iterable instanceof Collection) {
			return new ArrayList<Object>((Collection<?>) iterable);
		}
		if (iterable instanceof Iterable) {
			List<Object> list = new ArrayList<>();
			for (Object o : (Iterable<?>) iterable) {
				list.add(o);
			}
			return list;
		}
		if (iterable instanceof Enumeration) {
			List<Object> list = new ArrayList<>();
			for (Enumeration<?> en = (Enumeration<?>) iterable; en.hasMoreElements();) {
				list.add(en.nextElement());
			}
			return list;
		}
		return null;
	}

	public static boolean isMacOSLaf() {
		return UIManager.getLookAndFeel().getName().equals("Mac OS X");
	}

	public static boolean isWindowsLaf() {
		return UIManager.getLookAndFeel().getName().startsWith("Windows");
	}

	public static boolean isNimbusLaf() {
		return UIManager.getLookAndFeel().getName().equals("Nimbus");
	}

	/**
	 * Returns the Java version as an int value.
	 * 
	 * @return the Java version as an int value (8, 9, etc.)
	 * @since 12130
	 */
	public static int getJavaVersion() {
		String version = System.getProperty("java.version");
		if (version.startsWith("1.")) {
			version = version.substring(2);
		}
		// Allow these formats:
		// 1.8.0_72-ea
		// 9-ea
		// 9
		// 9.0.1
		int dotPos = version.indexOf('.');
		int dashPos = version.indexOf('-');
		return Integer.parseInt(version.substring(0, dotPos > -1 ? dotPos : dashPos > -1 ? dashPos : 1));
	}

	/**
	 * Returns the Java update as an int value.
	 * 
	 * @return the Java update as an int value (121, 131, etc.)
	 * @since 12217
	 */
	/*
	public static int getJavaUpdate() {
		String version = System.getProperty("java.version");
		if (version.startsWith("1.")) {
			version = version.substring(2);
		}
		// Allow these formats:
		// 1.8.0_72-ea
		// 9-ea
		// 9
		// 9.0.1
		int undePos = version.indexOf('_');
		int dashPos = version.indexOf('-');
		if (undePos > -1) {
			return Integer.parseInt(version.substring(undePos + 1, dashPos > -1 ? dashPos : version.length()));
		}
		int firstDotPos = version.indexOf('.');
		int lastDotPos = version.lastIndexOf('.');
		if (firstDotPos == lastDotPos) {
			return 0;
		}
		return firstDotPos > -1 ? Integer.parseInt(version.substring(firstDotPos + 1, lastDotPos > -1 ? lastDotPos : version.length())) : 0;
	}
	*/
	/**
	 * Returns the Java build number as an int value.
	 * 
	 * @return the Java build number as an int value (0, 1, etc.)
	 * @since 12217
	 */

	/*
	public static int getJavaBuild() {
		String version = System.getProperty("java.runtime.version");
		int bPos = version.indexOf('b');
		int pPos = version.indexOf('+');
		try {
			return Integer.parseInt(version.substring(bPos > -1 ? bPos + 1 : pPos + 1, version.length()));
		} catch (NumberFormatException e) {
			e.printStackTrace();
			return 0;
		}
	}
	*/
	/**
	 * Returns the JRE expiration date.
	 * 
	 * @return the JRE expiration date, or null
	 * @since 12219
	 */

	/*
	public static Date getJavaExpirationDate() {
		try {
			Object value = null;
			Class<?> c = Class.forName("com.sun.deploy.config.BuiltInProperties");
			try {
				value = c.getDeclaredField("JRE_EXPIRATION_DATE").get(null);
			} catch (NoSuchFieldException e) {
				// Field is gone with Java 9, there's a method instead
				e.printStackTrace();
				value = c.getDeclaredMethod("getProperty", String.class).invoke(null, "JRE_EXPIRATION_DATE");
			}
			if (value instanceof String) {
				return DateFormat.getDateInstance(3, Locale.US).parse((String) value);
			}
		} catch (IllegalArgumentException | ReflectiveOperationException | SecurityException | ParseException e) {
			System.err.println("Cannot find class: com.sun.deploy.config.BuiltInProperties");
		}
		return null;
	}
	*/
	/**
	 * Returns the latest version of Java, from Oracle website.
	 * 
	 * @return the latest version of Java, from Oracle website
	 * @since 12219
	 */
	/*
	public static String getJavaLatestVersion() {
		try {
			return HttpClient.create(new URL(
					Config.getPref().get("java.baseline.version.url", "http://javadl-esd-secure.oracle.com/update/baseline.version")))
					.connect().fetchContent().split("\n")[0];
		} catch (IOException e) {
			Logging.error(e);
		}
	return null;
	
	}*/

	/**
	 * Updates a given system property.
	 * 
	 * @param key
	 *            The property key
	 * @param value
	 *            The property value
	 * @return the previous value of the system property, or {@code null} if it did not have one.
	 * @since 7894
	 */
	public static String updateSystemProperty(String key, String value) {
		if (value != null) {
			String old = System.setProperty(key, value);
			/*if (Logging.isDebugEnabled() && !value.equals(old)) {
			    if (!key.toLowerCase(Locale.ENGLISH).contains("password")) {
			        Logging.debug("System property '" + key + "' set to '" + value + "'. Old value was '" + old + '\'');
			    } else {
			        Logging.debug("System property '" + key + "' changed.");
			    }
			}*/
			return old;
		}
		return null;
	}

	public static String execOutput(List<String> command) throws IOException, ExecutionException, InterruptedException {
		Process p = new ProcessBuilder(command).start();
		try (BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream(), StandardCharsets.UTF_8))) {
			StringBuilder all = null;
			String line;
			while ((line = input.readLine()) != null) {
				if (all == null) {
					all = new StringBuilder(line);
				}
				else {
					all.append('\n');
					all.append(line);
				}
			}
			String msg = all != null ? all.toString() : null;
			if (p.waitFor() != 0) {
				throw new ExecutionException(msg, null);
			}
			return msg;
		}
	}

	/**
	 * Sets {@code AccessibleObject}(s) accessible.
	 * 
	 * @param objects
	 *            objects
	 * @see AccessibleObject#setAccessible
	 * @since 10223
	 */
	public static void setObjectsAccessible(final AccessibleObject... objects) {
		if (objects != null && objects.length > 0) {
			AccessController.doPrivileged((PrivilegedAction<Object>) () -> {
				for (AccessibleObject o : objects) {
					o.setAccessible(true);
				}
				return null;
			});
		}
	}

}
