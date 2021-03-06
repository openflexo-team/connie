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

package org.openflexo.logging;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

import org.openflexo.toolbox.ToolBox;

/**
 * Utility class used to format logs of Flexo
 * 
 * @author sylvain
 */
public class FlexoLoggingFormatter extends Formatter {

	private final DateFormat dateFormat = new SimpleDateFormat("dd/MM/yy HH:mm:ss,SSS");

	public static boolean logDate = true;

	@Override
	public String format(LogRecord log) {
		StringBuffer sb = new StringBuffer();
		sb.append(formatString(logDate ? 30 : 10,
				log.getLevel().toString() + (logDate ? " " + dateFormat.format(new Date(log.getMillis())) : "")));
		sb.append(formatString(100, log.getMessage()));
		sb.append(formatString(50, "[" + log.getSourceClassName() + "." + log.getSourceMethodName() + "]"));
		sb.append("\n");
		if (log.getThrown() != null) {
			sb.append(ToolBox.stackTraceAsAString(log.getThrown()));
		}
		return sb.toString();
	}

	public static String formatString(int cols, String aString) {
		char[] blank;
		if (aString == null) {
			aString = "null";
		}
		if (cols > aString.length()) {
			blank = new char[cols - aString.length()];
			for (int i = 0; i < cols - aString.length(); i++) {
				blank[i] = ' ';
			}
			return aString + new String(blank);
		}
		return aString;
	}
}
