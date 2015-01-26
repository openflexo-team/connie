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

package org.openflexo.search;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

import org.openflexo.search.TextQueryResult.Result;

public class TextQueryEngine {

	public static TextQueryResult performSearchOnDocument(Document document, TextQuery query) {
		try {
			return performSearchInText(document.getText(0, document.getLength()), query, document);
		} catch (BadLocationException e) {
			e.printStackTrace();// Should never happen
			return null;
		}
	}

	/**
	 * 
	 * @param text
	 * @param query
	 * @param original
	 *            the original document, if any. This argument can be null.
	 * @return
	 */
	public static TextQueryResult performSearchInText(String text, TextQuery query, Document original) {
		if (text == null) {
			return null;
		}
		TextQueryResult result = original != null ? new TextQueryResult(query, original) : new TextQueryResult(query, text);
		if (query.getSearchedText() == null || query.getSearchedText().length() == 0) {
			return result;
		}
		if (query.isRegularExpression()) {
			int flag = Pattern.DOTALL;
			if (!query.isCaseSensitive()) {
				flag |= Pattern.CASE_INSENSITIVE;
			}
			Pattern pattern = Pattern.compile(query.getSearchedText(), flag);
			Matcher m = pattern.matcher(text);
			while (m.find()) {
				Result r = result.new Result(m.start(), m.end());
				result.addToResults(r);
			}
			return result;
		} else {
			for (int i = 0; i < text.length(); i++) {
				if (text.regionMatches(!query.isCaseSensitive(), i, query.getSearchedText(), 0, query.getSearchedText().length())) {
					if (query.isWholeWord()
							&& (i == 0 || !(Character.isLetterOrDigit(text.charAt(i - 1)) || text.charAt(i - 1) == '_'))
							&& (i + query.getSearchedText().length() == text.length() || !(Character.isLetterOrDigit(text.charAt(i
									+ query.getSearchedText().length())) || text.charAt(i + query.getSearchedText().length()) == '_'))
							|| !query.isWholeWord()) {
						Result r = result.new Result(i, i + query.getSearchedText().length());
						result.addToResults(r);
					}
				}
			}
			return result;
		}
	}

}
