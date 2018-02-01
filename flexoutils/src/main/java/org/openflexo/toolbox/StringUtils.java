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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

public class StringUtils {

	private static final char[] DEFAULT_STRIP = { '\u200B', '\uFEFF' };

	/*
		public static String getString(InputStream is, String encoding) throws IOException {
			BufferedReader br = new BufferedReader(new InputStreamReader(is, encoding));
			StringWriter sw = new StringWriter();
			String line = null;
			while ((line = br.readLine()) != null) {
				sw.write(line);
				sw.write(LINE_SEPARATOR);
			}
			return sw.toString();
		}
	
	public static String reverse(String s) {
		if (s == null) {
			return s;
		}
		StringBuilder sb = new StringBuilder(s.length());
		for (int i = s.length(); i > 0; i--) {
			sb.append(s.charAt(i - 1));
		}
		return sb.toString();
	}
	
	public static String circularOffset(String s, int offset) {
		if (offset == 0) {
			return s;
		}
		if (s.length() != 0) {
			offset = offset % s.length();
		}
		StringBuilder sb = new StringBuilder(s.length());
		for (int i = 0; i < s.length(); i++) {
			int location = (i + offset) % s.length();
			if (location < 0) {
				location += s.length();
			}
			sb.append(s.charAt(location));
		}
		return sb.toString();
	}
	*/

