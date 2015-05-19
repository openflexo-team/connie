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

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DateUtils {
	private static final String[] ACCEPTABLEDATEFORMAT = { "d/M/y", "d/M/y H'h'm", "d/M/y H:m", "d-M-y", "d-M-y H'h'm", "d-M-y H:m",
			"M/d/y", "M/d/y H'h'm", "M/d/y H:m", "M-d-y", "M-d-y H'h'm", "M-d-y H:m" };

	/**
	 * Parse all specified String values and returns them as a date array. This method try to detect the date format to use and ensure the
	 * same will be used for all values. <br>
	 * If a common date format cannot be found, null is returned.
	 * 
	 * @param values the strings to parse
	 * @return all specified String values as Date, null if they cannot be converted.
	 */
	public static Date[] parseDate(String[] values) {
		SimpleDateFormat dateFormat = new SimpleDateFormat();
		dateFormat.setLenient(false);
		ParsePosition pos = new ParsePosition(0);
		for (String pattern : ACCEPTABLEDATEFORMAT) {
			dateFormat.applyPattern(pattern);
			List<Date> results = new ArrayList<Date>();

			for (String value : values) {
				pos.setIndex(0);
				Date date = dateFormat.parse(value.trim(), pos);
				if (date == null || pos.getIndex() != value.length()) {
					break;
				}
				results.add(date);
			}

			if (results.size() == values.length) {
				return results.toArray(new Date[values.length]);
			}
		}

		return null;
	}

	public static Date parseDate(String value) {
		Date[] dates = parseDate(new String[] { value });
		if (dates != null && dates.length > 0) {
			return dates[0];
		}
		return null;
	}
}