	public static String convertAccents(String s) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			switch (c) {
				case 'Á':
				case 'À':
				case 'Â':
				case 'Ä':
				case 'Ã':
				case 'Å':
					sb.append('A');
					break;
				case 'É':
				case 'È':
				case 'Ê':
				case 'Ë':
					sb.append('E');
					break;
				case 'Í':
				case 'Ì':
				case 'Î':
				case 'Ï':
					sb.append('I');
					break;
				case 'Ó':
				case 'Ò':
				case 'Ô':
				case 'Ö':
				case 'Õ':
				case 'Ø':
					sb.append('O');
					break;
				case 'Ú':
				case 'Ù':
				case 'Û':
				case 'Ü':
					sb.append('U');
					break;
				case 'Ç':
					sb.append('C');
					break;
				case 'Ñ':
					sb.append('N');
					break;
				case 'á':
				case 'à':
				case 'â':
				case 'ä':
				case 'ã':
				case 'å':
					sb.append('a');
					break;
				case 'é':
				case 'è':
				case 'ê':
				case 'ë':
					sb.append('e');
					break;
				case 'í':
				case 'ì':
				case 'î':
				case 'ï':
					sb.append('i');
					break;
				case 'ó':
				case 'ò':
				case 'ô':
				case 'ö':
				case 'õ':
				case 'ø':
					sb.append('o');
					break;
				case 'ú':
				case 'ù':
				case 'û':
				case 'ü':
					sb.append('u');
					break;
				case 'ý':
				case 'ÿ':
					sb.append('y');
					break;
				case 'ç':
					sb.append('c');
					break;
				case 'ñ':
					sb.append('n');
					break;
				default:
					sb.append(c);
			}
		}
		return sb.toString();
	}

	/*
	public static String replaceNonMatchingPatterns(String string, String regexp, String replacement) {
		return replaceNonMatchingPatterns(string, regexp, replacement, false);
	}
	
	public static String replaceNonMatchingPatterns(String string, String regexp, String replacement, boolean replaceEachCharacter) {
		if (string == null || string.length() == 0) {
			return string;
		}
		StringBuilder sb = new StringBuilder();
		Matcher m = Pattern.compile(regexp).matcher(string);
		int last = 0;
		while (m.find()) {
			if (replaceEachCharacter) {
				for (int i = last; i < m.start(); i++) {
					sb.append(replacement);
				}
			}
			else if (last != m.start()) {
				sb.append(replacement);
			}
			sb.append(m.group());
			last = m.end();
		}
		if (replaceEachCharacter) {
			for (int i = last; i < string.length(); i++) {
				sb.append(replacement);
			}
		}
		else if (last != string.length()) {
			sb.append(replacement);
		}
		return sb.toString();
	}
	
	public static Hashtable<String, String> getQueryFromURL(URL url) {
		if (url == null || url.getQuery() == null) {
			return new Hashtable<>();
		}
		Hashtable<String, String> returned = new Hashtable<>();
		StringTokenizer st = new StringTokenizer(url.getQuery(), "&");
		while (st.hasMoreTokens()) {
			StringTokenizer subSt = new StringTokenizer(st.nextToken(), "=");
			String key = null, value = null;
			if (subSt.hasMoreTokens()) {
				key = subSt.nextToken();
			}
			if (subSt.hasMoreTokens()) {
				value = subSt.nextToken();
			}
			if (key != null && value != null) {
				returned.put(key, value);
			}
		}
		return returned;
	}
	*/

	public static int countMatches(String str, String sub) {
		if (isEmpty(str) || isEmpty(sub)) {
			return 0;
		}
		int count = 0;
		int idx = 0;
		while ((idx = str.indexOf(sub, idx)) != -1) {
			count++;
			idx += sub.length();
		}
		return count;
	}

	public static boolean isSame(String str1, String str2) {
		if (str1 == null) {
			return str2 == null;
		}
		return str1.equals(str2);
	}

	public static boolean isEmpty(String str) {
		return str == null || str.length() == 0;
	}

	public static boolean isNotEmpty(String str) {
		return str != null && str.length() > 0;
	}

	/**
	 * Compute number of lines of a string (use \n)
	 */
	public static int linesNb(String aString) {
		int returned = 0;
		try (BufferedReader rdr = new BufferedReader(new StringReader(aString))) {
			for (;;) {
				String line = null;
				try {
					line = rdr.readLine();
				} catch (IOException e) {
					e.printStackTrace();
				}
				if (line == null) {
					break;
				}
				returned++;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return returned;
	}

	public static final String LINE_SEPARATOR = "\n";/*System.getProperty("line.separator");*/
	/*
		public static String extractStringFromLine(String aString, int lineNb) {
			StringBuilder sb = new StringBuilder();
			int n = 0;
			for (int i = 0; i < aString.length(); i++) {
				char c = aString.charAt(i);
				if (n >= lineNb) {
					sb.append(c);
				}
				if (c == '\n') {
					n++;
				}
				if (c == '\r' && i + 1 < aString.length() && aString.charAt(i + 1) != '\n') {
					n++;
				}
			}
			return sb.toString();
		}
	
	public static String extractStringFromLineOld(String aString, int lineNb) {
		StringBuffer returned = new StringBuffer();
		int n = 0;
		BufferedReader rdr = new BufferedReader(new StringReader(aString));
		for (;;) {
			String line = null;
			try {
				line = rdr.readLine();
			} catch (IOException e) {
				e.printStackTrace();
			}
			if (line == null) {
				break;
			}
			if (n >= lineNb) {
				returned.append((n > lineNb ? LINE_SEPARATOR : "") + line);
			}
			n++;
		}
		return returned.toString();
	}
	*/

	public static String extractStringAtLine(String aString, int lineNb) {
		int n = 0;
		BufferedReader rdr = new BufferedReader(new StringReader(aString));
		for (;;) {
			String line = null;
			try {
				line = rdr.readLine();
			} catch (IOException e) {
				e.printStackTrace();
			}
			if (line == null) {
				break;
			}
			if (n == lineNb) {
				return line;
			}
			n++;
		}
		return null;
	}

	/*
		public static String extractWhiteSpace(String aString) {
			if (aString == null) {
				return null;
			}
			int index = 0;
			while (index < aString.length() && aString.charAt(index) < ' ') {
				index++;
			}
			return aString.substring(0, index);
		}
	*/
	public static String buildString(char c, int length) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < length; i++) {
			sb.append(c);
		}
		return sb.toString();
	}

	public static String buildWhiteSpaceIndentation(int length) {
		return buildString(' ', length);
	}

	/*
		public static int indexOfEscapingJava(char searchedChar, String someJavaCode) {
			int parentLevel = 0; // ()
	int bracketLevel = 0; // [] 
	int curlyLevel = 0; // {} 
	
	int index = 0;
	
	while(index<someJavaCode.length())
	{
		char current = someJavaCode.charAt(index);
		if (current == '(') {
			parentLevel++;
		}
		if (current == ')') {
			parentLevel--;
		}
		if (current == '[') {
			bracketLevel++;
		}
		if (current == ']') {
			bracketLevel--;
		}
		if (current == '{') {
			curlyLevel++;
		}
		if (current == '}') {
			curlyLevel--;
		}
		if (parentLevel == 0 && bracketLevel == 0 && curlyLevel == 0 && current == searchedChar) {
			return index;
		}
		index++;
	}
	
	return-1;
	}
	
	public static String replaceBreakLinesBy(String value, String replacement) {
		return value.replaceAll("(\r\n|\r|\n|\n\r)", replacement);
	}
	*/
	/**
	 * Returns the specified string into "camel case" : each word are appended without white-spaces, but with a capital letter.<br>
	 * Note that, except for the first word, if the whole word is uppercase, it will be converted into lowercase.
	 * 
	 * Example : "Todo list" =&gt; "TodoList"; "DAO controller" =&gt; "daoController".
	 * 
	 * @param firstUpper
	 *            {@code true} if the first letter has to be uppercase.
	 * @param string
	 *            the string to transform into camel case
	 * @return the camel case string
	 */
	public static String camelCase(String string, boolean firstUpper) {
		if (string == null) {
			return null;
		}
		String value = string.trim().replace('_', ' ');
		if (value.trim().length() == 0) {
			return value;
		}
		if (value.equals(value.toUpperCase())) {
			value = value.toLowerCase();
		}
		StringBuilder result = new StringBuilder(value.length());

		String[] words = value.split(" ");

		// First word
		if (words[0].equals(words[0].toUpperCase())) {
			if (firstUpper) {
				result.append(words[0]);// If the first word is upper case, and first letter must be uppercase, we keep all the word
				// uppercase.
			}
			else {
				result.append(words[0].toLowerCase());// If the first word is upper case, and first letter must be lowercase, we set all the
				// word lowercase.
			}
		}
		else {
			if (firstUpper) {
				result.append(firstUpper(words[0]));
			}
			else {
				result.append(firstsLower(words[0]));
			}
		}

		// Other words
		for (int i = 1; i < words.length; i++) {
			if (words[i].equals(words[i].toUpperCase())) {
				result.append(words[i]);
			}
			else {
				result.append(firstUpper(words[i]));
			}
		}

		return result.toString();
	}

	/**
	 * Sets the first char into upper case.
	 * 
	 * @param value
	 *            the string to transform.
	 * @return the same string with the first char upper case.
	 */
	private static String firstUpper(String value) {
		if (value == null) {
			return null;
		}
		if (value.length() < 2) {
			return value.toUpperCase();
		}
		return value.substring(0, 1).toUpperCase() + value.substring(1);
	}

	/**
	 * Sets the first char into lower case.<br>
	 * If the word has more than its first letter in upper case, the consecutive next upper case letters are also converted into lower case.
	 * 
	 * @param value
	 *            the string to convert.
	 * @return the same string, with its first upper case letters converted into lower case.
	 */
	public static String firstsLower(String value) {
		if (value == null) {
			return null;
		}

		if (value.length() == 0) {
			return value;
		}

		int indexOfUpperCase = -1;
		for (char c : value.toCharArray()) {
			if (!Character.isUpperCase(c)) {
				break;
			}
			indexOfUpperCase++;
		}

		if (indexOfUpperCase <= 0) {
			indexOfUpperCase = 1;
		}

		return value.substring(0, indexOfUpperCase).toLowerCase()
				+ (value.length() > indexOfUpperCase ? value.substring(indexOfUpperCase) : "");
	}

	/**
	 * Try to lookup the best enum value for a given string
	 * 
	 * @param valueAsString
	 * @param enumType
	 * @param minimumMatchingChars
	 * @return
	 */
	/*
	public static <E extends Enum<?>> E getBestEnumValue(String valueAsString, Class<? extends E> enumType, int minimumMatchingChars) {
		if (StringUtils.isEmpty(valueAsString)) {
			return null;
		}
		String searchedString = JavaUtils.getVariableName(valueAsString.trim()).toUpperCase();
		int bestMatchingChars = 0;
		E bestValue = null;
		for (E e : enumType.getEnumConstants()) {
			String enumValueAsString = JavaUtils.getVariableName(e.name().trim()).toUpperCase();
			int matchingChars = matchingChars(searchedString, enumValueAsString);
			if (matchingChars > bestMatchingChars && matchingChars >= minimumMatchingChars) {
				bestMatchingChars = matchingChars;
				bestValue = e;
			}
		}
		return bestValue;
	}
	*/
	/**
	 * A quick and dirty method used to lookup a string in an other one<br>
	 * Return number of matchings chars, asserting that first char lookup wil be the optimum (this is generally not the case)
	 * 
	 * @param s1
	 * @param s2
	 * @return
	 */

	/*
	public static int matchingChars(String s1, String s2) {
		if (s1.length() > s2.length()) {
			return matchingChars(s2, s1);
		}
		int s1StartIndex = 0;
		int s2StartIndex = s2.indexOf(s1.charAt(s1StartIndex));
		while (s2StartIndex == -1 && s1StartIndex < s1.length() - 1) {
			s1StartIndex++;
			s2StartIndex = s2.indexOf(s1.charAt(s1StartIndex));
		}
		if (s2StartIndex > -1) {
			int returned = 0;
			for (int i = s1StartIndex; i < s1.length() && i - s1StartIndex + s2StartIndex < s2.length(); i++) {
				if (s1.charAt(i) == s2.charAt(i - s1StartIndex + s2StartIndex)) {
					returned++;
				}
			}
			return returned;
		}
		return 0;
	}
	*/
	/**
	 * Returns true if the name have one of the given extensions. It supports multiple extensions ('.foo.bar').
	 * 
	 * @param name
	 *            the name to test
	 * @param possibleExtensions
	 *            the possible extensions in <b>lower case</b>.
	 * @return true if one extension is found, false otherwise.
	 */

	/*
	public static boolean hasExtension(String name, String... possibleExtensions) {
		name = name.toLowerCase();
	
		int dotIndex = name.indexOf('.');
		while (dotIndex >= 0) {
	
			String extension = name.substring(dotIndex);
			for (String possibleExtension : possibleExtensions) {
				if (possibleExtension.equals(extension))
					return true;
			}
			dotIndex = name.indexOf('.', dotIndex + 1);
		}
		return false;
	}
	*/
	/**
	 * Computes levebshtein distance between 2 strings<br>
	 * Original code from https://en.wikibooks.org/wiki/Algorithm_Implementation/Strings/Levenshtein_distance<br>
	 * See https://creativecommons.org/licenses/by-sa/3.0/
	 */
	public static int levenshteinDistance(CharSequence lhs, CharSequence rhs) {
		int len0 = lhs.length() + 1;
		int len1 = rhs.length() + 1;

		// the array of distances
		int[] cost = new int[len0];
		int[] newcost = new int[len0];

		// initial cost of skipping prefix in String s0
		for (int i = 0; i < len0; i++)
			cost[i] = i;

		// dynamically computing the array of distances

		// transformation cost for each letter in s1
		for (int j = 1; j < len1; j++) {
			// initial cost of skipping prefix in String s1
			newcost[0] = j;

			// transformation cost for each letter in s0
			for (int i = 1; i < len0; i++) {
				// matching current letters in both strings
				int match = (lhs.charAt(i - 1) == rhs.charAt(j - 1)) ? 0 : 1;

				// computing cost for each transformation
				int cost_replace = cost[i - 1] + match;
				int cost_insert = cost[i] + 1;
				int cost_delete = newcost[i - 1] + 1;

				// keep minimum cost
				newcost[i] = Math.min(Math.min(cost_insert, cost_delete), cost_replace);
			}

			// swap cost/newcost arrays
			int[] swap = cost;
			cost = newcost;
			newcost = swap;
		}

		// the distance is the cost for transforming all letters in both strings
		return cost[len0 - 1];
	}

	/**
	 * An alternative to {@link String#trim()} to effectively remove all leading and trailing white characters, including Unicode ones.
	 * 
	 * @param str
	 *            The string to strip
	 * @return <code>str</code>, without leading and trailing characters, according to {@link Character#isWhitespace(char)} and
	 *         {@link Character#isSpaceChar(char)}.
	 * @see <a href="http://closingbraces.net/2008/11/11/javastringtrim/">Java String.trim has a strange idea of whitespace</a>
	 * @see <a href="https://bugs.openjdk.java.net/browse/JDK-4080617">JDK bug 4080617</a>
	 * @see <a href="https://bugs.openjdk.java.net/browse/JDK-7190385">JDK bug 7190385</a>
	 * @since 5772
	 */
	public static String strip(final String str) {
		if (str == null || str.isEmpty()) {
			return str;
		}
		return strip(str, DEFAULT_STRIP);
	}

	/**
	 * An alternative to {@link String#trim()} to effectively remove all leading and trailing white characters, including Unicode ones.
	 * 
	 * @param str
	 *            The string to strip
	 * @param skipChars
	 *            additional characters to skip
	 * @return <code>str</code>, without leading and trailing characters, according to {@link Character#isWhitespace(char)},
	 *         {@link Character#isSpaceChar(char)} and skipChars.
	 * @since 8435
	 */
	/*
	public static String strip(final String str, final String skipChars) {
		if (str == null || str.isEmpty()) {
			return str;
		}
		return strip(str, stripChars(skipChars));
	}
	*/
	private static String strip(final String str, final char... skipChars) {

		int start = 0;
		int end = str.length();
		boolean leadingSkipChar = true;
		while (leadingSkipChar && start < end) {
			leadingSkipChar = isStrippedChar(str.charAt(start), skipChars);
			if (leadingSkipChar) {
				start++;
			}
		}
		boolean trailingSkipChar = true;
		while (trailingSkipChar && end > start + 1) {
			trailingSkipChar = isStrippedChar(str.charAt(end - 1), skipChars);
			if (trailingSkipChar) {
				end--;
			}
		}

		return str.substring(start, end);
	}

	private static boolean isStrippedChar(char c, final char... skipChars) {
		return Character.isWhitespace(c) || Character.isSpaceChar(c) || stripChar(skipChars, c);
	}

	/*
		private static char[] stripChars(final String skipChars) {
			if (skipChars == null || skipChars.isEmpty()) {
				return DEFAULT_STRIP;
			}
	
			char[] chars = new char[DEFAULT_STRIP.length + skipChars.length()];
			System.arraycopy(DEFAULT_STRIP, 0, chars, 0, DEFAULT_STRIP.length);
			skipChars.getChars(0, skipChars.length(), chars, DEFAULT_STRIP.length);
	
			return chars;
		}
	*/
	private static boolean stripChar(final char[] strip, char c) {
		for (char s : strip) {
			if (c == s) {
				return true;
			}
		}
		return false;
	}

}
